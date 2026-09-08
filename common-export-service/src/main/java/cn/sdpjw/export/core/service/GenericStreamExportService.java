package cn.sdpjw.export.core.service;


import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.export.config.ExportConfigProperties;
import cn.sdpjw.export.core.callback.ProgressCallback;
import cn.sdpjw.export.core.enricher.DataEnricher;
import cn.sdpjw.export.core.mapper.CsvFieldMapper;
import cn.sdpjw.export.core.provider.CountProvider;
import cn.sdpjw.export.core.result.ExportResponse;
import lombok.Getter;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import javax.annotation.PreDestroy;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 通用流式导出服务（已修复：SingleFileModeHandler 使用 sheet.getLastRowNum() 管理行号）
 *
 * @param <T> 实体类型
 */
@Slf4j
@Service
public class GenericStreamExportService<T> {

    @Autowired
    private ExportConfigProperties configProperties;

    private final TransactionTemplate transactionTemplate;
    private final ZipFileService zipFileService;
    
    /**
     * 数据增强专用线程池（用于超时控制）
     * 使用固定大小的线程池，避免创建过多线程
     */
    private final ExecutorService enrichExecutorService = Executors.newFixedThreadPool(5);

    @Autowired
    public GenericStreamExportService(PlatformTransactionManager transactionManager,
                                      ZipFileService zipFileService) {
        // 创建只读事务模板
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setReadOnly(true);
        this.zipFileService = zipFileService;
    }
    
    /**
     * 服务销毁时关闭线程池
     */
    @PreDestroy
    public void destroy() {
        if (enrichExecutorService != null && !enrichExecutorService.isShutdown()) {
            enrichExecutorService.shutdown();
            try {
                // 等待正在执行的任务完成，最多等待30秒
                if (!enrichExecutorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    // 如果30秒后还有任务在执行，强制关闭
                    enrichExecutorService.shutdownNow();
                    log.warn("数据增强线程池强制关闭");
                }
            } catch (InterruptedException e) {
                enrichExecutorService.shutdownNow();
                Thread.currentThread().interrupt();
                log.warn("数据增强线程池关闭时被中断", e);
            }
        }
    }

    /**
     * 执行流式导出（使用 ResultHandler 实现真正的流式处理）
     */
    public ExportResponse<String> executeExport(
            String taskId,
            java.util.function.Consumer<ResultHandler<T>> resultHandlerProvider,
            CountProvider countProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            ProgressCallback progressCallback,
            String exportDir,
            String tempDir,
            long totalRows
    ) throws IOException {
        return executeExport(taskId, resultHandlerProvider, countProvider, fieldMapper,
                fileNamePrefix, progressCallback, exportDir, tempDir, totalRows, null);
    }

    /**
     * 执行流式导出（使用 ResultHandler 实现真正的流式处理）
     * 
     * @param dataEnricher 数据增强器（可选，用于补充第三方业务模块数据）
     */
    public ExportResponse<String> executeExport(
            String taskId,
            java.util.function.Consumer<ResultHandler<T>> resultHandlerProvider,
            CountProvider countProvider,
            CsvFieldMapper<T> fieldMapper,
            String fileNamePrefix,
            ProgressCallback progressCallback,
            String exportDir,
            String tempDir,
            long totalRows,
            DataEnricher<T, T> dataEnricher
    ) throws IOException {
        log.info("1. 执行导出");
        // 使用默认目录或传入的目录
        String finalExportDir = exportDir != null ? exportDir : configProperties.getExportDir();
        String finalTempDir = tempDir != null ? tempDir : (configProperties.getTempDir() + "/" + taskId);

        // 如果总数为0，直接返回未查询到数据
        if (totalRows == 0) {
            log.info("导出任务无数据: taskId={}", taskId);
            return ExportResponse.noData("未查询到数据");
        }

        log.info("开始导出任务: taskId={}, 总行数={}", taskId, totalRows);

        // 2. 创建临时目录
        Files.createDirectories(Paths.get(finalTempDir));

        // 3. 流式查询并写入CSV（在事务中执行，真正的流式处理）
        List<String> csvFiles = transactionTemplate.execute(status -> {
            try {
                return processStreamingExportWithHandler(
                        resultHandlerProvider,
                        fieldMapper,
                        finalTempDir,
                        taskId,
                        totalRows,
                        progressCallback,
                        dataEnricher
                );
            } catch (IOException e) {
                throw new BusinessException(e.getMessage());
            }
        });

        // 如果未生成任何CSV文件，返回未查询到数据
        if (csvFiles == null || csvFiles.isEmpty()) {
            log.info("导出任务未生成文件: taskId={}", taskId);
            cleanupTempFiles(finalTempDir);
            return ExportResponse.noData("未查询到数据");
        }

        // 4. 压缩处理
        String finalFilePath = handleCompression(csvFiles, taskId, fileNamePrefix, finalExportDir);

        log.info("导出任务完成: taskId={}, 文件={}", taskId, finalFilePath);

        return ExportResponse.success(finalFilePath, "导出成功");
    }

