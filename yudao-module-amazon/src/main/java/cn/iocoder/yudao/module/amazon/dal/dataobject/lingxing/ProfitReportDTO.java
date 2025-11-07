package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

/**
 * 领星利润报表数据DTO
 *
 * @author 芋道源码
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)  // 忽略未知字段
public class ProfitReportDTO {
    
    // ========== 基础信息 ==========
    private String id;
    private String postedDateLocale; // 按天汇总日期
    private Boolean isDisplayDetail;
    private String smallImageUrl;
    private String asin;
    private String parentAsin;
    private String storeName;
    private String sid;
    private String sids;
    private String asins;
    private String country;
    private String countryCode;
    private String localName;
    private String localSku;
    private String itemName;
    private String model;
    private String principalRealname;
    private String productDeveloperRealname;
    private String categoryName;
    private String brandName;
    private String currencyCode;
    private String currencyIcon;
    private String listingTagIds;
    
    // ========== 销售数量相关 ==========
    private Integer totalFbaAndFbmQuantity;
    private BigDecimal totalFbaAndFbmAmount;
    private Integer totalSalesQuantity;
    private Integer fbaSalesQuantity;
    private Integer fbmSalesQuantity;
    private Integer totalReshipQuantity;
    private Integer reshipFbmProductSalesQuantity;
    private Integer reshipFbmProductSaleRefundsQuantity;
    private Integer reshipFbaProductSalesQuantity;
    private Integer reshipFbaProductSaleRefundsQuantity;
    private Integer mcFbaFulfillmentFeesQuantity;
    private BigDecimal cgAbsQuantity;
    private BigDecimal cgQuantity;
    
    // ========== 广告相关 ==========
    private BigDecimal totalAdsSales;
    private BigDecimal adsSdSales;
    private BigDecimal adsSpSales;
    private BigDecimal sharedAdsSbSales;
    private BigDecimal sharedAdsSbvSales;
    private Integer totalAdsSalesQuantity;
    private Integer adsSdSalesQuantity;
    private Integer adsSpSalesQuantity;
    private Integer sharedAdsSbSalesQuantity;
    private Integer sharedAdsSbvSalesQuantity;
    
    // ========== 销售金额相关 ==========
    private BigDecimal totalSalesAmount;
    private BigDecimal fbaSaleAmount;
    private BigDecimal fbmSaleAmount;
    private BigDecimal shippingCredits;
    private BigDecimal promotionalRebates;
    private BigDecimal fbaInventoryCredit;
    private BigDecimal cashOnDelivery;
    private BigDecimal otherInAmount;
    private BigDecimal fbaLiquidationProceeds;
    private BigDecimal fbaLiquidationProceedsAdjustments;
    private BigDecimal amazonShippingReimbursement;
    private BigDecimal safeTReimbursement;
    private BigDecimal netcoTransaction;
    private BigDecimal reimbursements;
    private BigDecimal clawbacks;
    private BigDecimal sharedComminglingVatIncome;
    private BigDecimal giftWrapCredits;
    private BigDecimal guaranteeClaims;
    @JsonProperty("costOfPoIntegersGranted")  // API返回的字段名有拼写错误
    private BigDecimal costOfPointsGranted;
    
    // ========== 退款相关 ==========
    private BigDecimal totalSalesRefunds;
    private BigDecimal fbaSalesRefunds;
    private BigDecimal fbmSalesRefunds;
    private BigDecimal shippingCreditRefunds;
    private BigDecimal giftWrapCreditRefunds;
    private BigDecimal chargebacks;
    @JsonProperty("costOfPoIntegersReturned")  // API返回的字段名有拼写错误
    private BigDecimal costOfPointsReturned;
    private BigDecimal promotionalRebateRefunds;
    private BigDecimal totalFeeRefunds;
    private BigDecimal sellingFeeRefunds;
    private BigDecimal fbaTransactionFeeRefunds;
    private BigDecimal refundAdministrationFees;
    private BigDecimal otherTransactionFeeRefunds;
    private BigDecimal refundForAdvertiser;
    private BigDecimal pointsAdjusted;
    private BigDecimal shippingLabelRefunds;
    private Integer refundsQuantity;
    private BigDecimal refundsRate;
    private Integer fbaReturnsQuantity;
    private Integer fbaReturnsSaleableQuantity;
    private Integer fbaReturnsUnsaleableQuantity;
    private BigDecimal fbaReturnsQuantityRate;
    
    // ========== 费用相关 ==========
    private BigDecimal platformFee;
    private BigDecimal fbaDeliveryFee;
    private BigDecimal mcFbaDeliveryFee;
    private BigDecimal totalFbaDeliveryFee;
    private BigDecimal otherTransactionFees;
    private BigDecimal totalAdsCost;
    private BigDecimal adsSpCost;
    private BigDecimal adsSbCost;
    private BigDecimal adsSbvCost;
    private BigDecimal adsSdCost;
    private BigDecimal sharedCostOfAdvertising;
    private BigDecimal promotionFee;
    private BigDecimal sharedSubscriptionFee;
    private BigDecimal sharedLdFee;
    private BigDecimal sharedCouponFee;
    private BigDecimal sharedEarlyReviewerProgramFee;
    private BigDecimal sharedVineFee;
    
    // ========== 仓储费用相关 ==========
    private BigDecimal totalStorageFee;
    private BigDecimal fbaStorageFee;
    private BigDecimal sharedFbaStorageFee;
    private BigDecimal longTermStorageFee;
    private BigDecimal sharedLongTermStorageFee;
    private BigDecimal sharedStorageRenewalBilling;
    private BigDecimal sharedFbaDisposalFee;
    private BigDecimal sharedFbaRemovalFee;
    private BigDecimal sharedFbaInboundTransportationProgramFee;
    private BigDecimal sharedLabelingFee;
    private BigDecimal sharedPolybaggingFee;
    private BigDecimal sharedBubblewrapFee;
    private BigDecimal sharedTapingFee;
    private BigDecimal sharedAwdProcessingFee;
    private BigDecimal sharedAwdTransportationFee;
    private BigDecimal sharedAwdStorageFee;
    private BigDecimal sharedStarStorageFee;
    private BigDecimal sharedFbaIntegerernationalInboundFee;  // FBA国际物流货运费（API拼写错误）
    
    // ========== 成本相关 ==========
    private BigDecimal cgPriceTotal;
    private BigDecimal cgPriceAbsTotal;
    private Integer hasCgPriceDetail;
    private BigDecimal cgUnitPrice;
    private BigDecimal proportionOfCg;
    private BigDecimal cgTransportCostsTotal;
    private Integer hasCgTransportCostsDetail;
    private BigDecimal cgTransportUnitCosts;
    private BigDecimal proportionOfCgTransport;
    private BigDecimal totalCost;
    private BigDecimal proportionOfTotalCost;
    private BigDecimal cgOtherCostsTotal;
    private BigDecimal cgOtherUnitCosts;
    private Integer hasCgOtherCostsDetail;
    private BigDecimal proportionOfCgOtherCosts;
    
    // ========== 利润相关 ==========
    private BigDecimal grossProfit;
    private BigDecimal grossProfitTax;
    private BigDecimal grossProfitIncome;
    private BigDecimal grossRate;
    
    // ========== 其他费用 ==========
    private List<OtherFeeInfo> otherFeeStr;
    private BigDecimal customOrderFee;
    private BigDecimal customOrderFeePrincipal;
    private BigDecimal customOrderFeeCommission;
    
    // ========== 交易状态 ==========
    private String transactionStatus;
    private String transactionStatusCode;
    
    // ========== 其他 ==========
    private Integer fbaInventoryCreditQuantity;  // 修改为Integer类型
    private Integer disposalQuantity;
    private Integer removalQuantity;
    private BigDecimal others;
    private BigDecimal adjustments;  // 调整费用
    private BigDecimal totalPlatformOtherFee;  // 平台其他费
    
    /**
     * 其他费用信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OtherFeeInfo {
        private Long otherFeeTypeId;
        private String otherFeeName;
        private BigDecimal feeAllocation;
    }
}