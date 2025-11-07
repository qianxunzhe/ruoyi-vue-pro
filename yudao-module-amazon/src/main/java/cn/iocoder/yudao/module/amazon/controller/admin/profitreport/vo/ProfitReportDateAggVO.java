package cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 日期维度聚合结果 VO
 */
@Schema(description = "管理后台 - 日期维度聚合结果 VO")
@Data
public class ProfitReportDateAggVO {
    
    @Schema(description = "日期", example = "2024-01-15")
    @ExcelProperty("日期")
    private LocalDate date;
    
    @Schema(description = "ASIN数量", example = "150")
    @ExcelProperty("ASIN数量")
    private Long asinCount;
    
    @Schema(description = "SKU数量", example = "200")
    @ExcelProperty("SKU数量")
    private Long skuCount;
    
    @Schema(description = "店铺数量", example = "10")
    @ExcelProperty("店铺数量")
    private Long storeCount;
    
    @Schema(description = "总销售数量", example = "10000")
    @ExcelProperty("总销售数量")
    private Integer totalSalesQuantity;
    
    @Schema(description = "FBA销售数量", example = "8000")
    @ExcelProperty("FBA销售数量")
    private Integer fbaSalesQuantity;
    
    @Schema(description = "FBM销售数量", example = "2000")
    @ExcelProperty("FBM销售数量")
    private Integer fbmSalesQuantity;
    
    @Schema(description = "退款数量", example = "100")
    @ExcelProperty("退款数量")
    private Integer refundsQuantity;
    
    @Schema(description = "总销售金额", example = "500000.00")
    @ExcelProperty("销售收入")
    private BigDecimal totalSalesAmount;
    
    @Schema(description = "FBA销售金额", example = "400000.00")
    @ExcelProperty("FBA销售金额")
    private BigDecimal fbaSaleAmount;
    
    @Schema(description = "FBM销售金额", example = "100000.00")
    @ExcelProperty("FBM销售金额")
    private BigDecimal fbmSaleAmount;
    
    @Schema(description = "退款金额", example = "5000.00")
    @ExcelProperty("退款金额")
    private BigDecimal totalSalesRefunds;
    
    @Schema(description = "退货退款", example = "5000.00")
    @ExcelProperty("退货退款")
    private BigDecimal totalRefunds;
    
    @Schema(description = "销售成本", example = "30000.00")
    @ExcelProperty("销售成本")
    private BigDecimal cgPriceTotal;
    
    @Schema(description = "头程运费", example = "5000.00")
    @ExcelProperty("头程运费")
    private BigDecimal cgTransportCostsTotal;
    
    @Schema(description = "其他业务成本", example = "2000.00")
    @ExcelProperty("其他业务成本")
    private BigDecimal cgOtherCostsTotal;
    
    @Schema(description = "其他业务收入", example = "3000.00")
    @ExcelProperty("其他业务收入")
    private BigDecimal otherRevenue;
    
    // 净销售额组成明细字段
    @Schema(description = "买家运费", example = "50.00")
    private BigDecimal shippingCredits;
    
    @Schema(description = "促销折扣", example = "30.00")
    private BigDecimal promotionalRebates;
    
    @Schema(description = "FBA库存赔偿", example = "20.00")
    private BigDecimal fbaInventoryCredit;
    
    @Schema(description = "COD", example = "10.00")
    private BigDecimal cashOnDelivery;
    
    @Schema(description = "其他收入", example = "90.00")
    private BigDecimal otherInAmount;
    
    @Schema(description = "费用退款项", example = "150.00")
    private BigDecimal totalFeeRefunds;
    
    @Schema(description = "推广费", example = "5000.00")
    @ExcelProperty("推广费")
    private BigDecimal promotionFee;
    
    @Schema(description = "站外费", example = "3000.00")
    @ExcelProperty("站外费")
    private BigDecimal offsiteFee;
    
    @Schema(description = "其他费用", example = "4000.00")
    @ExcelProperty("其他费用")
    private BigDecimal otherFee;
    
    @Schema(description = "广告花费", example = "20000.00")
    @ExcelProperty("广告费")
    private BigDecimal totalAdsCost;
    
    @Schema(description = "广告销售额", example = "80000.00")
    @ExcelProperty("广告销售额")
    private BigDecimal totalAdsSales;
    
    @Schema(description = "平台费用", example = "75000.00")
    @ExcelProperty("平台费")
    private BigDecimal platformFee;
    
    @Schema(description = "FBA配送费", example = "40000.00")
    @ExcelProperty("配送费")
    private BigDecimal totalFbaDeliveryFee;
    
    @Schema(description = "仓储费用", example = "10000.00")
    @ExcelProperty("仓储费")
    private BigDecimal totalStorageFee;
    
    @Schema(description = "总成本", example = "200000.00")
    @ExcelProperty("总成本")
    private BigDecimal totalCost;
    
    @Schema(description = "毛利润", example = "150000.00")
    @ExcelProperty("毛利润")
    private BigDecimal grossProfit;
    
    @Schema(description = "毛利率(%)", example = "30.00")
    @ExcelProperty("毛利率(%)")
    private BigDecimal grossRate;
    
    @Schema(description = "ACOS(%)", example = "25.00")
    @ExcelProperty("ACOS(%)")
    private BigDecimal acos;
}