    private List<String> processStreamingExportWithHandler(
            java.util.function.Consumer<ResultHandler<T>> resultHandlerProvider,
            CsvFieldMapper<T> fieldMapper,
            String tempDir,
            String taskId,
            long totalRows,
            ProgressCallback progressCallback,
            DataEnricher<T, T> dataEnricher
    ) throws IOException {
        log.info("3. 流式查询并写入CSV（在事务中执行，真正的流式处理）");
        boolean zipEnabled = configProperties.isZipEnabled();
        int csvMaxRows = configProperties.getCsvMaxRows();
        int batchSize = configProperties.getBatchSize();

        log.info("开始流式查询数据，准备写入CSV文件... zipEnabled={}, csvMaxRows={}, batchSize={}, totalRows={}",
                zipEnabled, csvMaxRows, batchSize, totalRows);

        List<String> csvFiles;
        long processedRows;

        if (zipEnabled) {
            ZipModeHandler zipHandler = createZipModeHandler(fieldMapper, tempDir, taskId, totalRows,
                    csvMaxRows, batchSize, progressCallback);
            resultHandlerProvider.accept(zipHandler);
            zipHandler.finish();
            csvFiles = zipHandler.getCsvFiles();
            processedRows = zipHandler.getProcessedRows();
        } else {
            SingleFileModeHandler singleFileHandler = createSingleFileModeHandler(fieldMapper, tempDir, taskId, totalRows,
                    csvMaxRows, batchSize, progressCallback, dataEnricher);
            resultHandlerProvider.accept(singleFileHandler);
            singleFileHandler.finish();
            csvFiles = singleFileHandler.getCsvFiles();
            processedRows = singleFileHandler.getProcessedRows();
        }

        log.info("流式查询完成，共处理{}行数据，生成{}个文件", processedRows, csvFiles.size());

        return csvFiles;
    }

    private ZipModeHandler createZipModeHandler(
            CsvFieldMapper<T> fieldMapper,
            String tempDir,
            String taskId,
            long totalRows,
            int csvMaxRows,
            int batchSize,
            ProgressCallback progressCallback) {
        return new ZipModeHandler(fieldMapper, tempDir, taskId, totalRows,
                csvMaxRows, batchSize, progressCallback);
    }

    private SingleFileModeHandler createSingleFileModeHandler(
            CsvFieldMapper<T> fieldMapper,
            String tempDir,
            String taskId,
            long totalRows,
            int csvMaxRows,
            int batchSize,
            ProgressCallback progressCallback,
            DataEnricher<T, T> dataEnricher) {
        return new SingleFileModeHandler(fieldMapper, tempDir, taskId, totalRows,
                csvMaxRows, batchSize, progressCallback, dataEnricher);
    }

    private class ZipModeHandler implements ResultHandler<T> {
        private final CsvFieldMapper<T> fieldMapper;
        private final String tempDir;
        private final String taskId;
        private final long totalRows;
        private final int csvMaxRows;
        private final int batchSize;
        private final ProgressCallback progressCallback;

        @Getter
        private final List<String> csvFiles = new ArrayList<>();
        @Getter
        private long processedRows = 0;
        private long currentFileRows = 0;
        private int fileIndex = 1;

        private final List<T> batch = new ArrayList<>();
        private Writer writer = null;
        private CSVPrinter csvPrinter = null;
        private String currentCsvFile = null;

