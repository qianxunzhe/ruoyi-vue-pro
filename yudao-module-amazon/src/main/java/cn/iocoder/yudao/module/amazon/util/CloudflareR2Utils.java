package cn.iocoder.yudao.module.amazon.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Cloudflare R2 文件上传工具类
 * 
 * 提供文件上传到 Cloudflare R2 存储服务的功能
 * 支持单文件上传和多文件打包ZIP上传
 * 
 * @author 芋道源码
 */
@Slf4j
@Component
public class CloudflareR2Utils {

    @Value("${cloudflare.r2.endpoint:}")
    private String endpoint;
    
    @Value("${cloudflare.r2.access-key:}")
    private String accessKey;
    
    @Value("${cloudflare.r2.secret-key:}")
    private String secretKey;
    
    @Value("${cloudflare.r2.bucket:}")
    private String bucket;
    
    @Value("${cloudflare.r2.domain:}")
    private String domain;
    
    @Value("${cloudflare.r2.enabled:false}")
    private boolean enabled;
    
    private S3Client s3Client;
    private S3Presigner s3Presigner;
    
    /**
     * 初始化 S3 客户端
     */
    @PostConstruct
    public void init() {
        if (!enabled) {
            log.info("Cloudflare R2 未启用");
            return;
        }
        
        if (StrUtil.isBlank(endpoint) || StrUtil.isBlank(accessKey) || 
            StrUtil.isBlank(secretKey) || StrUtil.isBlank(bucket)) {
            log.warn("Cloudflare R2 配置不完整，跳过初始化");
            return;
        }
        
        try {
            // 构建 S3 客户端
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);
            
            S3Configuration config = S3Configuration.builder()
                    .pathStyleAccessEnabled(false)
                    .chunkedEncodingEnabled(false)
                    .build();
            
            this.s3Client = S3Client.builder()
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(credentialsProvider)
                    .region(Region.of("auto"))
                    .serviceConfiguration(config)
                    .build();
            
            // 初始化预签名器
            this.s3Presigner = S3Presigner.builder()
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(credentialsProvider)
                    .region(Region.of("auto"))
                    .serviceConfiguration(config)
                    .build();
            
            log.info("Cloudflare R2 客户端初始化成功");
        } catch (Exception e) {
            log.error("Cloudflare R2 客户端初始化失败", e);
        }
    }
    
    /**
     * 清理资源
     */
    @PreDestroy
    public void destroy() {
        if (s3Client != null) {
            s3Client.close();
            log.info("Cloudflare R2 客户端已关闭");
        }
        if (s3Presigner != null) {
            s3Presigner.close();
            log.info("Cloudflare R2 预签名器已关闭");
        }
    }
    
    /**
     * 上传文件到 R2
     * 
     * @param content 文件内容
     * @param fileName 文件名
     * @param directory 目录路径（可选）
     * @return 文件访问 URL
     */
    public String uploadFile(byte[] content, String fileName, String directory) {
        if (!enabled || s3Client == null) {
            log.warn("Cloudflare R2 未启用或未初始化");
            return null;
        }
        
        try {
            // 构建文件路径
            String key = buildObjectKey(fileName, directory);
            
            // 获取文件类型
            String contentType = getContentType(fileName);
            
            // 构建上传请求
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) content.length)
                    .build();
            
            // 执行上传
            s3Client.putObject(putRequest, RequestBody.fromBytes(content));
            
            // 返回文件访问 URL
            String url = buildAccessUrl(key);
            log.info("文件上传成功: {}", url);
            return url;
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传到 R2 失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 上传 Excel 文件
     * 
     * @param excelContent Excel 文件内容
     * @param fileName 文件名（不含扩展名）
     * @return 文件访问 URL
     */
    public String uploadExcel(byte[] excelContent, String fileName) {
        String fullName = fileName + ".xlsx";
        String directory = "excel/" + DateUtil.format(new Date(), "yyyy/MM/dd");
        return uploadFile(excelContent, fullName, directory);
    }
    
    /**
     * 上传多个文件打包成 ZIP
     * 
     * @param fileMap 文件映射 (文件名 -> 文件内容)
     * @param zipName ZIP 文件名
     * @return ZIP 文件访问 URL
     */
    public String uploadZip(Map<String, byte[]> fileMap, String zipName) {
        if (fileMap == null || fileMap.isEmpty()) {
            throw new IllegalArgumentException("文件列表不能为空");
        }
        
        try {
            // 创建 ZIP 文件
            byte[] zipContent = createZipBytes(fileMap);
            
            // 确保文件名以 .zip 结尾
            if (!zipName.endsWith(".zip")) {
                zipName = zipName + ".zip";
            }
            
            // 上传 ZIP 文件
            String directory = "zip/" + DateUtil.format(new Date(), "yyyy/MM/dd");
            return uploadFile(zipContent, zipName, directory);
            
        } catch (Exception e) {
            log.error("ZIP 文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("ZIP 文件上传失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 删除文件
     * 
     * @param fileUrl 文件 URL 或对象键
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (!enabled || s3Client == null) {
            log.warn("Cloudflare R2 未启用或未初始化");
            return false;
        }
        
        try {
            // 从 URL 中提取对象键
            String key = extractObjectKey(fileUrl);
            
            // 构建删除请求
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            
            // 执行删除
            s3Client.deleteObject(deleteRequest);
            
            log.info("文件删除成功: {}", key);
            return true;
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 检查文件是否存在
     * 
     * @param fileUrl 文件 URL 或对象键
     * @return 是否存在
     */
    public boolean existsFile(String fileUrl) {
        if (!enabled || s3Client == null) {
            return false;
        }
        
        try {
            String key = extractObjectKey(fileUrl);
            
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            
            s3Client.headObject(headRequest);
            return true;
            
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("检查文件存在性失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 构建对象键（文件在 R2 中的路径）
     */
    private String buildObjectKey(String fileName, String directory) {
        // 生成唯一文件名（防止覆盖）
        String uniqueName = DateUtil.format(new Date(), "yyyyMMddHHmmss") + "_" + 
                           IdUtil.fastSimpleUUID().substring(0, 8) + "_" + fileName;
        
        if (StrUtil.isNotBlank(directory)) {
            return directory + "/" + uniqueName;
        }
        return uniqueName;
    }
    
    /**
     * 构建访问 URL
     */
    private String buildAccessUrl(String key) {
        // 如果配置了自定义域名，使用自定义域名
        if (StrUtil.isNotBlank(domain)) {
            return domain + "/" + key;
        }
        
        // 否则使用 R2 默认域名
        return endpoint + "/" + bucket + "/" + key;
    }
    
    /**
     * 从 URL 中提取对象键
     */
    private String extractObjectKey(String fileUrl) {
        if (StrUtil.isBlank(fileUrl)) {
            throw new IllegalArgumentException("文件 URL 不能为空");
        }
        
        // 如果已经是对象键，直接返回
        if (!fileUrl.startsWith("http")) {
            return fileUrl;
        }
        
        // 从 URL 中提取
        if (StrUtil.isNotBlank(domain) && fileUrl.startsWith(domain)) {
            return fileUrl.substring(domain.length() + 1);
        }
        
        // 尝试从 endpoint URL 中提取
        String prefix = endpoint + "/" + bucket + "/";
        if (fileUrl.startsWith(prefix)) {
            return fileUrl.substring(prefix.length());
        }
        
        // 尝试提取最后的路径部分
        int lastSlash = fileUrl.lastIndexOf('/');
        if (lastSlash != -1) {
            return fileUrl.substring(lastSlash + 1);
        }
        
        return fileUrl;
    }
    
    /**
     * 获取文件 MIME 类型
     */
    private String getContentType(String fileName) {
        String extension = FileUtil.extName(fileName).toLowerCase();
        
        Map<String, String> mimeTypes = new HashMap<>();
        mimeTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mimeTypes.put("xls", "application/vnd.ms-excel");
        mimeTypes.put("zip", "application/zip");
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("csv", "text/csv");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("xml", "application/xml");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("gif", "image/gif");
        
        return mimeTypes.getOrDefault(extension, "application/octet-stream");
    }
    
    /**
     * 创建 ZIP 字节数组
     */
    private byte[] createZipBytes(Map<String, byte[]> fileMap) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {
            
            for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
                String fileName = entry.getKey();
                byte[] fileContent = entry.getValue();
                
                // 创建 ZIP 条目
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);
                
                // 写入文件内容
                zipOut.write(fileContent);
                
                // 关闭当前条目
                zipOut.closeEntry();
            }
            
            zipOut.finish();
            return baos.toByteArray();
        }
    }
    
    /**
     * 生成预签名URL（临时访问URL）
     * 
     * @param objectKey 对象键
     * @param durationHours 有效时长（小时）
     * @return 预签名URL或自定义域名URL
     */
    public String generatePresignedUrl(String objectKey, int durationHours) {
        if (!enabled) {
            log.warn("Cloudflare R2 未启用");
            return null;
        }
        
        // 如果配置了自定义域名，直接返回自定义域名的URL
        // 注意：使用自定义域名需要在Cloudflare R2中配置相应的访问策略
        if (StrUtil.isNotBlank(domain)) {
            String url = buildAccessUrl(objectKey);
            log.info("使用自定义域名访问: {}", url);
            return url;
        }
        
        // 如果没有自定义域名，生成预签名URL
        if (s3Presigner == null) {
            log.warn("预签名器未初始化");
            return null;
        }
        
        try {
            // 构建预签名请求
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();
            
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(durationHours))
                    .getObjectRequest(getObjectRequest)
                    .build();
            
            // 生成预签名URL
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();
            
            log.info("生成预签名URL成功: {}, 有效期: {}小时", objectKey, durationHours);
            return presignedUrl;
            
        } catch (Exception e) {
            log.error("生成预签名URL失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * 上传文件并返回访问URL
     * 
     * @param content 文件内容
     * @param fileName 文件名
     * @param directory 目录路径（可选）
     * @param durationHours 预签名URL有效时长（小时），仅在未配置自定义域名时生效
     * @return 访问URL（自定义域名URL或预签名URL）
     */
    public String uploadFileWithPresignedUrl(byte[] content, String fileName, String directory, int durationHours) {
        if (!enabled || s3Client == null) {
            log.warn("Cloudflare R2 未启用或未初始化");
            return null;
        }
        
        try {
            // 构建文件路径
            String key = buildObjectKey(fileName, directory);
            
            // 获取文件类型
            String contentType = getContentType(fileName);
            
            // 构建上传请求
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .contentLength((long) content.length)
                    .build();
            
            // 执行上传
            s3Client.putObject(putRequest, RequestBody.fromBytes(content));
            
            // 生成预签名URL
            String presignedUrl = generatePresignedUrl(key, durationHours);
            
            log.info("文件上传成功并生成预签名URL: {}", presignedUrl);
            return presignedUrl;
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传到 R2 失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 上传 Excel 文件并返回预签名URL
     * 
     * @param excelContent Excel 文件内容
     * @param fileName 文件名（不含扩展名）
     * @param durationHours 预签名URL有效时长（小时）
     * @return 预签名URL
     */
    public String uploadExcelWithPresignedUrl(byte[] excelContent, String fileName, int durationHours) {
        String fullName = fileName + ".xlsx";
        String directory = "excel/" + DateUtil.format(new Date(), "yyyy/MM/dd");
        return uploadFileWithPresignedUrl(excelContent, fullName, directory, durationHours);
    }
    
    /**
     * 上传多个文件打包成 ZIP 并返回预签名URL
     * 
     * @param fileMap 文件映射 (文件名 -> 文件内容)
     * @param zipName ZIP 文件名
     * @param durationHours 预签名URL有效时长（小时）
     * @return 预签名URL
     */
    public String uploadZipWithPresignedUrl(Map<String, byte[]> fileMap, String zipName, int durationHours) {
        if (fileMap == null || fileMap.isEmpty()) {
            throw new IllegalArgumentException("文件列表不能为空");
        }
        
        try {
            // 创建 ZIP 文件
            byte[] zipContent = createZipBytes(fileMap);
            
            // 确保文件名以 .zip 结尾
            if (!zipName.endsWith(".zip")) {
                zipName = zipName + ".zip";
            }
            
            // 上传 ZIP 文件并返回预签名URL
            String directory = "zip/" + DateUtil.format(new Date(), "yyyy/MM/dd");
            return uploadFileWithPresignedUrl(zipContent, zipName, directory, durationHours);
            
        } catch (Exception e) {
            log.error("ZIP 文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("ZIP 文件上传失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 检查 R2 是否已启用
     */
    public boolean isEnabled() {
        return enabled && s3Client != null;
    }
}