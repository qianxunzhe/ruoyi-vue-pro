package cn.iocoder.yudao.module.amazon.dal.dataobject.profitreport;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.module.amazon.framework.mybatis.PostgresJsonbTypeHandler;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 亚马逊利润报表数据 DO
 *
 * @author Demons
 */
@TableName(value = "amazon_profit_report", autoResultMap = true)
// @KeySequence("amazon_profit_report_seq") // 注释掉，使用PostgreSQL的SERIAL自增
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfitReportDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO) // 使用数据库自增
    private Long id;
    /**
     * 同步日期（数据所属日期，按天）
     */
    private LocalDate syncDate;
    /**
     * ASIN
     */
    private String asin;
    /**
     * 父ASIN
     */
    private String parentAsin;
    /**
     * SKU
     */
    private String localSku;
    /**
     * SPU
     */
    private String spu;
    /**
     * SID
     */
    private String sid;
    /**
     * 店铺名称
     */
    private String storeName;
    /**
     * 国家代码
     */
    private String countryCode;
    /**
     * 总销售数量
     */
    private Integer totalSalesQuantity;
    /**
     * FBA销售数量
     */
    private Integer fbaSalesQuantity;
    /**
     * FBM销售数量
     */
    private Integer fbmSalesQuantity;
    /**
     * 退款数量
     */
    private Integer refundsQuantity;
    /**
     * FBA退货数量
     */
    private Integer fbaReturnsQuantity;
    /**
     * 总销售金额
     */
    private BigDecimal totalSalesAmount;
    /**
     * FBA销售金额
     */
    private BigDecimal fbaSaleAmount;
    /**
     * FBM销售金额
     */
    private BigDecimal fbmSaleAmount;
    /**
     * 总退款金额
     */
    private BigDecimal totalSalesRefunds;
    /**
     * 广告总销售额
     */
    private BigDecimal totalAdsSales;
    /**
     * 广告总花费
     */
    private BigDecimal totalAdsCost;
    /**
     * SP广告销售额
     */
    private BigDecimal adsSpSales;
    /**
     * SP广告花费
     */
    private BigDecimal adsSpCost;
    /**
     * SD广告销售额
     */
    private BigDecimal adsSdSales;
    /**
     * SD广告花费
     */
    private BigDecimal adsSdCost;
    /**
     * SB广告销售额
     */
    private BigDecimal adsSbSales;
    /**
     * SB广告花费
     */
    private BigDecimal adsSbCost;
    /**
     * 平台费用
     */
    private BigDecimal platformFee;
    /**
     * FBA总配送费用
     */
    private BigDecimal totalFbaDeliveryFee;
    /**
     * 总仓储费用
     */
    private BigDecimal totalStorageFee;
    /**
     * 推广费用
     */
    private BigDecimal promotionFee;
    /**
     * 采购成本总额
     */
    private BigDecimal cgPriceTotal;
    /**
     * 头程运输成本总额
     */
    private BigDecimal cgTransportCostsTotal;
    /**
     * 其他成本总额
     */
    private BigDecimal cgOtherCostsTotal;
    /**
     * 合计成本
     */
    private BigDecimal totalCost;
    /**
     * 毛利润
     */
    private BigDecimal grossProfit;
    /**
     * 毛利率
     */
    private BigDecimal grossRate;
    /**
     * 货币代码
     */
    private String currencyCode;
    /**
     * 交易状态
     */
    private String transactionStatus;
    /**
     * 产品本地名称/品名
     */
    private String localName;
    /**
     * 其他费用明细（JSON格式存储）
     */
    @TableField(typeHandler = PostgresJsonbTypeHandler.class)
    private String otherFeeStr;
    /**
     * 数据同步时间
     */
    private LocalDateTime syncTime;

    // ==================== 新增字段 ====================
    
    // 1. 收入相关字段
    /**
     * 买家运费
     */
    private BigDecimal shippingCredits;
    /**
     * 促销折扣
     */
    private BigDecimal promotionalRebates;
    /**
     * FBA库存赔偿
     */
    private BigDecimal fbaInventoryCredit;
    /**
     * COD货到付款
     */
    private BigDecimal cashOnDelivery;
    /**
     * 其他收入
     */
    private BigDecimal otherInAmount;
    /**
     * 包装收入
     */
    private BigDecimal giftWrapCredits;
    /**
     * 买家交易保障索赔额
     */
    private BigDecimal guaranteeClaims;
    /**
     * 积分抵减收入
     */
    private BigDecimal costOfPointsGranted;
    
    // 2. 退款相关字段
    /**
     * FBA销售退款额
     */
    private BigDecimal fbaSalesRefunds;
    /**
     * FBM销售退款额
     */
    private BigDecimal fbmSalesRefunds;
    /**
     * 费用退款额
     */
    private BigDecimal totalFeeRefunds;
    /**
     * 平台费退款额
     */
    private BigDecimal sellingFeeRefunds;
    /**
     * 发货费退款额
     */
    private BigDecimal fbaTransactionFeeRefunds;
    /**
     * 其他订单费退款额
     */
    private BigDecimal otherTransactionFeeRefunds;
    /**
     * 买家运费退款额
     */
    private BigDecimal shippingCreditRefunds;
    
    // 3. 费用相关字段（细分项）
    /**
     * FBA发货费
     */
    private BigDecimal fbaDeliveryFee;
    /**
     * FBA发货费(多渠道)
     */
    private BigDecimal mcFbaDeliveryFee;
    /**
     * 其他订单费用
     */
    private BigDecimal otherTransactionFees;
    /**
     * 调整费用
     */
    private BigDecimal adjustments;
    /**
     * 平台其他费
     */
    private BigDecimal totalPlatformOtherFee;
    
    // 4. 成本相关字段（单价）
    /**
     * 采购均价
     */
    private BigDecimal cgUnitPrice;
    /**
     * 头程均价
     */
    private BigDecimal cgTransportUnitCosts;
    
    // 5. 库存相关字段
    /**
     * 赔偿量
     */
    private Integer fbaInventoryCreditQuantity;
    
    // 6. 站外推广费相关字段
    /**
     * 订单其他费
     */
    private BigDecimal customOrderFee;
    /**
     * 站外推广费-本金
     */
    private BigDecimal customOrderFeePrincipal;
    /**
     * 站外推广费-佣金
     */
    private BigDecimal customOrderFeeCommission;
    
    // 7. 额外的业务字段
    /**
     * 退款率
     */
    private BigDecimal refundsRate;
    /**
     * FBA退货率
     */
    private BigDecimal fbaReturnsQuantityRate;
    
    // 8. 其他费用计算所需字段
    /**
     * 入库配置费（入仓手续费）
     */
    private BigDecimal sharedFbaInboundTransportationProgramFee;
    /**
     * FBA国际物流货运费
     */
    private BigDecimal sharedFbaIntegerernationalInboundFee;

}