        public ZipModeHandler(CsvFieldMapper<T> fieldMapper, String tempDir, String taskId,
                              long totalRows, int csvMaxRows, int batchSize,
                              ProgressCallback progressCallback) {
            this.fieldMapper = fieldMapper;
            this.tempDir = tempDir;
            this.taskId = taskId;
            this.totalRows = totalRows;
            this.csvMaxRows = csvMaxRows;
            this.batchSize = batchSize;
            this.progressCallback = progressCallback;
        }

        @Override
        public void handleResult(ResultContext<? extends T> resultContext) {
            T entity = resultContext.getResultObject();
            if (entity == null) {
                return;
            }

            try {
                boolean needNewFile = false;

                if (writer == null) {
                    needNewFile = true;
                } else if (currentFileRows >= csvMaxRows) {
                    needNewFile = true;
                }

                if (needNewFile) {
                    closeCurrentFile();
                    currentCsvFile = createCsvFile(tempDir, fileIndex);
                    csvFiles.add(currentCsvFile);
                    fileIndex++;
                    writer = createWriter(currentCsvFile);
                    csvPrinter = createCsvPrinter(writer, fieldMapper.getHeaders());
                    currentFileRows = 0;
                    log.info("ZIP模式：创建新CSV文件: {}", currentCsvFile);
                }

                batch.add(entity);

                if (batch.size() >= batchSize) {
                    writeBatch(batch, csvPrinter, fieldMapper);
                    processedRows += batch.size();
                    currentFileRows += batch.size();

                    if (currentFileRows >= csvMaxRows) {
                        if (writer != null) {
                            writer.flush();
                        }
                        closeCurrentFile();
                        currentFileRows = 0;
                        log.info("ZIP模式：当前文件已达到最大行数限制({})，下次将创建新文件", csvMaxRows);
                    } else {
                        if (writer != null) {
                            writer.flush();
                        }
                    }

                    batch.clear();

                    if (progressCallback != null) {
                        progressCallback.onProgress(taskId, processedRows, totalRows);
                    }

                    log.debug("ZIP模式：已写入{}行数据到文件: {}, 当前文件行数: {}",
                            processedRows, currentCsvFile, currentFileRows);
                }
            } catch (IOException e) {
                throw new BusinessException("写入CSV文件失败：" + e.getMessage());
            }
        }

        private void closeCurrentFile() throws IOException {
            if (csvPrinter != null) {
                csvPrinter.close();
                csvPrinter = null;
            }
            if (writer != null) {
                writer.close();
                writer = null;
            }
        }

        public void finish() throws IOException {

            if (!batch.isEmpty() && csvPrinter != null) {
                writeBatch(batch, csvPrinter, fieldMapper);
                processedRows += batch.size();
                currentFileRows += batch.size();
                if (writer != null) {
                    writer.flush();
                }
                batch.clear();
                if (progressCallback != null) {
                    progressCallback.onProgress(taskId, processedRows, totalRows);
                }
            }
            closeCurrentFile();
        }

    }

    private class SingleFileModeHandler implements ResultHandler<T> {
        private final CsvFieldMapper<T> fieldMapper;
        private final String taskId;
        private final long totalRows;
        private final int csvMaxRows;
        private final int batchSize;
        private final ProgressCallback progressCallback;
        private final DataEnricher<T, T> dataEnricher;
        private boolean currentSheetHasData = false;
        @Getter
        private final List<String> csvFiles = new ArrayList<>();
        @Getter
        private long processedRows = 0;
        private int sheetIndex = 1;  // 当前sheet索引

        private final List<T> batch = new ArrayList<>();
        private SXSSFWorkbook workbook = null;
        private SXSSFSheet currentSheet = null;
        private CellStyle headerStyle = null;
        private String singleExcelFile = null;

        public SingleFileModeHandler(CsvFieldMapper<T> fieldMapper, String tempDir, String taskId,
                                     long totalRows, int csvMaxRows, int batchSize,
                                     ProgressCallback progressCallback,
                                     DataEnricher<T, T> dataEnricher) {
            this.fieldMapper = fieldMapper;
            this.taskId = taskId;
            this.totalRows = totalRows;
            this.csvMaxRows = csvMaxRows;
            this.batchSize = batchSize;
            this.progressCallback = progressCallback;
            this.dataEnricher = dataEnricher;

            // 预先创建单个文件路径（XLSX格式）
            singleExcelFile = tempDir + "/" + taskId + ".xlsx";
            csvFiles.add(singleExcelFile);
        }

