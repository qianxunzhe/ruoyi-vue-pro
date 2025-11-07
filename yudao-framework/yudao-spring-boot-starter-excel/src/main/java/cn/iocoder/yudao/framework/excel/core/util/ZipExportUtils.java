package cn.iocoder.yudao.framework.excel.core.util;

import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import jakarta.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZIP文件导出工具类
 * 
 * 支持将多个文件打包成ZIP格式导出
 *
 * @author 芋道源码
 */
public class ZipExportUtils {

    /**
     * 将多个文件打包成ZIP并通过HTTP响应下载
     *
     * @param response HTTP响应对象
     * @param zipFileName ZIP文件名（包含.zip扩展名）
     * @param fileMap 文件映射，key为文件名（包含扩展名），value为文件字节数组
     * @throws IOException 导出失败时抛出
     */
    public static void exportZip(HttpServletResponse response, String zipFileName, 
                                 Map<String, byte[]> fileMap) throws IOException {
        if (fileMap == null || fileMap.isEmpty()) {
            throw new IllegalArgumentException("文件列表不能为空");
        }

        // 设置响应头
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", 
                          "attachment;filename=" + HttpUtils.encodeUtf8(zipFileName));

        // 创建ZIP输出流
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
                String fileName = entry.getKey();
                byte[] fileContent = entry.getValue();

                // 创建ZIP条目
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);
                
                // 写入文件内容
                zipOut.write(fileContent);
                
                // 关闭当前条目
                zipOut.closeEntry();
            }
            
            zipOut.finish();
        }
    }

    /**
     * 将多个文件打包成ZIP字节数组
     *
     * @param fileMap 文件映射，key为文件名（包含扩展名），value为文件字节数组
     * @return ZIP文件的字节数组
     * @throws IOException 打包失败时抛出
     */
    public static byte[] createZipBytes(Map<String, byte[]> fileMap) throws IOException {
        if (fileMap == null || fileMap.isEmpty()) {
            throw new IllegalArgumentException("文件列表不能为空");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zipOut = new ZipOutputStream(baos)) {
            
            for (Map.Entry<String, byte[]> entry : fileMap.entrySet()) {
                String fileName = entry.getKey();
                byte[] fileContent = entry.getValue();

                // 创建ZIP条目
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
     * 根据文件数量决定是直接导出单个文件还是打包成ZIP
     *
     * @param response HTTP响应对象
     * @param fileMap 文件映射，key为文件名（包含扩展名），value为文件字节数组
     * @param defaultFileName 当只有一个文件时使用的默认文件名
     * @param zipFileName 当有多个文件时使用的ZIP文件名
     * @throws IOException 导出失败时抛出
     */
    public static void exportFilesOrZip(HttpServletResponse response, 
                                        Map<String, byte[]> fileMap,
                                        String defaultFileName, 
                                        String zipFileName) throws IOException {
        if (fileMap == null || fileMap.isEmpty()) {
            throw new IllegalArgumentException("文件列表不能为空");
        }

        if (fileMap.size() == 1) {
            // 只有一个文件，直接导出Excel
            Map.Entry<String, byte[]> entry = fileMap.entrySet().iterator().next();
            byte[] fileContent = entry.getValue();
            
            // 设置Excel响应头（根据文件扩展名判断）
            String contentType = defaultFileName.toLowerCase().endsWith(".xlsx") 
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8"
                : "application/vnd.ms-excel;charset=UTF-8";
            response.setContentType(contentType);
            response.addHeader("Content-Disposition", 
                              "attachment;filename=" + HttpUtils.encodeUtf8(defaultFileName));
            
            // 写入文件内容
            response.getOutputStream().write(fileContent);
            response.getOutputStream().flush();
        } else {
            // 多个文件，打包成ZIP
            exportZip(response, zipFileName, fileMap);
        }
    }

    /**
     * 生成安全的文件名（移除特殊字符）
     *
     * @param fileName 原始文件名
     * @return 安全的文件名
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "unnamed";
        }
        
        // 替换不安全的字符
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_")
                      .replaceAll("\\s+", "_");
    }
}