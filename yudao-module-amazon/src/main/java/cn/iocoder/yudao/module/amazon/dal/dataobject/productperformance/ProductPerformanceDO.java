package cn.iocoder.yudao.module.amazon.dal.dataobject.productperformance;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 亚马逊产品表现数据 DO
 *
 * @author 芋道源码
 */
@TableName(value = "amazon_product_performance", autoResultMap = true)
@KeySequence("amazon_product_performance_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPerformanceDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;

    /**
     * ASIN
     */
    private String asin;

    /**
     * 父ASIN
     */
    private String parentAsin;

    /**
     * MSKU（seller_sku）
     */
    private String msku;

    /**
     * SKU（local_sku）
     */
    private String sku;

    /**
     * SPU（产品编码）
     */
    private String spu;

    /**
     * 店铺ID
     */
    private Long sid;

    /**
     * 站点ID
     */
    private Integer mid;

    /**
     * 店铺名称
     */
    private String sellerName;

    /**
     * 国家
     */
    private String country;

    /**
     * 货币类型
     */
    private String currencyCode;

    /**
     * 币种符号
     */
    private String currencyIcon;

    // ========== 基础信息 ==========
    /**
     * 产品标题
     */
    private String itemName;

    /**
     * 缩略图地址
     */
    private String smallImageUrl;

    /**
     * 亚马逊前台地址
     */
    private String amazonUrl;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 商品状态：-1未同步 1active 0inactive 2incomplete
     */
    private Integer status;

    // ========== 销售数据 ==========
    /**
     * 销量
     */
    private Integer volume;

    /**
     * 订单量
     */
    private Integer orderItems;

    /**
     * 销售额
     */
    private BigDecimal amount;

    /**
     * 净销售额
     */
    private BigDecimal netAmount;

    /**
     * 销售均价
     */
    private BigDecimal avgCustomPrice;

    /**
     * 平均销量
     */
    private BigDecimal avgVolume;

    // ========== 环比数据 ==========
    /**
     * 环比销量
     */
    private Integer volumeChain;

    /**
     * 销量环比
     */
    private BigDecimal volumeChainRatio;

    /**
     * 环比销售额
     */
    private BigDecimal amountChain;

    /**
     * 销售额环比
     */
    private BigDecimal amountChainRatio;

    /**
     * 环比订单量
     */
    private Integer orderItemsChain;

    /**
     * 订单量环比
     */
    private BigDecimal orderChainRatio;

    // ========== 促销数据 ==========
    /**
     * 促销销量
     */
    private Integer promotionVolume;

    /**
     * 促销销售额
     */
    private BigDecimal promotionAmount;

    /**
     * 促销订单量
     */
    private Integer promotionOrderItems;

    /**
     * 促销折扣
     */
    private BigDecimal promotionDiscount;

    // ========== 毛利数据 ==========
    /**
     * 结算毛利润
     */
    private BigDecimal grossProfit;

    /**
     * 订单毛利润
     */
    private BigDecimal predictGrossProfit;

    /**
     * 结算毛利率
     */
    private BigDecimal grossMargin;

    /**
     * 订单毛利率
     */
    private BigDecimal predictGrossMargin;

    /**
     * ROI
     */
    private BigDecimal roi;

    // ========== 评价数据 ==========
    /**
     * 评论数
     */
    private Integer reviewsCount;

    /**
     * 评分
     */
    private BigDecimal avgStar;

    /**
     * 前一个评分
     */
    private BigDecimal prevStar;

    /**
     * 留评率
     */
    private BigDecimal commentRate;

    // ========== 退货数据 ==========
    /**
     * 退款量
     */
    private Integer returnCount;

    /**
     * 退款率
     */
    private BigDecimal returnRate;

    /**
     * 退货量
     */
    private Integer returnGoodsCount;

    /**
     * 退货率
     */
    private BigDecimal returnGoodsRate;

    /**
     * 退款金额
     */
    private BigDecimal returnAmount;

    // ========== 库存数据 ==========
    /**
     * FBA可售
     */
    private Integer afnFulfillableQuantity;

    /**
     * FBA入库中
     */
    private Integer afnInboundReceivingQuantity;

    /**
     * FBA在途
     */
    private Integer afnInboundShippedQuantity;

    /**
     * FBA计划入库
     */
    private Integer afnInboundWorkingQuantity;

    /**
     * FBA不可售
     */
    private Integer afnUnsellableQuantity;

    /**
     * 调仓中
     */
    private Integer reservedFcProcessing;

    /**
     * 待调仓
     */
    private Integer reservedFcTransfers;

    /**
     * FBM可售
     */
    private Integer fbmQuantity;

    /**
     * 待发货
     */
    private Integer reservedCustomerorders;

    /**
     * 实际在途
     */
    private Integer stockUpNum;

    /**
     * 可售预估天数
     */
    private Integer availableDays;

    /**
     * FBM可售天数
     */
    private Integer fbmAvailableDays;

    /**
     * 月库销比
     */
    private BigDecimal monthStockSalesRatio;

    /**
     * 存销比
     */
    private BigDecimal inventorySalesRatio;

    // ========== 流量数据 ==========
    /**
     * 点击量
     */
    private Integer clicks;

    /**
     * Sessions-Browser
     */
    private Integer sessions;

    /**
     * Sessions-Mobile
     */
    private Integer sessionsMobile;

    /**
     * Sessions-Total
     */
    private Integer sessionsTotal;

    /**
     * PV-Browser
     */
    private Integer pageViews;

    /**
     * PV-Mobile
     */
    private Integer pageViewsMobile;

    /**
     * PV-Total
     */
    private Integer pageViewsTotal;

    /**
     * Buybox占比
     */
    private BigDecimal buyBoxPercentage;

    // ========== 转化率数据 ==========
    /**
     * 转化率
     */
    private BigDecimal cvr;

    /**
     * 点击率
     */
    private BigDecimal ctr;

    /**
     * 销量CVR
     */
    private BigDecimal volumeCvr;

    /**
     * 广告CVR
     */
    private BigDecimal adCvr;

    // ========== 广告数据 ==========
    /**
     * 曝光量
     */
    private Integer impressions;

    /**
     * 广告点击量
     */
    private Integer adClicks;

    /**
     * 广告订单量
     */
    private Integer adOrderQuantity;

    /**
     * 广告销售额
     */
    private BigDecimal adSalesAmount;

    /**
     * 广告花费
     */
    private BigDecimal spend;

    /**
     * CPC
     */
    private BigDecimal cpc;

    /**
     * CPM
     */
    private BigDecimal cpm;

    /**
     * CPO
     */
    private BigDecimal cpo;

    /**
     * ACOS
     */
    private BigDecimal acos;

    /**
     * ACoAS
     */
    private BigDecimal acoas;

    /**
     * ROAS
     */
    private BigDecimal roas;

    /**
     * ASoAS
     */
    private BigDecimal asoas;

    /**
     * 广告订单量占比
     */
    private BigDecimal advRate;

    /**
     * TACOS
     */
    private BigDecimal tacos;

    // ========== 广告细分数据 ==========
    /**
     * SP广告费
     */
    private BigDecimal adsSpCost;

    /**
     * SP广告销售额
     */
    private BigDecimal adsSpSales;

    /**
     * SD广告费
     */
    private BigDecimal adsSdCost;

    /**
     * SD广告销售额
     */
    private BigDecimal adsSdSales;

    /**
     * SB广告费
     */
    private BigDecimal sharedAdsSbCost;

    /**
     * SB广告销售额
     */
    private BigDecimal sharedAdsSbSales;

    /**
     * SBV广告费
     */
    private BigDecimal sharedAdsSbvCost;

    /**
     * SBV广告销售额
     */
    private BigDecimal sharedAdsSbvSales;

    /**
     * 差异分摊
     */
    private BigDecimal sharedCostOfAdvertising;

    /**
     * 直接成交销售额
     */
    private BigDecimal adDirectSalesAmount;

    /**
     * 直接成交订单量
     */
    private Integer adDirectOrderQuantity;

    // ========== 排名数据 ==========
    /**
     * 大类排名
     */
    private Integer cateRank;

    /**
     * 上一次大类排名
     */
    private Integer prevCateRank;

    /**
     * 大类排名分类
     */
    private String rankCategory;

    /**
     * 小类排名（第一个小类的排名）
     */
    @TableField("small_cate_rank")
    private Integer smallCateRank;
    
    /**
     * 上一次小类排名
     */
    @TableField("prev_small_cate_rank")
    private Integer prevSmallCateRank;
    
    /**
     * 小类排名分类
     */
    @TableField("small_rank_category")
    private String smallRankCategory;
    
    /**
     * 小类排名详细信息JSON（包含所有小类的排名）
     */
    @TableField(value = "small_cate_rank_detail", typeHandler = JacksonTypeHandler.class)
    private List<SmallCateRank> smallCateRankDetail;

    // ========== 其他字段 ==========
    /**
     * 分类JSON数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> categories;

    /**
     * 品牌JSON数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> brands;

    /**
     * 负责人JSON数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> principalNames;

    /**
     * 开发人JSON数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> developerNames;

    /**
     * 供应商JSON数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> suppliers;

    /**
     * 属性JSON数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> attributes;

    /**
     * 标签信息JSON数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<TagSet> tagSet;

    /**
     * 价格列表JSON
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<PriceInfo> priceList;

    // ========== SKU维度特有字段 ==========
    /**
     * 品名
     */
    private String localName;

    /**
     * 采购成本
     */
    private BigDecimal cgPrice;

    /**
     * 可用货值
     */
    private BigDecimal whsValue;

    /**
     * 本地可用
     */
    private Integer localQuantity;

    /**
     * 海外仓可用
     */
    private Integer overseaQuantity;

    /**
     * 平均售价
     */
    private BigDecimal avgLandedPrice;

    /**
     * 型号JSON数组
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> model;

    // ========== 业务字段 ==========
    /**
     * 汇总维度：asin/parent_asin/msku/sku
     */
    private String summaryField;

    /**
     * 数据开始日期
     */
    private LocalDate startDate;

    /**
     * 数据结束日期
     */
    private LocalDate endDate;

    /**
     * 同步时间
     */
    private LocalDateTime syncTime;

    // ========== 运营分析扩展字段 ==========
    /**
     * Prime价格
     */
    private BigDecimal primePrice;

    /**
     * Coupon价格
     */
    private BigDecimal couponPrice;

    /**
     * Coupon折扣率
     */
    private BigDecimal couponDiscount;

    /**
     * 目标订单
     */
    private Integer targetOrders;

    /**
     * 促销活动
     */
    private String promotionActivity;

    /**
     * 关键词数量
     */
    private Integer keywordCount;

    /**
     * P1关键词数量
     */
    @TableField("p1_keywords")
    private Integer p1Keywords;

    /**
     * P2关键词数量
     */
    @TableField("p2_keywords")
    private Integer p2Keywords;

    /**
     * P3关键词数量
     */
    @TableField("p3_keywords")
    private Integer p3Keywords;

    /**
     * 总流量
     */
    private Integer totalTraffic;

    /**
     * 总销售额
     */
    private BigDecimal totalSalesAmount;

    /**
     * 0-90天库龄
     */
    @TableField("inventory_age_0_90")
    private Integer inventoryAge0To90;

    /**
     * 91-180天库龄
     */
    @TableField("inventory_age_91_180")
    private Integer inventoryAge91To180;

    /**
     * 180+天库龄
     */
    @TableField("inventory_age_180_plus")
    private Integer inventoryAge180Plus;
    
    /**
     * 181-270天库龄
     */
    @TableField("inv_age_181_to_270_days")
    private Integer invAge181To270Days;
    
    /**
     * 271-365天库龄
     */
    @TableField("inv_age_271_to_365_days")
    private Integer invAge271To365Days;
    
    /**
     * 365+天库龄
     */
    @TableField("inv_age_365_plus_days")
    private Integer invAge365PlusDays;
    
    /**
     * 记录日期
     */
    @TableField("record_date")
    private LocalDate recordDate;

    /**
     * 预计冗余商品
     */
    private Integer expectedRedundantProducts;

    /**
     * 30天仓储费
     */
    @TableField("storage_fee_30_days")
    private BigDecimal storageFee30Days;

    /**
     * 180-365天收费数量
     */
    @TableField("storage_fee_180_365_quantity")
    private Integer storageFee180To365Quantity;

    /**
     * 180-365天收费金额
     */
    @TableField("storage_fee_180_365_amount")
    private BigDecimal storageFee180To365Amount;

    /**
     * 盈利平衡TACOS
     */
    private BigDecimal breakEvenTacos;

    /**
     * 运营分析
     */
    private String operationalAnalysis;
    
    /**
     * 租户编号
     */
    private Long tenantId = 1L;

    /**
     * 小类排名信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmallCateRank {
        /**
         * 类别
         */
        private String category;
        /**
         * 排名
         */
        private Integer rank;
        /**
         * 上一次排名
         */
        private Integer prevRank;
    }

    /**
     * 标签信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TagSet {
        /**
         * 标签ID
         */
        private String globalTagId;
        /**
         * 标签名
         */
        private String tagName;
        /**
         * 标签颜色
         */
        private String color;
    }

    /**
     * 价格信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceInfo {
        /**
         * 品名
         */
        private String localName;
        /**
         * SKU
         */
        private String localSku;
        /**
         * MSKU
         */
        private String sellerSku;
        /**
         * 价格
         */
        private BigDecimal price;
        /**
         * 国家
         */
        private String country;
        /**
         * 店铺名
         */
        private String sellerName;
        /**
         * 站点ID
         */
        private Integer mid;
        /**
         * 店铺ID
         */
        private Long sid;
        /**
         * 销量
         */
        private Integer volume;
        /**
         * 缩略图地址
         */
        private String smallImageUrl;
        /**
         * 商品状态
         */
        private Integer status;
    }
}