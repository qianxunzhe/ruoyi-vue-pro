package cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 亚马逊利润报表数据 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProfitReportRespVO {


    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("日期")
    private LocalDate syncDate;



    @Schema(description = "ASIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("ASIN")
    private String asin;

    @Schema(description = "父ASIN")
    @ExcelProperty("父ASIN")
    private String parentAsin;

    @Schema(description = "SKU")
    @ExcelProperty("SKU")
    private String localSku;

    @Schema(description = "SPU")
    @ExcelProperty("SPU")
    private String spu;


    @Schema(description = "店铺名称", example = "李四")
    @ExcelProperty("店铺名称")
    private String storeName;


    @Schema(description = "销售收入")
    @ExcelProperty("销售收入")
    private BigDecimal totalSalesAmount;


    @Schema(description = "退货退款")
    @ExcelProperty("退货退款")
    private BigDecimal totalRefunds;

    @Schema(description = "销售成本")
    @ExcelProperty("销售成本")
    private BigDecimal cgPriceTotal;


    @Schema(description = "头程运费")
    @ExcelProperty("头程运费")
    private BigDecimal cgTransportCostsTotal;

    @Schema(description = "营业税金及附加")
    @ExcelProperty("营业税金及附加")
    private BigDecimal cgTransportCostsTotal222;


    @Schema(description = "其他业务收入")
    @ExcelProperty("其他业务收入")
    private BigDecimal otherRevenue;

    @Schema(description = "其他业务成本")
    @ExcelProperty("其他业务成本")
    private BigDecimal cgOtherCostsTotal;



    @Schema(description = "毛利润")
    @ExcelProperty("毛利润")
    private BigDecimal grossProfit;

    @Schema(description = "毛利率")
    @ExcelProperty("毛利率")
    private BigDecimal grossRate;

    @Schema(description = "毛利率")
    @ExcelProperty("毛利率")
    private String grossRateStr;


    @Schema(description = "平台费")
    @ExcelProperty("平台费")
    private BigDecimal platformFee;

    @Schema(description = "配送费")
    @ExcelProperty("配送费")
    private BigDecimal totalFbaDeliveryFee;

    @Schema(description = "广告费")
    @ExcelProperty("广告费")
    private BigDecimal totalAdsCost;

    @Schema(description = "推广费")
    @ExcelProperty("推广费")
    private BigDecimal promotionFee;


    @Schema(description = "站外费")
    @ExcelProperty("站外费")
    private BigDecimal OffsiteFee;



    @Schema(description = "仓储费")
    @ExcelProperty("仓储费")
    private BigDecimal totalStorageFee;

    @Schema(description = "其他费用")
    @ExcelProperty("其他费用")
    private BigDecimal otherFee;






    @Schema(description = "广告总销售额")
    @ExcelProperty("广告总销售额")
    private BigDecimal totalAdsSales;



    @Schema(description = "SP广告销售额")
    @ExcelProperty("SP广告销售额")
    private BigDecimal adsSpSales;

    @Schema(description = "SP广告花费")
    @ExcelProperty("SP广告花费")
    private BigDecimal adsSpCost;

    @Schema(description = "SD广告销售额")
    @ExcelProperty("SD广告销售额")
    private BigDecimal adsSdSales;

    @Schema(description = "SD广告花费")
    @ExcelProperty("SD广告花费")
    private BigDecimal adsSdCost;

    @Schema(description = "SB广告销售额")
    @ExcelProperty("SB广告销售额")
    private BigDecimal adsSbSales;

    @Schema(description = "SB广告花费")
    @ExcelProperty("SB广告花费")
    private BigDecimal adsSbCost;


    @Schema(description = "合计成本")
    @ExcelProperty("合计成本")
    private BigDecimal totalCost;


    @Schema(description = "货币代码")
    @ExcelProperty("货币代码")
    private String currencyCode;



    @Schema(description = "产品本地名称/品名", example = "王五")
    @ExcelProperty("产品本地名称/品名")
    private String localName;

    @Schema(description = "其他费用明细（JSON格式存储）")
    @ExcelProperty("其他费用明细（JSON格式存储）")
    private String otherFeeStr;


    
    // ==================== 新增字段（用于计算） ====================

    // 1. 收入相关字段
    @Schema(description = "买家运费")
    private BigDecimal shippingCredits;

    @Schema(description = "促销折扣")
    private BigDecimal promotionalRebates;

    @Schema(description = "FBA库存赔偿")
    private BigDecimal fbaInventoryCredit;

    @Schema(description = "COD货到付款")
    private BigDecimal cashOnDelivery;

    @Schema(description = "其他收入")
    private BigDecimal otherInAmount;

    // 2. 退款相关字段
    @Schema(description = "收入退款额")
    private BigDecimal totalSalesRefunds;

    @Schema(description = "费用退款额")
    private BigDecimal totalFeeRefunds;

    @Schema(description = "FBA销售退款额")
    private BigDecimal fbaSalesRefunds;

    @Schema(description = "FBM销售退款额")
    private BigDecimal fbmSalesRefunds;

    @Schema(description = "平台费退款额")
    private BigDecimal sellingFeeRefunds;

    @Schema(description = "发货费退款额")
    private BigDecimal fbaTransactionFeeRefunds;

    @Schema(description = "其他订单费退款额")
    private BigDecimal otherTransactionFeeRefunds;

    @Schema(description = "买家运费退款额")
    private BigDecimal shippingCreditRefunds;

    // 3. 费用相关字段（细分项）
    @Schema(description = "FBA发货费")
    private BigDecimal fbaDeliveryFee;

    @Schema(description = "FBA发货费(多渠道)")
    private BigDecimal mcFbaDeliveryFee;

    @Schema(description = "其他订单费用")
    private BigDecimal otherTransactionFees;

    @Schema(description = "调整费用")
    private BigDecimal adjustments;

    @Schema(description = "平台其他费")
    private BigDecimal totalPlatformOtherFee;

    // 4. 成本相关字段（单价）
    @Schema(description = "采购均价")
    private BigDecimal cgUnitPrice;

    @Schema(description = "头程均价")
    private BigDecimal cgTransportUnitCosts;

    // 5. 库存相关字段
    @Schema(description = "赔偿量")
    private Integer fbaInventoryCreditQuantity;

    // 6. 站外推广费相关字段
    @Schema(description = "订单其他费")
    private BigDecimal customOrderFee;

    @Schema(description = "站外推广费-本金")
    private BigDecimal customOrderFeePrincipal;

    @Schema(description = "站外推广费-佣金")
    private BigDecimal customOrderFeeCommission;

    // 7. 额外的业务字段
    @Schema(description = "退款率")
    private BigDecimal refundsRate;

    @Schema(description = "FBA退货率")
    private BigDecimal fbaReturnsQuantityRate;

    // 8. 其他费用计算所需字段
    @Schema(description = "入库配置费（入仓手续费）")
    private BigDecimal sharedFbaInboundTransportationProgramFee;

    @Schema(description = "FBA国际物流货运费")
    private BigDecimal sharedFbaIntegerernationalInboundFee;

}