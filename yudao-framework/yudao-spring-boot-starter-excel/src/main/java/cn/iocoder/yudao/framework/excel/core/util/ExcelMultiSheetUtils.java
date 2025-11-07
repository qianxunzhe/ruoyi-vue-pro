package cn.iocoder.yudao.framework.excel.core.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Excel多Sheet导出工具类
 * 
 * 支持将数据按分组导出到不同的Sheet中
 *
 * @author 芋道源码
 */
public class ExcelMultiSheetUtils {

    /**
     * 导出多Sheet的Excel到字节数组
     *
     * @param sheetDataMap Sheet名称与数据的映射，key为sheet名称，value为该sheet的数据列表
     * @param head         数据类的Class
     * @param <T>          数据类型
     * @return Excel文件的字节数组
     * @throws IOException 导出失败时抛出
     */
    public static <T> byte[] writeMultiSheet(Map<String, List<T>> sheetDataMap, Class<T> head) throws IOException {
        if (sheetDataMap == null || sheetDataMap.isEmpty()) {
            throw new IllegalArgumentException("Sheet数据不能为空");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ExcelWriter excelWriter = EasyExcel.write(outputStream, head)
                    .autoCloseStream(false)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerConverter(new LongStringConverter())
                    .build();

            try {
                int sheetIndex = 0;
                for (Map.Entry<String, List<T>> entry : sheetDataMap.entrySet()) {
                    String sheetName = entry.getKey();
                    List<T> data = entry.getValue();
                    
                    // 限制sheet名称长度，Excel sheet名称最大31个字符
                    if (sheetName != null && sheetName.length() > 31) {
                        sheetName = sheetName.substring(0, 28) + "...";
                    }
                    
                    // 如果sheet名称为空或无效，使用默认名称
                    if (sheetName == null || sheetName.trim().isEmpty()) {
                        sheetName = "Sheet" + (sheetIndex + 1);
                    }
                    
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, sheetName)
                            .head(head)
                            .build();
                    
                    excelWriter.write(data, writeSheet);
                }
            } finally {
                excelWriter.finish();
            }

            return outputStream.toByteArray();
        }
    }

    /**
     * 导出多Sheet的Excel到字节数组（支持不同Sheet使用不同的数据类）
     *
     * @param sheets Sheet配置列表
     * @return Excel文件的字节数组
     * @throws IOException 导出失败时抛出
     */
    public static byte[] writeMultiSheetWithDifferentHead(List<SheetConfig<?>> sheets) throws IOException {
        if (sheets == null || sheets.isEmpty()) {
            throw new IllegalArgumentException("Sheet配置不能为空");
        }

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ExcelWriter excelWriter = EasyExcel.write(outputStream)
                    .autoCloseStream(false)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerConverter(new LongStringConverter())
                    .build();

            try {
                int sheetIndex = 0;
                for (SheetConfig<?> config : sheets) {
                    String sheetName = config.getSheetName();
                    
                    // 限制sheet名称长度
                    if (sheetName != null && sheetName.length() > 31) {
                        sheetName = sheetName.substring(0, 28) + "...";
                    }
                    
                    // 如果sheet名称为空或无效，使用默认名称
                    if (sheetName == null || sheetName.trim().isEmpty()) {
                        sheetName = "Sheet" + (sheetIndex + 1);
                    }
                    
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, sheetName)
                            .head(config.getHeadClass())
                            .build();
                    
                    excelWriter.write(config.getData(), writeSheet);
                }
            } finally {
                excelWriter.finish();
            }

            return outputStream.toByteArray();
        }
    }

    /**
     * Sheet配置类
     *
     * @param <T> 数据类型
     */
    public static class SheetConfig<T> {
        private String sheetName;
        private Class<T> headClass;
        private List<T> data;

        public SheetConfig(String sheetName, Class<T> headClass, List<T> data) {
            this.sheetName = sheetName;
            this.headClass = headClass;
            this.data = data;
        }

        public String getSheetName() {
            return sheetName;
        }

        public Class<T> getHeadClass() {
            return headClass;
        }

        public List<T> getData() {
            return data;
        }
    }
}