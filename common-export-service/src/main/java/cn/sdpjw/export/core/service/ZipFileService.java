package cn.sdpjw.export.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * ZIP文件处理服务
 */
@Slf4j
@Service
public class ZipFileService {
    
    /**
     * 解压ZIP文件到临时目录
     * 
     * @param zipFilePath ZIP文件路径
     * @param extractDir 解压目录
     * @return 解压后的文件列表
     * @throws IOException IO异常
     */
    public List<String> extractZip(String zipFilePath, String extractDir) throws IOException {
        List<String> extractedFiles = new ArrayList<>();
        
        Path extractPath = Paths.get(extractDir);
        if (!Files.exists(extractPath)) {
            Files.createDirectories(extractPath);
        }
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String fileName = entry.getName();
                    // 只处理Excel和CSV文件
                    if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls") || fileName.endsWith(".csv")) {
                        Path filePath = extractPath.resolve(new File(fileName).getName());
                        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
                            byte[] buffer = new byte[8192];
                            int len;
                            while ((len = zis.read(buffer)) > 0) {
                                fos.write(buffer, 0, len);
                            }
                        }
                        extractedFiles.add(filePath.toString());
                        log.info("解压文件: {} -> {}", fileName, filePath);
                    }
                }
                zis.closeEntry();
            }
        }
        
        return extractedFiles;
    }
    
    /**
     * 判断文件是否为ZIP格式
     * 
     * @param filePath 文件路径
     * @return 是否为ZIP文件
     */
    public boolean isZipFile(String filePath) {
        return filePath != null && filePath.toLowerCase().endsWith(".zip");
    }
    
    /**
     * 压缩文件为ZIP
     * 
     * @param files 要压缩的文件列表
     * @param zipFilePath 输出ZIP文件路径
     * @throws IOException IO异常
     */
    public void createZip(List<String> files, String zipFilePath) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            for (String filePath : files) {
                File file = new File(filePath);
                if (file.exists()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry entry = new ZipEntry(file.getName());
                        zos.putNextEntry(entry);
                        
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                        }
                        zos.closeEntry();
                    }
                }
            }
        }
        log.info("创建ZIP文件: {}, 包含{}个文件", zipFilePath, files.size());
    }
}

