package cn.iocoder.yudao.framework.excel.core.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Excel合并工具类
 * 支持将新数据追加到现有Excel文件的对应Sheet中
 *
 * @author 芋道源码
 */
@Slf4j
public class ExcelMergeUtils {

    /**
     * 合并Excel数据
     * 将新数据追加到历史Excel文件的对应Sheet中，保持原有数据不变
     *
     * @param historyFileBytes 历史Excel文件的字节数组
     * @param newDataMap       新数据Map，key为sheet名称，value为该sheet的新数据列表
     * @param dataClass        数据类的Class
     * @param <T>              数据类型
     * @return 合并后的Excel文件字节数组
     * @throws IOException 处理失败时抛出
     */
    public static <T> byte[] mergeExcelSheets(byte[] historyFileBytes, 
                                               Map<String, List<T>> newDataMap,
                                               Class<T> dataClass) throws IOException {
        
        log.info("开始合并Excel文件，历史文件大小：{}，新数据Sheet数量：{}", 
            historyFileBytes != null ? historyFileBytes.length : 0, 
            newDataMap != null ? newDataMap.size() : 0);
        
        if (newDataMap != null) {
            log.info("新数据Sheet名称列表: {}", newDataMap.keySet());
            for (Map.Entry<String, List<T>> entry : newDataMap.entrySet()) {
                log.info("Sheet: {} - 数据行数: {}", entry.getKey(), 
                    entry.getValue() != null ? entry.getValue().size() : 0);
            }
        }
        
        if (historyFileBytes == null || historyFileBytes.length == 0) {
            // 如果没有历史文件，直接创建新文件
            log.info("没有历史文件，创建新的Excel文件");
            return ExcelMultiSheetUtils.writeMultiSheet(newDataMap, dataClass);
        }
        
        // 读取历史Excel文件的所有Sheet数据
        Map<String, List<LinkedHashMap<Integer, String>>> historyDataMap = readAllSheetsAsMap(historyFileBytes);
        log.info("读取到历史文件中的Sheet数量：{}", historyDataMap.size());
        
        // 创建输出流
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ExcelWriter excelWriter = EasyExcel.write(outputStream)
                    .autoCloseStream(false)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .registerConverter(new LongStringConverter())
                    .build();
            
            try {
                int sheetIndex = 0;
                Set<String> processedSheets = new HashSet<>();
                
                // 首先处理历史文件中已存在的Sheet
                for (Map.Entry<String, List<LinkedHashMap<Integer, String>>> historyEntry : historyDataMap.entrySet()) {
                    String sheetName = historyEntry.getKey();
                    List<LinkedHashMap<Integer, String>> historyData = historyEntry.getValue();
                    
                    log.info("处理Sheet：{}，历史数据行数：{}", sheetName, historyData.size());
                    
                    // 获取该Sheet的新数据
                    List<T> newData = findMatchingNewData(sheetName, newDataMap);
                    
                    log.info("Sheet: {} - 历史数据行数: {}, 新数据行数: {}", 
                        sheetName, historyData.size(), 
                        newData != null ? newData.size() : 0);
                    
                    // 合并数据
                    List<List<String>> mergedData = mergeSheetDataSimple(historyData, newData);
                    
                    log.info("Sheet {} 合并后的数据行数：{}, 列数: {}", 
                        sheetName, mergedData.size(),
                        mergedData.isEmpty() ? 0 : mergedData.get(0).size());
                    
                    // 写入合并后的数据
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, sheetName).build();
                    excelWriter.write(mergedData, writeSheet);
                    processedSheets.add(sheetName);
                }
                
                // 处理新数据中存在但历史文件中不存在的Sheet
                for (Map.Entry<String, List<T>> newEntry : newDataMap.entrySet()) {
                    String sheetName = newEntry.getKey();
                    String matchedSheetName = findMatchingSheetName(sheetName, processedSheets);
                    
                    if (matchedSheetName == null) {
                        List<T> newData = newEntry.getValue();
                        log.info("创建新Sheet：{}，数据行数：{}", sheetName, newData.size());
                        
                        // 转换新数据为二维列表格式
                        List<List<String>> formattedData = formatNewDataAsSheet(newData);
                        
                        WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, sheetName).build();
                        excelWriter.write(formattedData, writeSheet);
                    }
                }
                
            } finally {
                excelWriter.finish();
            }
            
            byte[] result = outputStream.toByteArray();
            log.info("合并完成，生成的Excel文件大小：{}", result.length);
            return result;
        }
    }
    
    /**
     * 查找匹配的新数据
     */
    private static <T> List<T> findMatchingNewData(String sheetName, Map<String, List<T>> newDataMap) {
        // 直接匹配
        if (newDataMap.containsKey(sheetName)) {
            return newDataMap.get(sheetName);
        }
        
        // 处理被截断的sheet名称（超过31字符会被截断）
        for (Map.Entry<String, List<T>> entry : newDataMap.entrySet()) {
            String key = entry.getKey();
            if (key.length() > 31) {
                String truncatedKey = key.substring(0, 28) + "...";
                if (truncatedKey.equals(sheetName)) {
                    return entry.getValue();
                }
            }
            // 反向匹配：历史sheet名称可能是截断的
            if (sheetName.endsWith("...") && key.startsWith(sheetName.substring(0, sheetName.length() - 3))) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    /**
     * 查找匹配的Sheet名称
     */
    private static String findMatchingSheetName(String sheetName, Set<String> processedSheets) {
        // 直接匹配
        if (processedSheets.contains(sheetName)) {
            return sheetName;
        }
        
        // 处理被截断的sheet名称
        if (sheetName.length() > 31) {
            String truncatedName = sheetName.substring(0, 28) + "...";
            if (processedSheets.contains(truncatedName)) {
                return truncatedName;
            }
        }
        
        // 检查是否有匹配的截断名称
        for (String processed : processedSheets) {
            if (processed.endsWith("...") && sheetName.startsWith(processed.substring(0, processed.length() - 3))) {
                return processed;
            }
        }
        
        return null;
    }
    
    /**
     * 读取Excel文件的所有Sheet数据（保持原始格式）
     */
    private static Map<String, List<LinkedHashMap<Integer, String>>> readAllSheetsAsMap(byte[] excelBytes) {
        Map<String, List<LinkedHashMap<Integer, String>>> allSheetsData = new HashMap<>();
        
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(excelBytes)) {
            ExcelReader excelReader = EasyExcel.read(inputStream).build();
            
            // 获取所有Sheet
            List<ReadSheet> sheets = excelReader.excelExecutor().sheetList();
            
            for (ReadSheet sheet : sheets) {
                List<LinkedHashMap<Integer, String>> sheetData = new ArrayList<>();
                
                // 读取每个Sheet的数据，保持原始列索引
                ReadSheet readSheet = EasyExcel.readSheet(sheet.getSheetNo())
                        // 历史文件模板首行就是数据，显式设置没有表头，避免首行被EasyExcel当作表头丢失
                        .headRowNumber(0)
                        .registerReadListener(new AnalysisEventListener<LinkedHashMap<Integer, String>>() {
                            @Override
                            public void invoke(LinkedHashMap<Integer, String> data, AnalysisContext context) {
                                // 保存每行数据
                                sheetData.add(new LinkedHashMap<>(data));
                            }
                            
                            @Override
                            public void doAfterAllAnalysed(AnalysisContext context) {
                                // 读取完成
                            }
                        }).build();
                
                excelReader.read(readSheet);
                allSheetsData.put(sheet.getSheetName(), sheetData);
                log.info("读取Sheet：{}，数据行数：{}", sheet.getSheetName(), sheetData.size());
            }
            
            excelReader.finish();
        } catch (Exception e) {
            log.error("读取Excel文件失败", e);
        }
        
        return allSheetsData;
    }
    
    /**
     * 合并单个Sheet的数据（简化版本）
     * 在历史数据的基础上，追加新的列
     */
    private static <T> List<List<String>> mergeSheetDataSimple(
            List<LinkedHashMap<Integer, String>> historyData,
            List<T> newData) {
        
        List<List<String>> mergedData = new ArrayList<>();
        
        // 如果没有历史数据，直接格式化新数据
        if (historyData == null || historyData.isEmpty()) {
            if (newData != null && !newData.isEmpty()) {
                return formatNewDataAsSheet(newData);
            }
            return mergedData;
        }
        
        // 获取历史数据的最大列数
        int maxColumns = 0;
        for (LinkedHashMap<Integer, String> row : historyData) {
            // 找出最大的列索引
            for (Integer colIndex : row.keySet()) {
                if (colIndex != null && colIndex >= maxColumns) {
                    maxColumns = colIndex + 1;
                }
            }
        }
        log.info("历史数据最大列数: {}", maxColumns);
        
        // 将新数据转换为List格式，便于按索引访问
        List<Map<String, Object>> newDataList = new ArrayList<>();
        if (newData != null) {
            for (T data : newData) {
                newDataList.add(convertObjectToMap(data));
            }
        }
        
        // 检查新数据的第一行是否是日期范围（需要跳过）
        int newDataStartOffset = 0;
        if (!newDataList.isEmpty()) {
            Map<String, Object> firstRow = newDataList.get(0);
            if (firstRow != null && "日期范围".equals(firstRow.get("itemName"))) {
                newDataStartOffset = 1; // 跳过日期范围行
                log.info("检测到新数据包含日期范围行，将跳过，日期范围: {}", firstRow.get("amountStr"));
            }
        }
        
        log.info("准备合并数据 - 历史数据行数: {}, 新数据行数: {}, 新数据起始偏移: {}", 
            historyData.size(), newDataList.size(), newDataStartOffset);
        
        // 逐行合并数据
        for (int i = 0; i < historyData.size(); i++) {
            LinkedHashMap<Integer, String> historyRow = historyData.get(i);
            List<String> mergedRow = new ArrayList<>();
            
            // 复制历史数据的所有列
            for (int j = 0; j < maxColumns; j++) {
                mergedRow.add(historyRow.getOrDefault(j, ""));
            }
            
            // 添加新数据列
            if (!newDataList.isEmpty()) {
                // 计算新数据的对应索引
                int newDataIndex = i + newDataStartOffset;
                
                if (newDataIndex < newDataList.size()) {
                    Map<String, Object> newDataRow = newDataList.get(newDataIndex);
                    if (newDataRow != null) {
                        Object amountStr = newDataRow.get("amountStr");
                        // 处理null值，显示为空而不是"null"字符串
                        if (amountStr != null && !"null".equals(String.valueOf(amountStr))) {
                            String value = String.valueOf(amountStr);
                            mergedRow.add(value);
                            if (i < 5) { // 只记录前5行的日志
                                log.debug("合并第{}行，新数据值: '{}'", i+1, value);
                            }
                        } else {
                            mergedRow.add("");
                            if (i < 5) {
                                log.debug("合并第{}行，新数据为空", i+1);
                            }
                        }
                    } else {
                        mergedRow.add("");
                    }
                } else {
                    // 如果新数据行数不够，添加空值
                    mergedRow.add("");
                }
            }
            
            mergedData.add(mergedRow);
        }
        
        // 如果新数据比历史数据多，追加剩余的行
        if (newDataList.size() > historyData.size() + newDataStartOffset) {
            for (int i = historyData.size() + newDataStartOffset; i < newDataList.size(); i++) {
                Map<String, Object> newDataRow = newDataList.get(i);
                if (newDataRow != null) {
                    List<String> newRow = new ArrayList<>();
                    
                    // 第一列：项目名称
                    Object itemName = newDataRow.get("itemName");
                    newRow.add(itemName != null ? String.valueOf(itemName) : "");
                    
                    // 中间列：添加空值以对齐
                    for (int j = 1; j < maxColumns; j++) {
                        newRow.add("");
                    }
                    
                    // 最后一列：新数据值
                    Object amountStr = newDataRow.get("amountStr");
                    if (amountStr != null && !"null".equals(String.valueOf(amountStr))) {
                        newRow.add(String.valueOf(amountStr));
                    } else {
                        newRow.add("");
                    }
                    
                    mergedData.add(newRow);
                }
            }
        }
        
        log.info("合并后的数据行数：{}，列数：{}", mergedData.size(), 
            mergedData.isEmpty() ? 0 : mergedData.get(0).size());
        
        return mergedData;
    }
    
    
    /**
     * 格式化新数据作为新Sheet
     */
    private static <T> List<List<String>> formatNewDataAsSheet(List<T> newData) {
        List<List<String>> formattedData = new ArrayList<>();
        
        if (newData == null || newData.isEmpty()) {
            return formattedData;
        }
        
        int dataStartIndex = 0;

        // 历史模板首行即为数据，此处仅跳过人工插入的"日期范围"行
        Map<String, Object> firstRowMap = convertObjectToMap(newData.get(0));
        if (firstRowMap != null && "日期范围".equals(firstRowMap.get("itemName"))) {
            dataStartIndex = 1; // 跳过描述列
        }

        // 添加数据行（保持与历史模板一致，不额外生成表头）
        for (int i = dataStartIndex; i < newData.size(); i++) {
            T data = newData.get(i);
            Map<String, Object> dataMap = convertObjectToMap(data);
            if (dataMap != null) {
                List<String> row = new ArrayList<>();
                
                Object itemName = dataMap.get("itemName");
                Object amountStr = dataMap.get("amountStr");
                
                row.add(itemName != null ? String.valueOf(itemName) : "");
                row.add(amountStr != null ? String.valueOf(amountStr) : "");
                
                formattedData.add(row);
            }
        }

        return formattedData;
    }
    
    /**
     * 将对象转换为Map
     */
    private static Map<String, Object> convertObjectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        
        Map<String, Object> map = new HashMap<>();
        
        // 使用反射获取对象的所有字段
        java.lang.reflect.Field[] fields = obj.getClass().getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    map.put(field.getName(), value);
                    // 记录关键字段的日志
                    if ("itemName".equals(field.getName()) || "amountStr".equals(field.getName())) {
                        log.trace("字段 {} = {}", field.getName(), value);
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("读取字段值失败: {}", field.getName(), e);
            }
        }
        
        return map;
    }
}
