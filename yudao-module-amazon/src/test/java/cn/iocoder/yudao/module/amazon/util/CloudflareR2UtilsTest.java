package cn.iocoder.yudao.module.amazon.util;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cloudflare R2 工具类测试
 * 
 * 注意：运行测试前需要：
 * 1. 配置 application-r2.yml 中的 R2 相关参数
 * 2. 设置 cloudflare.r2.enabled = true
 * 3. 确保网络可以访问 Cloudflare R2
 * 
 * @author 芋道源码
 */
@Slf4j
@SpringBootTest
@ActiveProfiles({"local", "r2"})  // 加载 local 和 r2 配置
public class CloudflareR2UtilsTest {

    @Resource
    private CloudflareR2Utils r2Utils;
    
    @BeforeEach
    public void setUp() {
        // 检查 R2 是否启用
        if (!r2Utils.isEnabled()) {
            log.warn("Cloudflare R2 未启用，跳过测试");
        }
    }
    
    /**
     * 测试上传文本文件
     */
    @Test
    public void testUploadTextFile() {
        if (!r2Utils.isEnabled()) {
            log.warn("R2 未启用，跳过测试");
            return;
        }
        
        // 准备测试数据
        String content = "Hello, Cloudflare R2! 这是一个测试文件。\n时间: " + System.currentTimeMillis();
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        String fileName = "test_" + RandomUtil.randomNumbers(6) + ".txt";
        
        // 执行上传
        String url = r2Utils.uploadFile(bytes, fileName, "test");
        
        // 验证结果
        assertNotNull(url, "上传返回的 URL 不应为空");
        assertTrue(url.contains(fileName), "URL 应包含文件名");
        
        log.info("文本文件上传成功，URL: {}", url);
        
        // 测试文件存在性
        boolean exists = r2Utils.existsFile(url);
        assertTrue(exists, "上传的文件应该存在");
    }
    
    /**
     * 测试上传 Excel 文件
     */
    @Test
    public void testUploadExcelFile() {
        if (!r2Utils.isEnabled()) {
            log.warn("R2 未启用，跳过测试");
            return;
        }
        
        // 模拟 Excel 文件内容（实际应用中是真实的 Excel 字节数据）
        String excelContent = "ID,Name,Value\n1,Test1,100\n2,Test2,200";
        byte[] bytes = excelContent.getBytes(StandardCharsets.UTF_8);
        String fileName = "test_report_" + RandomUtil.randomNumbers(6);
        
        // 执行上传
        String url = r2Utils.uploadExcel(bytes, fileName);
        
        // 验证结果
        assertNotNull(url, "上传返回的 URL 不应为空");
        assertTrue(url.contains(".xlsx"), "URL 应包含 .xlsx 扩展名");
        assertTrue(url.contains("excel/"), "URL 应包含 excel 目录");
        
        log.info("Excel 文件上传成功，URL: {}", url);
    }
    
    /**
     * 测试上传 ZIP 文件
     */
    @Test
    public void testUploadZipFile() {
        if (!r2Utils.isEnabled()) {
            log.warn("R2 未启用，跳过测试");
            return;
        }
        
        // 准备多个文件
        Map<String, byte[]> fileMap = new HashMap<>();
        fileMap.put("file1.txt", "这是第一个文件的内容".getBytes(StandardCharsets.UTF_8));
        fileMap.put("file2.csv", "col1,col2,col3\nval1,val2,val3".getBytes(StandardCharsets.UTF_8));
        fileMap.put("readme.md", "# 测试 ZIP 文件\n包含多个文件".getBytes(StandardCharsets.UTF_8));
        
        String zipName = "test_archive_" + RandomUtil.randomNumbers(6);
        
        // 执行上传
        String url = r2Utils.uploadZip(fileMap, zipName);
        
        // 验证结果
        assertNotNull(url, "上传返回的 URL 不应为空");
        assertTrue(url.contains(".zip"), "URL 应包含 .zip 扩展名");
        assertTrue(url.contains("zip/"), "URL 应包含 zip 目录");
        
        log.info("ZIP 文件上传成功，URL: {}", url);
        log.info("ZIP 包含 {} 个文件", fileMap.size());
    }
    
    /**
     * 测试删除文件
     */
    @Test
    public void testDeleteFile() {
        if (!r2Utils.isEnabled()) {
            log.warn("R2 未启用，跳过测试");
            return;
        }
        
        // 先上传一个文件
        String content = "这是一个将要被删除的测试文件";
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        String fileName = "delete_test_" + RandomUtil.randomNumbers(6) + ".txt";
        
        String url = r2Utils.uploadFile(bytes, fileName, "test");
        assertNotNull(url, "上传应该成功");
        
        // 验证文件存在
        assertTrue(r2Utils.existsFile(url), "文件应该存在");
        
        // 执行删除
        boolean deleted = r2Utils.deleteFile(url);
        assertTrue(deleted, "删除应该成功");
        
        // 验证文件已被删除
        assertFalse(r2Utils.existsFile(url), "文件应该已被删除");
        
        log.info("文件删除测试成功");
    }
    
    /**
     * 测试大文件上传（模拟）
     */
//    @Test
//    public void testUploadLargeFile() {
//        if (!r2Utils.isEnabled()) {
//            log.warn("R2 未启用，跳过测试");
//            return;
//        }
//
//        // 创建一个较大的文件（例如 5MB）
//        int size = 5 * 1024 * 1024; // 5MB
//        byte[] largeContent = new byte[size];
//        RandomUtil.randomBytes(largeContent);
//
//        String fileName = "large_file_" + RandomUtil.randomNumbers(6) + ".bin";
//
//        // 记录开始时间
//        long startTime = System.currentTimeMillis();
//
//        // 执行上传
//        String url = r2Utils.uploadFile(largeContent, fileName, "large");
//
//        // 计算耗时
//        long duration = System.currentTimeMillis() - startTime;
//
//        // 验证结果
//        assertNotNull(url, "大文件上传应该成功");
//
//        log.info("大文件上传成功，大小: {} MB，耗时: {} ms，URL: {}",
//                size / (1024 * 1024), duration, url);
//    }
    
    /**
     * 测试并发上传
     */
    @Test
    public void testConcurrentUpload() throws InterruptedException {
        if (!r2Utils.isEnabled()) {
            log.warn("R2 未启用，跳过测试");
            return;
        }
        
        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                String content = "并发测试文件 " + index + " - " + System.currentTimeMillis();
                byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
                String fileName = "concurrent_" + index + "_" + RandomUtil.randomNumbers(4) + ".txt";
                
                String url = r2Utils.uploadFile(bytes, fileName, "concurrent");
                assertNotNull(url, "并发上传应该成功");
                
                log.info("线程 {} 上传成功: {}", index, url);
            });
            threads[i].start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        log.info("并发上传测试完成，共 {} 个线程", threadCount);
    }
}