        @Override
        public void handleResult(ResultContext<? extends T> resultContext) {
            T entity = resultContext.getResultObject();
            if (entity == null) {
                return;
            }

            try {
                ensureInitialized();

                batch.add(entity);

                // 将 batch 写入（考虑 batch 可能跨越多个 sheet）
                if (batch.size() >= batchSize) {
                    flushBatchToSheets();

                }

            } catch (Exception e) {
                throw new BusinessException("写入 XLSX 出错：" + e.getMessage());
            }
        }

        /**
         * 确保 workbook 与首个 sheet 已初始化并写入表头
         */
        private void ensureInitialized() {
            if (workbook == null) {
                workbook = new SXSSFWorkbook(100);
                sheetIndex = 1;
                currentSheet = workbook.createSheet("Sheet" + sheetIndex);
                writeHeader(currentSheet, fieldMapper.getHeaders());
                log.info("初始化 XLSX，创建 Sheet{}", sheetIndex);
            }
        }

        /**
         * 把当前 batch 刷入 sheet（会处理跨 sheet 的场景）
         */
        private void flushBatchToSheets() {
            int idx = 0;
            while (idx < batch.size()) {
                int dataRowsWritten = getDataRowCount(currentSheet); // 当前 sheet 已写入的数据行数（不含表头）
                int remainingRows = csvMaxRows - dataRowsWritten;
                if (remainingRows <= 0) {
                    // 当前 sheet 无可写数据行，先创建新 sheet
                    createNewSheet();
                    continue;
                }

                int toWrite = Math.min(remainingRows, batch.size() - idx);
                List<T> sub = new ArrayList<>(batch.subList(idx, idx + toWrite));
                
                // 数据增强处理：在写入之前进行数据增强（如果配置了enricher）
                List<T> enrichedSub = safeEnrich(sub, dataEnricher);
                
                writeBatchPortion(enrichedSub, currentSheet);
                processedRows += sub.size();
                idx += toWrite;

                if (progressCallback != null) {
                    progressCallback.onProgress(taskId, processedRows, totalRows);
                }

                // 如果写满当前 sheet，则下一次循环会创建新 sheet
                if (toWrite == remainingRows) {
                    // explicitly create new sheet on next loop
                    createNewSheet();
                }
            }
            batch.clear();
        }

        /**
         * 获取当前 sheet 已写入的数据行数（不包含表头）
         */
        private int getDataRowCount(SXSSFSheet sheet) {
            // 如果只写了表头，lastRowNum = 0 -> dataRows = 0
            int lastRow = sheet.getLastRowNum();
            if (lastRow <= 0) return 0;
            return lastRow; // header at row 0，数据从 row1 开始，lastRow 等于 header + dataRows
        }

        /**
         * 创建新 sheet 并写入表头
         */
        private void createNewSheet() {
            if (currentSheet != null) {
                autoSizeColumns(currentSheet, fieldMapper.getHeaders().length);
            }
            sheetIndex++;
            currentSheet = workbook.createSheet("Sheet" + sheetIndex);
            writeHeader(currentSheet, fieldMapper.getHeaders());
            currentSheetHasData = false;  // <-- 新 sheet 刚建，没有数据
            log.info("单文件模式：创建新sheet: sheetIndex={}", sheetIndex);
        }

        /**
         * 写入表头（表头固定写在第0行）：灰色背景、加粗居中、细边框，并按内容自适应列宽
         */
        private void writeHeader(SXSSFSheet sheet, String[] headers) {
            if (headerStyle == null) {
                headerStyle = createHeaderStyle();
            }
            // SXSSF 需先跟踪列，后续 autoSizeColumn 才能按已写入内容计算宽度
            sheet.trackAllColumnsForAutoSizing();
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            autoSizeColumns(sheet, headers.length);
        }

        /**
         * 表头样式：浅灰底、加粗黑字、水平垂直居中、细黑边框
         */
        private CellStyle createHeaderStyle() {
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLACK.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle style = workbook.createCellStyle();
            style.setFont(headerFont);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            return style;
        }

        /**
         * 按当前已跟踪内容自适应列宽；中文额外补偿，并限制在 Excel 最大列宽内
         */
        private void autoSizeColumns(SXSSFSheet sheet, int columnCount) {
            for (int i = 0; i < columnCount; i++) {
                sheet.autoSizeColumn(i);
                int width = sheet.getColumnWidth(i);
                int paddedWidth = (int) (width * 1.3d) + 512;
                int maxWidth = 255 * 256;
                int minWidth = 8 * 256;
                sheet.setColumnWidth(i, Math.max(minWidth, Math.min(paddedWidth, maxWidth)));
            }
        }

