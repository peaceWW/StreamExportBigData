package cn.sdpjw.export.core.upload;


import cn.sdpjw.common.base.exception.BusinessException;
import cn.sdpjw.export.config.OssConfigProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.UploadFileRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;

/**
 * 阿里云OSS文件上传策略
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "export.oss", name = "access-key-id")
public class OssFileUploadStrategy implements FileUploadStrategy {

    @Autowired(required = false)
    private OssConfigProperties ossConfig;

    @Override
    public String getType() {
        return "oss";
    }


    public String orgUpload(String filePath, String remotePath) throws IOException {
        if (ossConfig == null) {
            throw new BusinessException("OSS配置未初始化，请检查配置文件中的export.oss相关配置");
        }

        // 验证配置
        if (ossConfig.getAccessKeyId() == null || ossConfig.getAccessKeyId().isEmpty()) {
            throw new BusinessException("OSS AccessKeyId未配置");
        }
        if (ossConfig.getAccessKeySecret() == null || ossConfig.getAccessKeySecret().isEmpty()) {
            throw new IllegalStateException("OSS AccessKeySecret未配置");
        }
        if (ossConfig.getEndpoint() == null || ossConfig.getEndpoint().isEmpty()) {
            throw new BusinessException("OSS Endpoint未配置");
        }
        if (ossConfig.getBucketName() == null || ossConfig.getBucketName().isEmpty()) {
            throw new BusinessException("OSS BucketName未配置");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }

        // 如果没有指定远程路径，使用文件名
        String objectKey = remotePath;
        if (objectKey == null || objectKey.isEmpty()) {
            objectKey = file.getName();
        }

        OSS ossClient = null;
        try {
            // 创建OSS客户端（方案二：条件判断 - 如果配置了 internal 则使用内网，否则使用公网）
            String endpointToUse = getEndpointForClient();
            ossClient = new OSSClientBuilder().build(
                    endpointToUse,
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret()
            );
            log.debug("创建OSS客户端: endpoint={}", endpointToUse);

            // 上传文件
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    objectKey,
                    file
            );

            ossClient.putObject(putObjectRequest);
            log.info("文件上传到OSS成功: bucket={}, objectKey={}, localFile={}",
                    ossConfig.getBucketName(), objectKey, filePath);

            // 生成签名URL（用于下载）
            Date expiration = new Date(System.currentTimeMillis() + ossConfig.getExpired() * 1000);
            URL url = ossClient.generatePresignedUrl(ossConfig.getBucketName(), objectKey, expiration);

            String urlString = url.toString();

            // 如果配置了 host，替换 URL 中的域名部分为配置的 host（用于返回公网地址）
            urlString = replaceUrlHostIfConfigured(urlString);

            log.info("生成OSS签名URL: {}", urlString);

            return urlString;

        } catch (Exception e) {
            log.error("上传文件到OSS失败: filePath={}, objectKey={}", filePath, objectKey, e);
            throw new IOException("上传文件到OSS失败: " + e.getMessage(), e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 最佳方案 2：UploadFileRequest（自动分片 + 自动多线程 + 可选断点续传）
     * 适用于 >=5MB 文件，效率极高。
     *
     * @param filePath 本地文件路径
     * @param remotePath OSS对象名称（远程文件路径），会根据日期自动构建路径结构
     *                   格式：根据objectName和当前日期（年/月/日）生成路径
     *                   示例：objectName="exports/order.xlsx" -> "exports/2024/01/15/order.xlsx"
     */
    @Override
    public String upload(String filePath, String remotePath) throws IOException {
        File localFile = new File(filePath);
        if (!localFile.exists()) {
            throw new IOException("文件不存在: " + filePath);
        }
        OSS ossClient = null;
        try {
            // 创建OSS客户端（方案二：条件判断 - 如果配置了 internal 则使用内网，否则使用公网）
            String endpointToUse = getEndpointForClient();
            ossClient = new OSSClientBuilder().build(endpointToUse, ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
            log.debug("创建OSS客户端: endpoint={}", endpointToUse);

            // 构建带日期的 OSS 路径
            String ossKey = buildOssPathWithDate(remotePath, localFile.getName());
            log.info("开始上传 OSS：local={}, oss={}", localFile.getAbsolutePath(), ossKey);

            // --------------------------
            // 核心逻辑（自动分片 + 多线程）
            // --------------------------
            UploadFileRequest uploadFileRequest = new UploadFileRequest(ossConfig.getBucketName(), ossKey);

            uploadFileRequest.setUploadFile(localFile.getAbsolutePath());

            // 分片大小（5MB）
            uploadFileRequest.setPartSize(5 * 1024 * 1024);

            // 并发线程数（建议 4~8）
            uploadFileRequest.setTaskNum(8);

            // 是否开启断点续传
            uploadFileRequest.setEnableCheckpoint(true);

            // 执行上传（SDK 自动管理分片 + 多线程）
            ossClient.uploadFile(uploadFileRequest);
            Date expiration = new Date(System.currentTimeMillis() + ossConfig.getExpired() * 1000);
            URL url = ossClient.generatePresignedUrl(ossConfig.getBucketName(), ossKey, expiration);

            String urlString = url.toString();

            // 如果配置了 host，替换 URL 中的域名部分为配置的 host（用于返回公网地址）
            urlString = replaceUrlHostIfConfigured(urlString);

            log.info("OSS上传完成，生成URL: {}", urlString);
            return urlString;

        } catch (Exception e) {
            log.error("OSS 上传失败: {}", e.getMessage(), e);
            throw new BusinessException("OSS 上传失败：" + e.getMessage());

        } catch (Throwable e) {
            throw new BusinessException(e.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


    @Override
    public InputStream download(String remoteUrl) throws IOException {
        if (ossConfig == null) {
            throw new IllegalStateException("OSS配置未初始化");
        }

        try {
            // 从URL中提取bucket和objectKey
            URI uri = new URI(remoteUrl);
            String host = uri.getHost();

            // 检查是否是OSS URL
            if (!host.contains("oss") || !host.contains("aliyuncs.com")) {
                throw new IOException("不是有效的OSS URL: " + remoteUrl);
            }

            // 从URL中提取bucket和objectKey
            // URL格式: https://bucket-name.oss-cn-beijing.aliyuncs.com/object-key?signature
            String bucketName;
            String objectKey;

            // 处理带签名的URL或直接URL
            if (host.startsWith(ossConfig.getBucketName() + ".")) {
                bucketName = ossConfig.getBucketName();
                objectKey = uri.getPath().substring(1); // 去掉开头的/
            } else {
                // 可能是其他格式，尝试从配置获取
                bucketName = ossConfig.getBucketName();
                // 从路径提取objectKey
                String path = uri.getPath();
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                objectKey = path;
            }

            // 创建OSS客户端（方案二：条件判断 - 如果配置了 internal 则使用内网，否则使用公网）
            String endpointToUse = getEndpointForClient();
            OSS ossClient = new OSSClientBuilder().build(
                    endpointToUse,
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret()
            );
            log.debug("创建OSS客户端用于下载: endpoint={}", endpointToUse);

            try {
                // 下载文件
                OSSObject ossObject = ossClient.getObject(bucketName, objectKey);
                log.info("从OSS下载文件成功: bucket={}, objectKey={}", bucketName, objectKey);
                return ossObject.getObjectContent();
            } finally {
                // 注意：不能在这里关闭ossClient，因为InputStream还需要使用
                // 在实际使用完InputStream后，需要关闭ossClient
                // 这里先不关闭，由调用者负责管理
            }
        } catch (Exception e) {
            log.error("从OSS下载文件失败: remoteUrl={}", remoteUrl, e);
            throw new IOException("从OSS下载文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean supportsUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        // 检查是否是OSS URL
        if (url.startsWith("http://") || url.startsWith("https://")) {
            try {
                URI uri = new URI(url);
                String host = uri.getHost();
                return host != null && (host.contains("oss") && host.contains("aliyuncs.com"));
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取用于创建 OSS 客户端的 endpoint（区域地址，不含 bucket 前缀）
     * <p>
     * OSS SDK 会自动以虚拟主机方式访问：{bucketName}.{endpoint}
     * 例如 bucket=duijie-dev、endpoint=oss-cn-beijing.aliyuncs.com
     * 实际请求域名为 duijie-dev.oss-cn-beijing.aliyuncs.com，属正常行为。
     */
    private String getEndpointForClient() {
        if (Boolean.TRUE.equals(ossConfig.getUseInternal())
                && ossConfig.getInternal() != null
                && !ossConfig.getInternal().trim().isEmpty()) {
            String internalEndpoint = normalizeEndpoint(ossConfig.getInternal().trim());
            log.info("OSS 客户端使用内网 endpoint: {}", internalEndpoint);
            return internalEndpoint;
        }

        String publicEndpoint = normalizeEndpoint(ossConfig.getEndpoint());
        if (publicEndpoint == null || publicEndpoint.isEmpty()) {
            throw new IllegalStateException("OSS Endpoint 未配置，请设置 export.oss.endpoint");
        }
        log.info("OSS 客户端使用公网 endpoint: {}", publicEndpoint);
        return publicEndpoint;
    }

    private String normalizeEndpoint(String endpoint) {
        if (endpoint == null) {
            return null;
        }
        String normalized = endpoint.trim();
        if (normalized.startsWith("https://")) {
            normalized = normalized.substring(8);
        } else if (normalized.startsWith("http://")) {
            normalized = normalized.substring(7);
        }
        return normalized;
    }

    /**
     * 如果配置了 host，替换 URL 中的域名部分为配置的 host
     * 用于确保返回给外部的 URL 使用公网地址，即使内部使用内网上传
     *
     * <p>处理逻辑：</p>
     * <ol>
     *   <li>统一使用 https 协议</li>
     *   <li>移除 URL 中的查询参数（签名部分，如 ?Expires=...&OSSAccessKeyId=...&Signature=...）</li>
     *   <li>如果配置了 host，替换域名部分</li>
     * </ol>
     *
     * @param originalUrl 原始 URL
     * @return 处理后的 URL（统一 https，无签名参数，如果配置了 host 则替换域名）
     */
    private String replaceUrlHostIfConfigured(String originalUrl) {
        try {
            // 解析原始 URL
            URI originalUri = new URI(originalUrl);
            String host = originalUri.getHost();
            String path = originalUri.getPath();
            String query = originalUri.getQuery();

            // 构建基础 URL（去掉查询参数）
            String baseUrl;
            if (path != null) {
                baseUrl = path;
            } else {
                baseUrl = "/";
            }

            // 确定要使用的 host
            String finalHost = host;
            if (ossConfig.getHost() != null && !ossConfig.getHost().trim().isEmpty()) {
                String configuredHost = ossConfig.getHost().trim();
                // 如果配置的 host 包含协议，提取域名部分；否则直接使用
                if (configuredHost.startsWith("https://")) {
                    finalHost = configuredHost.substring(8);
                } else if (configuredHost.startsWith("http://")) {
                    finalHost = configuredHost.substring(7);
                } else {
                    finalHost = configuredHost;
                }
                log.debug("替换URL域名: {} -> {}", host, finalHost);
            }

            // 构建最终 URL：统一使用 https，去掉查询参数
            String finalUrl = "https://" + finalHost + baseUrl;

            log.debug("处理URL: 原始={}, 最终={}, 已移除查询参数={}", originalUrl, finalUrl, query != null);
            return finalUrl;

        } catch (Exception e) {
            log.warn("处理URL失败，使用原始URL: {}", originalUrl, e);
            // 如果处理失败，至少尝试去掉查询参数和统一协议
            try {
                String processedUrl = originalUrl;
                // 去掉查询参数
                int queryIndex = processedUrl.indexOf('?');
                if (queryIndex > 0) {
                    processedUrl = processedUrl.substring(0, queryIndex);
                }
                // 统一使用 https
                if (processedUrl.startsWith("http://")) {
                    processedUrl = "https://" + processedUrl.substring(7);
                }
                return processedUrl;
            } catch (Exception e2) {
                log.warn("简单处理URL也失败，返回原始URL: {}", originalUrl, e2);
                return originalUrl;
            }
        }
    }

    /**
     * 根据objectName和当前日期构建OSS路径
     * 路径格式：{原始路径前缀}/{年}/{月}/{日}/{文件名}
     * 示例：
     * - objectName="exports/order.xlsx" -> "exports/2024/01/15/order.xlsx"
     * - objectName="order.xlsx" -> "2024/01/15/order.xlsx"
     * - objectName=null -> "2024/01/15/export_file.xlsx" (使用本地文件名)
     *
     * @param objectName 原始OSS对象名称（可能包含路径前缀）
     * @param remotePath 远程路径文件夹
     * @return 带日期的完整OSS路径
     */
    private String buildOssPathWithDate(String remotePath, String objectName) {
        // 获取当前日期（年/月/日）
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());
        String datePath = year + "/" + month + "/" + day;

        String fileName;
        String pathPrefix = remotePath;

        if (objectName != null && !objectName.isEmpty()) {
            // 解析objectName，提取路径前缀和文件名
            int lastSlashIndex = objectName.lastIndexOf('/');
            if (lastSlashIndex >= 0) {
                // 包含路径前缀
                pathPrefix = objectName.substring(0, lastSlashIndex);
                fileName = objectName.substring(lastSlashIndex + 1);
            } else {
                // 只有文件名，没有路径前缀
                fileName = objectName;
            }
        } else {
            // objectName为空，使用本地文件名
            fileName = remotePath;
        }

        // 确保文件名不为空
        if (fileName == null || fileName.isEmpty()) {
            fileName = remotePath;
        }

        // 构建最终路径：{路径前缀}/{年}/{月}/{日}/{文件名}
        if (pathPrefix != null && !pathPrefix.isEmpty()) {
            // 有路径前缀：exports/2024/01/15/order.xlsx
            return pathPrefix + "/" + datePath + "/" + fileName;
        } else {
            // 无路径前缀：2024/01/15/order.xlsx
            return datePath + "/" + fileName;
        }
    }
}
