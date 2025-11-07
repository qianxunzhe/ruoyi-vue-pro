package cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * SPU维度聚合结果 VO
 */
@Schema(description = "管理后台 - SPU维度聚合结果 VO")
@Data
public class ProfitReportSpuAggVO {
    
    @Schema(description = "SPU", example = "SPU001")
    @ExcelProperty("SPU")
    private String spu;
    
    @Schema(description = "父ASIN", example = "B001234567")
    @ExcelProperty("父ASIN")
    private String parentAsin;
    
    @Schema(description = "产品名称", example = "Product Series A")
    @ExcelProperty("产品名称")
    private String productName;
    
    @Schema(description = "SKU列表", example = "[\"SKU001\", \"SKU002\"]")
    @ExcelIgnore
    private List<String> skuList;
    
    @Schema(description = "SKU数量", example = "5")
    @ExcelProperty("SKU数量")
    private Integer skuCount;
    
    @Schema(description = "ASIN数量", example = "10")
    @ExcelProperty("ASIN数量")
    private Integer asinCount;
    
    @Schema(description = "总销售数量", example = "1000")
    @ExcelProperty("总销售数量")
    private Integer totalSalesQuantity;
    
    @Schema(description = "总销售金额", example = "50000.00")
    @ExcelProperty("销售收入")
    private BigDecimal totalSalesAmount;
    
    @Schema(description = "退款数量", example = "20")
    @ExcelProperty("退款数量")
    private Integer refundsQuantity;
    
    @Schema(description = "退款金额", example = "1000.00")
    @ExcelProperty("退款金额")
    private BigDecimal totalSalesRefunds;
    
    @Schema(description = "退货退款", example = "1200.00")
    @ExcelProperty("退货退款")
    private BigDecimal totalRefunds;
    
    @Schema(description = "销售成本", example = "10000.00")
    @ExcelProperty("销售成本")
    private BigDecimal cgPriceTotal;
    
    @Schema(description = "头程运费", example = "2000.00")
    @ExcelProperty("头程运费")
    private BigDecimal cgTransportCostsTotal;
    
    @Schema(description = "其他业务成本", example = "500.00")
    @ExcelProperty("其他业务成本")
    private BigDecimal cgOtherCostsTotal;
    
    @Schema(description = "其他业务收入", example = "1000.00")
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
    
    @Schema(description = "包装收入", example = "10.00")
    private BigDecimal giftWrapCredits;
    
    @Schema(description = "买家交易保障索赔额", example = "5.00")
    private BigDecimal guaranteeClaims;
    
    @Schema(description = "积分抵减收入", example = "15.00")
    private BigDecimal costOfPointsGranted;
    
    @Schema(description = "费用退款项", example = "150.00")
    private BigDecimal totalFeeRefunds;
    
    @Schema(description = "推广费", example = "1500.00")
    @ExcelProperty("推广费")
    private BigDecimal promotionFee;
    
    @Schema(description = "站外费", example = "800.00")
    @ExcelProperty("站外费")
    private BigDecimal offsiteFee;
    
    @Schema(description = "其他费用", example = "1200.00")
    @ExcelProperty("其他费用")
    private BigDecimal otherFee;
    
    @Schema(description = "广告花费", example = "2000.00")
    @ExcelProperty("广告费")
    private BigDecimal totalAdsCost;
    
    @Schema(description = "广告销售额", example = "8000.00")
    @ExcelProperty("广告销售额")
    private BigDecimal totalAdsSales;
    
    @Schema(description = "平台费", example = "7500.00")
    @ExcelProperty("平台费")
    private BigDecimal platformFee;
    
    @Schema(description = "配送费", example = "4000.00")
    @ExcelProperty("配送费")
    private BigDecimal totalFbaDeliveryFee;
    
    @Schema(description = "仓储费", example = "1000.00")
    @ExcelProperty("仓储费")
    private BigDecimal totalStorageFee;
    
    @Schema(description = "总费用", example = "15000.00")
    @ExcelProperty("总费用")
    private BigDecimal totalFees;
    
    @Schema(description = "总成本", example = "20000.00")
    @ExcelProperty("总成本")
    private BigDecimal totalCost;
    
    @Schema(description = "毛利润", example = "15000.00")
    @ExcelProperty("毛利润")
    private BigDecimal grossProfit;
    
    @Schema(description = "毛利率(%)", example = "30.00")
    @ExcelProperty("毛利率(%)")
    private BigDecimal grossRate;
    
    @Schema(description = "ACOS(%)", example = "25.00")
    @ExcelProperty("ACOS(%)")
    private BigDecimal acos;
    
    @Schema(description = "ROAS", example = "4.00")
    @ExcelProperty("ROAS")
    private BigDecimal roas;
}