        /**
         * 安全的数据增强方法（异常不会阻断流程，支持超时控制）
         * 
         * @param batch 原始批次数据
         * @param enricher 增强函数（可为null）
         * @return 增强后的数据，如果enricher为null或执行失败，返回原始数据
         */
        @SuppressWarnings("unchecked")
        private List<T> safeEnrich(List<T> batch, DataEnricher<T, T> enricher) {
            // 如果没有提供enricher，直接返回原始数据
            if (enricher == null) {
                return batch;
            }
            
            // 获取超时配置
            int timeoutSeconds = configProperties.getEnrichTimeoutSeconds();
            boolean hasTimeout = timeoutSeconds > 0;
            
            try {
                List<T> enriched;
                
                if (hasTimeout) {
                    // 使用 CompletableFuture 实现超时控制
                    CompletableFuture<List<T>> future = CompletableFuture.supplyAsync(() -> {
                        try {
                            return enricher.enrich(new ArrayList<>(batch));
                        } catch (Exception e) {
                            // 将异常包装为运行时异常，以便在 get() 时抛出
                            throw new BusinessException("数据增强执行异常：" + e.getMessage());
                        }
                    }, enrichExecutorService);
                    
                    try {
                        // 等待执行完成，如果超时则抛出 TimeoutException
                        enriched = future.get(timeoutSeconds, TimeUnit.SECONDS);
                    } catch (TimeoutException e) {
                        // 超时处理：取消任务并使用原始数据
                        future.cancel(true);
                        log.warn("数据增强超时（{}秒），使用原始数据继续导出。batchSize={}", 
                                timeoutSeconds, batch.size());
                        return batch;
                    } catch (java.util.concurrent.ExecutionException e) {
                        // 执行异常：获取原始异常
                        Throwable cause = e.getCause();
                        if (cause instanceof RuntimeException && cause.getCause() != null) {
                            throw (Exception) cause.getCause();
                        }
                        throw new BusinessException("数据增强执行失败：" + cause.getMessage());
                    }
                } else {
                    // 没有配置超时，直接执行
                    enriched = enricher.enrich(new ArrayList<>(batch));
                }
                
                // 如果增强成功但返回null或空列表，使用原始数据
                if (enriched == null || enriched.isEmpty()) {
                    log.warn("数据增强返回空结果，使用原始数据。batchSize={}", batch.size());
                    return batch;
                }
                // 验证增强后的数据数量是否一致
                if (enriched.size() != batch.size()) {
                    log.warn("数据增强后数量不一致（原始:{}，增强后:{}），使用原始数据", 
                            batch.size(), enriched.size());
                    return batch;
                }
                return enriched;
            } catch (Exception e) {
                // 捕获所有异常，记录日志但不阻断流程
                log.error("数据增强失败，使用原始数据继续导出。错误: {}", e.getMessage(), e);
                return batch;
            }
        }

        /**
         * 写入一段 batch 到给定 sheet（不做越界检查）
         */
        private void writeBatchPortion(List<T> portion, SXSSFSheet sheet) {
            String[] headers = fieldMapper.getHeaders();
            int rowNum = sheet.getLastRowNum() + 1; // next free row
            for (T entity : portion) {
                Object[] rowData = fieldMapper.mapToRow(entity);
                Row row = sheet.createRow(rowNum++);
                currentSheetHasData = true;   // <-- 标记此 sheet 已有数据
                for (int i = 0; i < rowData.length && i < headers.length; i++) {
                    Cell cell = row.createCell(i);
                    Object value = rowData[i];
                    if (value == null) {
                        cell.setCellValue("");
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }
        }

        public void finish() throws IOException {
            // 写入剩余数据（可能跨sheet）
            if (!batch.isEmpty()) {
                ensureInitialized();
                // 使用同样的 flush 逻辑写入剩余
                flushBatchToSheets();
            }

            // 写文件并释放资源
            if (workbook != null) {
                if (currentSheet != null) {
                    autoSizeColumns(currentSheet, fieldMapper.getHeaders().length);
                }
                try (FileOutputStream out = new FileOutputStream(singleExcelFile)) {
                    workbook.write(out);
                    out.flush();
                    //容器环境下不支持
                    //out.getFD().sync();
                }
                workbook.dispose();
                workbook.close();
                workbook = null;
                currentSheet = null;
                File file = new File(singleExcelFile);
                if (!file.exists() || file.length() == 0) {
                    throw new IOException("文件写入失败或文件为空: " + singleExcelFile);
                }
                log.debug("单文件模式：文件写入完成，文件大小: {} bytes, 路径: {}", file.length(), singleExcelFile);
            }

        }

    }

    private void writeBatch(List<T> batch, CSVPrinter csvPrinter, CsvFieldMapper<T> fieldMapper)
            throws IOException {
        for (T entity : batch) {
            csvPrinter.printRecord(fieldMapper.mapToRow(entity));
        }
        csvPrinter.flush();
    }

    private String createCsvFile(String tempDir, int fileIndex) {
        return tempDir + "/part" + fileIndex + ".csv";
    }

    private Writer createWriter(String filePath) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8);
        writer.write("");
        return writer;
    }

