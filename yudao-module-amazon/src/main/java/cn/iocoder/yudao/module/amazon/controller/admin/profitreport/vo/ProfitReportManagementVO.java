package cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理利润表 VO
 * 按照标准管理利润表格式导出
 */
@Schema(description = "管理后台 - 管理利润表 VO")
@Data
@ContentRowHeight(20)
@HeadRowHeight(25)
public class ProfitReportManagementVO {
    
    @ExcelProperty(value = "项目", index = 0)
    @ColumnWidth(30)
    private String itemName;
    
    @ExcelProperty(value = "金额", index = 1)
    @ColumnWidth(20)
    private String amountStr;  // 改为String类型，可以显示数字或文本
    
    /**
     * 创建标题行
     */
    public static ProfitReportManagementVO createTitle() {
        ProfitReportManagementVO vo = new ProfitReportManagementVO();
        vo.setItemName("管理利润表");
        return vo;
    }
    
    /**
     * 创建编制单位行
     */
    public static ProfitReportManagementVO createCompany(String companyName) {
        ProfitReportManagementVO vo = new ProfitReportManagementVO();
        vo.setItemName("编制单位: " + (companyName != null ? companyName : ""));
        vo.setAmountStr(null);
        return vo;
    }
    
    /**
     * 创建日期行
     */
    public static ProfitReportManagementVO createPeriod(String period) {
        ProfitReportManagementVO vo = new ProfitReportManagementVO();
        vo.setItemName("项目");
        vo.setAmountStr(period); // 现在可以直接显示日期文本
        return vo;
    }
    
    /**
     * 创建一级标题行（如：一、收入：）
     */
    public static ProfitReportManagementVO createSection(String sectionName) {
        ProfitReportManagementVO vo = new ProfitReportManagementVO();
        vo.setItemName(sectionName);
        vo.setAmountStr(null);
        return vo;
    }
    
    /**
     * 创建数据行
     */
    public static ProfitReportManagementVO createDataRow(String itemName, BigDecimal amount) {
        ProfitReportManagementVO vo = new ProfitReportManagementVO();
        vo.setItemName(itemName);
        vo.setAmountStr(amount != null ? amount.setScale(2, java.math.RoundingMode.HALF_UP).toString() : null);
        return vo;
    }
    
    /**
     * 创建文本数据行（字符串值）
     */
    public static ProfitReportManagementVO createStringDataRow(String itemName, String value) {
        ProfitReportManagementVO vo = new ProfitReportManagementVO();
        vo.setItemName(itemName);
        vo.setAmountStr(value);
        return vo;
    }
    
    /**
     * 创建文本数据行
     */
    public static ProfitReportManagementVO createTextRow(String itemName, String text) {
        ProfitReportManagementVO vo = new ProfitReportManagementVO();
        vo.setItemName(itemName);
        vo.setAmountStr(text);
        return vo;
    }
    
    /**
     * 创建缩进的数据行（子项目）
     */
    public static ProfitReportManagementVO createIndentedDataRow(String itemName, BigDecimal amount) {
        ProfitReportManagementVO vo = new ProfitReportManagementVO();
        vo.setItemName("      " + itemName); // 6个空格缩进
        vo.setAmountStr(amount != null ? amount.setScale(2, java.math.RoundingMode.HALF_UP).toString() : null);
        return vo;
    }
}