package cn.iocoder.yudao.module.amazon.controller.admin.profitreport;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo.ProfitReportManagementVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.*;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

@Tag(name = "测试 - 利润报表导出测试")
@RestController
@RequestMapping("/amazon/profit-report-test")
@Slf4j
public class ProfitReportTestController {

    @GetMapping("/test-excel-export")
    @Operation(summary = "测试Excel导出功能")
    public void testExcelExport(HttpServletResponse response) throws Exception {
        log.info("开始测试Excel导出功能");
        
        // 创建测试数据
        Map<String, List<ProfitReportManagementVO>> sheetDataMap = new LinkedHashMap<>();
        
        // 为每个SPU创建一个Sheet
        for (int i = 1; i <= 3; i++) {
            String sheetName = "SPU00" + i;
            List<ProfitReportManagementVO> data = createTestManagementData(sheetName);
            sheetDataMap.put(sheetName, data);
        }
        
        // 生成Excel字节数组
        byte[] excelBytes = generateExcelBytes(sheetDataMap);
        
        log.info("生成的Excel字节数组大小: {} bytes", excelBytes.length);
        
        // 设置响应头
        String fileName = "test_export.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", 
            "attachment;filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
        
        // 写入响应
        response.getOutputStream().write(excelBytes);
        response.getOutputStream().flush();
        response.getOutputStream().close();
        
        log.info("Excel导出完成");
    }
    
    /**
     * 创建测试的管理利润表数据
     */
    private List<ProfitReportManagementVO> createTestManagementData(String spu) {
        List<ProfitReportManagementVO> result = new ArrayList<>();
        
        // 添加标题行
        result.add(ProfitReportManagementVO.createTitle());
        
        // 项目和日期
//        result.add(ProfitReportManagementVO.createStringDataRow("项目", spu));
//        result.add(ProfitReportManagementVO.createStringDataRow("日期", "25-08-18--25-08-24"));
//
//        // 一、销售收入
//        result.add(ProfitReportManagementVO.createSection("一、销售收入："));
//        result.add(ProfitReportManagementVO.createDataRow("产品销售额", new BigDecimal("10000.00")));
//        result.add(ProfitReportManagementVO.createDataRow("退货退款", new BigDecimal("-500.00")));
//        result.add(ProfitReportManagementVO.createDataRow("净销售额", new BigDecimal("9500.00")));
//
//        // 二、毛利润
//        result.add(ProfitReportManagementVO.createSection("二、毛利润："));
//        result.add(ProfitReportManagementVO.createDataRow("采购成本", new BigDecimal("3000.00")));
//        result.add(ProfitReportManagementVO.createDataRow("采购物流成本", new BigDecimal("500.00")));
//        result.add(ProfitReportManagementVO.createDataRow("毛利润", new BigDecimal("6000.00")));
//        result.add(ProfitReportManagementVO.createDataRow("毛利率", "63.16%"));
        
        return result;
    }
    
    /**
     * 生成Excel字节数组
     */
    private byte[] generateExcelBytes(Map<String, List<ProfitReportManagementVO>> sheetDataMap) throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ExcelWriter excelWriter = EasyExcel.write(outputStream, ProfitReportManagementVO.class)
                    .autoCloseStream(false)
                    .build();
            
            try {
                int sheetIndex = 0;
                for (Map.Entry<String, List<ProfitReportManagementVO>> entry : sheetDataMap.entrySet()) {
                    String sheetName = entry.getKey();
                    List<ProfitReportManagementVO> data = entry.getValue();
                    
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheetIndex++, sheetName)
                            .head(ProfitReportManagementVO.class)
                            .build();
                    
                    excelWriter.write(data, writeSheet);
                    log.info("写入Sheet: {}, 数据行数: {}", sheetName, data.size());
                }
            } finally {
                excelWriter.finish();
            }
            
            return outputStream.toByteArray();
        }
    }
}