    private CSVPrinter createCsvPrinter(Writer writer, String[] headers) throws IOException {
        return new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
    }

    private CSVPrinter createCsvPrinterWithoutHeader(Writer writer, String[] headers) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT;
        CSVPrinter printer = new CSVPrinter(writer, format);
        printer.printRecord((Object[]) headers);
        printer.flush();
        log.debug("创建新sheet表头，表头列数: {}", headers.length);
        return printer;
    }

    private String handleCompression(List<String> csvFiles, String taskId,
                                     String fileNamePrefix, String exportDir) throws IOException {
        log.info("4.压缩处理");
        Files.createDirectories(Paths.get(exportDir));

        boolean zipEnabled = configProperties.isZipEnabled();
        if (zipEnabled) {
            log.info("打包压缩文件");
            String zipFilePath = exportDir + "/" + fileNamePrefix + "_" + taskId + ".zip";
            zipFileService.createZip(csvFiles, zipFilePath);
            log.info("已打包{}个CSV文件为ZIP: {}", csvFiles.size(), zipFilePath);
            for (String csvFile : csvFiles) {
                Files.deleteIfExists(Paths.get(csvFile));
            }
            return zipFilePath;
        } else {

            //不是本地不需要处理
            String sourceFile = csvFiles.get(0);
            File source = new File(sourceFile);
            if (!source.exists()) {
                throw new IOException("源文件不存在: " + sourceFile);
            }
            long startTime = System.currentTimeMillis();
            long fileSize = -1;
            while (System.currentTimeMillis() - startTime < 5000) {
                long currentSize = source.length();
                if (currentSize == fileSize && fileSize > 0) {
                    break;
                }
                fileSize = currentSize;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("等待文件写入完成时被中断", e);
                }
            }
            if (source.length() == 0) {
                throw new IOException("文件大小为0，可能还未完全写入: " + sourceFile);
            }
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String finalFile = exportDir + "/" + fileNamePrefix + "_" + timestamp + ".xlsx";
            Path sourcePath = Paths.get(sourceFile);
            Path finalPath = Paths.get(finalFile);
            if(!"local".equals(configProperties.getUploadType())){
                // 1. 获取源文件的父目录
                Path parentDir = sourcePath.getParent();
                finalPath = parentDir.resolve(fileNamePrefix + "_" + timestamp + ".xlsx");
                finalFile = parentDir + "/" + fileNamePrefix + "_" + timestamp + ".xlsx";
            }
            Files.move(sourcePath, finalPath);
            File target = new File(finalFile);
            if (!target.exists() || target.length() == 0) {
                throw new IOException("文件移动失败或目标文件无效: " + finalFile);
            }
            log.info("单文件模式：生成单个XLSX文件（包含多个sheet）: {}, 文件大小: {} bytes",
                    finalFile, target.length());
            return finalFile;
        }
    }

    private void cleanupTempFiles(String tempDir) {
        log.info("清理临时目录");
        if (tempDir == null || tempDir.isEmpty()) {
            return;
        }

        try {
            Files.deleteIfExists(Paths.get(tempDir));
        } catch (IOException e) {
            log.warn("清理临时目录失败: {}", tempDir, e);
        }
    }
}
