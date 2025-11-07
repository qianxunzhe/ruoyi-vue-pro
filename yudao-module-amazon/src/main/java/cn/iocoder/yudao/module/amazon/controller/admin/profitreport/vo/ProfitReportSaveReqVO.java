package cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 亚马逊利润报表数据新增/修改 Request VO")
@Data
public class ProfitReportSaveReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32601")
    private Long id;

    @Schema(description = "同步日期（数据所属日期）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "同步日期不能为空")
    private LocalDate syncDate;

    @Schema(description = "ASIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "ASIN不能为空")
    private String asin;

    @Schema(description = "父ASIN")
    private String parentAsin;

    @Schema(description = "SKU")
    private String localSku;

    @Schema(description = "SPU")
    private String spu;

    @Schema(description = "SID", example = "22369")
    private String sid;

    @Schema(description = "店铺名称", example = "李四")
    private String storeName;

    @Schema(description = "国家代码")
    private String countryCode;

    @Schema(description = "总销售数量")
    private Integer totalSalesQuantity;

    @Schema(description = "FBA销售数量")
    private Integer fbaSalesQuantity;

    @Schema(description = "FBM销售数量")
    private Integer fbmSalesQuantity;

    @Schema(description = "退款数量")
    private Integer refundsQuantity;

    @Schema(description = "FBA退货数量")
    private Integer fbaReturnsQuantity;

    @Schema(description = "总销售金额")
    private BigDecimal totalSalesAmount;

    @Schema(description = "FBA销售金额")
    private BigDecimal fbaSaleAmount;

    @Schema(description = "FBM销售金额")
    private BigDecimal fbmSaleAmount;

    @Schema(description = "总退款金额")
    private BigDecimal totalSalesRefunds;

    @Schema(description = "广告总销售额")
    private BigDecimal totalAdsSales;

    @Schema(description = "广告总花费")
    private BigDecimal totalAdsCost;

    @Schema(description = "SP广告销售额")
    private BigDecimal adsSpSales;

    @Schema(description = "SP广告花费")
    private BigDecimal adsSpCost;

    @Schema(description = "SD广告销售额")
    private BigDecimal adsSdSales;

    @Schema(description = "SD广告花费")
    private BigDecimal adsSdCost;

    @Schema(description = "SB广告销售额")
    private BigDecimal adsSbSales;

    @Schema(description = "SB广告花费")
    private BigDecimal adsSbCost;

    @Schema(description = "平台费用")
    private BigDecimal platformFee;

    @Schema(description = "FBA总配送费用")
    private BigDecimal totalFbaDeliveryFee;

    @Schema(description = "总仓储费用")
    private BigDecimal totalStorageFee;

    @Schema(description = "推广费用")
    private BigDecimal promotionFee;

    @Schema(description = "采购成本总额")
    private BigDecimal cgPriceTotal;

    @Schema(description = "头程运输成本总额")
    private BigDecimal cgTransportCostsTotal;

    @Schema(description = "其他成本总额")
    private BigDecimal cgOtherCostsTotal;

    @Schema(description = "合计成本")
    private BigDecimal totalCost;

    @Schema(description = "毛利润")
    private BigDecimal grossProfit;

    @Schema(description = "毛利率")
    private BigDecimal grossRate;

    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "交易状态", example = "1")
    private String transactionStatus;

    @Schema(description = "产品本地名称/品名", example = "王五")
    private String localName;

    @Schema(description = "其他费用明细（JSON格式存储）")
    private String otherFeeStr;

    @Schema(description = "数据同步时间")
    private LocalDateTime syncTime;

}