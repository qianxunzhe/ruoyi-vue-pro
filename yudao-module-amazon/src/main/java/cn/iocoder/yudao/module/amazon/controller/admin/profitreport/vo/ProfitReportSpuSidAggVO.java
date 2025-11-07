package cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * SPU+SID维度聚合结果 VO
 * 按SPU和店铺ID（SID）两个维度进行聚合，同一个SPU在不同店铺分开统计
 */
@Schema(description = "管理后台 - SPU+SID维度聚合结果 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProfitReportSpuSidAggVO extends ProfitReportSpuAggVO {
    
    @Schema(description = "店铺ID", example = "101")
    @ExcelProperty("店铺ID")
    private String sid;
    
    @Schema(description = "店铺名称", example = "US Store 1")
    @ExcelProperty("店铺名称")
    private String storeName;
    
    @Schema(description = "国家代码", example = "US")
    @ExcelProperty("国家代码")
    private String countryCode;
    
    @Schema(description = "店铺SPU组合键", example = "SPU001_101")
    @ExcelIgnore
    private String spuSidKey;
    
    @Schema(description = "该SPU在该店铺的ASIN列表", example = "[\"B001234567\", \"B001234568\"]")
    @ExcelIgnore
    private List<String> asinList;
    
    @Schema(description = "该SPU在该店铺的天数", example = "30")
    @ExcelProperty("统计天数")
    private Long daysCount;
    
    @Schema(description = "店铺平均毛利率(%)", example = "32.50")
    @ExcelProperty("店铺平均毛利率(%)")
    private BigDecimal avgGrossRate;
    
    @Schema(description = "日均销售数量", example = "33")
    @ExcelProperty("日均销售数量")
    private BigDecimal avgDailySalesQuantity;
    
    @Schema(description = "日均销售金额", example = "1666.67")
    @ExcelProperty("日均销售金额")
    private BigDecimal avgDailySalesAmount;
}