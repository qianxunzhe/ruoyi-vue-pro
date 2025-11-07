package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 领星产品表现API响应DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductPerformanceDTO {
    
    /**
     * 父ASIN列表
     */
    @JsonProperty("parent_asins")
    private List<ParentAsinInfo> parentAsins;
    
    /**
     * ASIN列表
     */
    @JsonProperty("asins")
    private List<AsinInfo> asins;
    
    /**
     * 价格列表
     */
    @JsonProperty("price_list")
    private List<PriceInfo> priceList;
    
    /**
     * 小类排名
     */
    @JsonProperty("small_cate_rank")
    private List<SmallCateRank> smallCateRank;
    
    /**
     * 标题
     */
    @JsonProperty("item_name")
    private String itemName;
    
    /**
     * SKU
     */
    @JsonProperty("sku")
    private String sku;
    
    /**
     * 品名
     */
    @JsonProperty("local_name")
    private String localName;
    
    /**
     * 大类排名
     */
    @JsonProperty("cate_rank")
    private Integer cateRank;
    
    /**
     * 大类排名分类
     */
    @JsonProperty("rank_category")
    private String rankCategory;
    
    /**
     * 上一次大类排名
     */
    @JsonProperty("prev_cate_rank")
    private Integer prevCateRank;
    
    /**
     * 币种符号
     */
    @JsonProperty("currency_icon")
    private String currencyIcon;
    
    /**
     * 货币类型
     */
    @JsonProperty("currency_code")
    private String currencyCode;
    
    /**
     * 采购成本币种符号
     */
    @JsonProperty("cg_price_currency_icon")
    private String cgPriceCurrencyIcon;
    
    /**
     * 排名更新时间
     */
    @JsonProperty("ranking_update_time")
    private String rankingUpdateTime;
    
    /**
     * 店铺/国家列表
     */
    @JsonProperty("seller_store_countries")
    private List<SellerStoreCountry> sellerStoreCountries;
    
    /**
     * 分类
     */
    @JsonProperty("categories")
    private List<String> categories;
    
    /**
     * 品牌
     */
    @JsonProperty("brands")
    private List<String> brands;
    
    /**
     * 负责人
     */
    @JsonProperty("principal_names")
    private List<String> principalNames;
    
    /**
     * 开发人
     */
    @JsonProperty("developer_names")
    private List<String> developerNames;
    
    /**
     * 属性
     */
    @JsonProperty("attributes")
    private List<String> attributes;
    
    /**
     * 月库销比
     */
    @JsonProperty("month_stock_sales_ratio")
    private BigDecimal monthStockSalesRatio;
    
    /**
     * 销量
     */
    @JsonProperty("volume")
    private Integer volume;
    
    /**
     * 订单量
     */
    @JsonProperty("order_items")
    private Integer orderItems;
    
    /**
     * 销售额
     */
    @JsonProperty("amount")
    private BigDecimal amount;
    
    /**
     * 销量环比
     */
    @JsonProperty("volume_chain_ratio")
    private BigDecimal volumeChainRatio;
    
    /**
     * 环比销量
     */
    @JsonProperty("volume_chain")
    private Integer volumeChain;
    
    /**
     * 销售额环比
     */
    @JsonProperty("amount_chain_ratio")
    private BigDecimal amountChainRatio;
    
    /**
     * 环比销售额
     */
    @JsonProperty("amount_chain")
    private BigDecimal amountChain;
    
    /**
     * 订单量环比
     */
    @JsonProperty("order_chain_ratio")
    private BigDecimal orderChainRatio;
    
    /**
     * 环比订单量
     */
    @JsonProperty("order_items_chain")
    private Integer orderItemsChain;
    
    /**
     * B2B销量
     */
    @JsonProperty("b2b_volume")
    private Integer b2bVolume;
    
    /**
     * B2B销售额
     */
    @JsonProperty("b2b_amount")
    private BigDecimal b2bAmount;
    
    /**
     * B2B订单量
     */
    @JsonProperty("b2b_order_items")
    private Integer b2bOrderItems;
    
    /**
     * 结算毛利润
     */
    @JsonProperty("gross_profit")
    private BigDecimal grossProfit;
    
    /**
     * 订单毛利润
     */
    @JsonProperty("predict_gross_profit")
    private BigDecimal predictGrossProfit;
    
    /**
     * 结算毛利率
     */
    @JsonProperty("gross_margin")
    private BigDecimal grossMargin;
    
    /**
     * 订单毛利率
     */
    @JsonProperty("predict_gross_margin")
    private BigDecimal predictGrossMargin;
    
    /**
     * ROI
     */
    @JsonProperty("roi")
    private BigDecimal roi;
    
    /**
     * 促销销量
     */
    @JsonProperty("promotion_volume")
    private Integer promotionVolume;
    
    /**
     * 促销销售额
     */
    @JsonProperty("promotion_amount")
    private BigDecimal promotionAmount;
    
    /**
     * 促销订单量
     */
    @JsonProperty("promotion_order_items")
    private Integer promotionOrderItems;
    
    /**
     * 促销折扣
     */
    @JsonProperty("promotion_discount")
    private BigDecimal promotionDiscount;
    
    /**
     * 评论数
     */
    @JsonProperty("reviews_count")
    private Integer reviewsCount;
    
    /**
     * 退款量
     */
    @JsonProperty("return_count")
    private Integer returnCount;
    
    /**
     * 退款率
     */
    @JsonProperty("return_rate")
    private BigDecimal returnRate;
    
    /**
     * FBA可售
     */
    @JsonProperty("afn_fulfillable_quantity")
    private Integer afnFulfillableQuantity;
    
    /**
     * FBA入库中
     */
    @JsonProperty("afn_inbound_receiving_quantity")
    private Integer afnInboundReceivingQuantity;
    
    /**
     * FBA在途
     */
    @JsonProperty("afn_inbound_shipped_quantity")
    private Integer afnInboundShippedQuantity;
    
    /**
     * FBA计划入库
     */
    @JsonProperty("afn_inbound_working_quantity")
    private Integer afnInboundWorkingQuantity;
    
    /**
     * FBA不可售
     */
    @JsonProperty("afn_unsellable_quantity")
    private Integer afnUnsellableQuantity;
    
    /**
     * 调仓中
     */
    @JsonProperty("reserved_fc_processing")
    private Integer reservedFcProcessing;
    
    /**
     * 待调仓
     */
    @JsonProperty("reserved_fc_transfers")
    private Integer reservedFcTransfers;
    
    /**
     * FBM可售
     */
    @JsonProperty("fbm_quantity")
    private Integer fbmQuantity;
    
    /**
     * 待发货
     */
    @JsonProperty("reserved_customerorders")
    private Integer reservedCustomerorders;
    
    /**
     * 实际在途
     */
    @JsonProperty("stock_up_num")
    private Integer stockUpNum;
    
    /**
     * 点击量
     */
    @JsonProperty("clicks")
    private Integer clicks;
    
    /**
     * 可售预估天数
     */
    @JsonProperty("available_days")
    private Integer availableDays;
    
    /**
     * FBM可售天数
     */
    @JsonProperty("fbm_available_days")
    private Integer fbmAvailableDays;
    
    /**
     * 评分
     */
    @JsonProperty("avg_star")
    private BigDecimal avgStar;
    
    /**
     * 前一个评分
     */
    @JsonProperty("prev_star")
    private BigDecimal prevStar;
    
    /**
     * 留评率
     */
    @JsonProperty("comment_rate")
    private BigDecimal commentRate;
    
    /**
     * Sessions-Browser
     */
    @JsonProperty("sessions")
    private Integer sessions;
    
    /**
     * Sessions-Mobile
     */
    @JsonProperty("sessions_mobile")
    private Integer sessionsMobile;
    
    /**
     * Sessions-Total
     */
    @JsonProperty("sessions_total")
    private Integer sessionsTotal;
    
    /**
     * Buybox
     */
    @JsonProperty("buy_box_percentage")
    private BigDecimal buyBoxPercentage;
    
    /**
     * PV-Browser
     */
    @JsonProperty("page_views")
    private Integer pageViews;
    
    /**
     * PV-Mobile
     */
    @JsonProperty("page_views_mobile")
    private Integer pageViewsMobile;
    
    /**
     * PV-Total
     */
    @JsonProperty("page_views_total")
    private Integer pageViewsTotal;
    
    /**
     * 广告订单量占比
     */
    @JsonProperty("adv_rate")
    private BigDecimal advRate;
    
    /**
     * 广告CVR
     */
    @JsonProperty("ad_cvr")
    private BigDecimal adCvr;
    
    /**
     * 销量CVR
     */
    @JsonProperty("volume_cvr")
    private BigDecimal volumeCvr;
    
    /**
     * CVR
     */
    @JsonProperty("cvr")
    private BigDecimal cvr;
    
    /**
     * CTR
     */
    @JsonProperty("ctr")
    private BigDecimal ctr;
    
    /**
     * ACoAS
     */
    @JsonProperty("acoas")
    private BigDecimal acoas;
    
    /**
     * ACOS
     */
    @JsonProperty("acos")
    private BigDecimal acos;
    
    /**
     * 是否有操作日志
     */
    @JsonProperty("has_oprator_log")
    private Boolean hasOpratorLog;
    
    /**
     * 退货量
     */
    @JsonProperty("return_goods_count")
    private Integer returnGoodsCount;
    
    /**
     * 退货率
     */
    @JsonProperty("return_goods_rate")
    private BigDecimal returnGoodsRate;
    
    /**
     * CPC
     */
    @JsonProperty("cpc")
    private BigDecimal cpc;
    
    /**
     * 广告花费
     */
    @JsonProperty("spend")
    private BigDecimal spend;
    
    /**
     * 差异分摊
     */
    @JsonProperty("shared_cost_of_advertising")
    private BigDecimal sharedCostOfAdvertising;
    
    /**
     * SB广告费
     */
    @JsonProperty("shared_ads_sb_cost")
    private BigDecimal sharedAdsSbCost;
    
    /**
     * SBV广告费
     */
    @JsonProperty("shared_ads_sbv_cost")
    private BigDecimal sharedAdsSbvCost;
    
    /**
     * SD广告费
     */
    @JsonProperty("ads_sd_cost")
    private BigDecimal adsSdCost;
    
    /**
     * SP广告费
     */
    @JsonProperty("ads_sp_cost")
    private BigDecimal adsSpCost;
    
    /**
     * ROAS
     */
    @JsonProperty("roas")
    private BigDecimal roas;
    
    /**
     * ASoAS
     */
    @JsonProperty("asoas")
    private BigDecimal asoas;
    
    /**
     * CPO
     */
    @JsonProperty("cpo")
    private BigDecimal cpo;
    
    /**
     * CPM
     */
    @JsonProperty("cpm")
    private BigDecimal cpm;
    
    /**
     * 广告销售额
     */
    @JsonProperty("ad_sales_amount")
    private BigDecimal adSalesAmount;
    
    /**
     * SP广告销售额
     */
    @JsonProperty("ads_sp_sales")
    private BigDecimal adsSpSales;
    
    /**
     * SD广告销售额
     */
    @JsonProperty("ads_sd_sales")
    private BigDecimal adsSdSales;
    
    /**
     * SB广告销售额
     */
    @JsonProperty("shared_ads_sb_sales")
    private BigDecimal sharedAdsSbSales;
    
    /**
     * SBV广告销售额
     */
    @JsonProperty("shared_ads_sbv_sales")
    private BigDecimal sharedAdsSbvSales;
    
    /**
     * 广告订单量
     */
    @JsonProperty("ad_order_quantity")
    private Integer adOrderQuantity;
    
    /**
     * 展示
     */
    @JsonProperty("impressions")
    private Integer impressions;
    
    /**
     * 店铺ID列表
     */
    @JsonProperty("sids")
    private List<Long> sids;
    
    /**
     * 净销售额
     */
    @JsonProperty("net_amount")
    private BigDecimal netAmount;
    
    /**
     * 缩略图地址
     */
    @JsonProperty("small_image_url")
    private String smallImageUrl;
    
    /**
     * 平均销量
     */
    @JsonProperty("avg_volume")
    private BigDecimal avgVolume;
    
    /**
     * 销售均价
     */
    @JsonProperty("avg_custom_price")
    private BigDecimal avgCustomPrice;
    
    /**
     * 运营日志数量
     */
    @JsonProperty("icon_num")
    private Integer iconNum;
    
    /**
     * SPU数据
     */
    @JsonProperty("spu_spu_names")
    private List<SpuInfo> spuSpuNames;
    
    /**
     * 采购成本
     */
    @JsonProperty("cg_price")
    private BigDecimal cgPrice;
    
    /**
     * 可用货值
     */
    @JsonProperty("whs_value")
    private BigDecimal whsValue;
    
    /**
     * 本地可用
     */
    @JsonProperty("local_quantity")
    private Integer localQuantity;
    
    /**
     * 海外仓可用
     */
    @JsonProperty("oversea_quantity")
    private Integer overseaQuantity;
    
    /**
     * 存销比
     */
    @JsonProperty("inventory_sales_ratio")
    private BigDecimal inventorySalesRatio;
    
    /**
     * 平均售价
     */
    @JsonProperty("avg_landed_price")
    private BigDecimal avgLandedPrice;
    
    /**
     * 供应商
     */
    @JsonProperty("suppliers")
    private List<String> suppliers;
    
    /**
     * 型号
     */
    @JsonProperty("model")
    private List<String> model;
    
    /**
     * 退款金额
     */
    @JsonProperty("return_amount")
    private BigDecimal returnAmount;
    
    /**
     * 产品创建时间
     */
    @JsonProperty("product_create_time")
    private String productCreateTime;
    
    /**
     * 直接成交销售额
     */
    @JsonProperty("ad_direct_sales_amount")
    private BigDecimal adDirectSalesAmount;
    
    /**
     * 直接成交订单量
     */
    @JsonProperty("ad_direct_order_quantity")
    private Integer adDirectOrderQuantity;
    
    /**
     * 可用库存数据
     */
    @JsonProperty("available_inventory")
    private AvailableInventory availableInventory;
    
    /**
     * 标签集合
     */
    @JsonProperty("tag_set")
    private List<TagInfo> tagSet;
    
    /**
     * 父ASIN信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ParentAsinInfo {
        @JsonProperty("amazon_url")
        private String amazonUrl;
        
        @JsonProperty("parent_asin")
        private String parentAsin;
        
        @JsonProperty("sid")
        private Long sid;
    }
    
    /**
     * ASIN信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AsinInfo {
        @JsonProperty("amazon_url")
        private String amazonUrl;
        
        @JsonProperty("asin")
        private String asin;
        
        @JsonProperty("sid")
        private Long sid;
    }
    
    /**
     * 价格信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriceInfo {
        @JsonProperty("local_name")
        private String localName;
        
        @JsonProperty("local_sku")
        private String localSku;
        
        @JsonProperty("seller_sku")
        private String sellerSku;
        
        @JsonProperty("price")
        private BigDecimal price;
        
        @JsonProperty("country")
        private String country;
        
        @JsonProperty("seller_name")
        private String sellerName;
        
        @JsonProperty("is_eur")
        private Integer isEur;
        
        @JsonProperty("mid")
        private Integer mid;
        
        @JsonProperty("sid")
        private Long sid;
        
        @JsonProperty("is_delete")
        private Integer isDelete;
        
        @JsonProperty("volume")
        private Integer volume;
        
        @JsonProperty("product_pic_url")
        private String productPicUrl;
        
        @JsonProperty("small_image_url")
        private String smallImageUrl;
        
        @JsonProperty("cid")
        private Integer cid;
        
        @JsonProperty("source_rate")
        private BigDecimal sourceRate;
        
        @JsonProperty("status")
        private Integer status;
    }
    
    /**
     * 小类排名
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SmallCateRank {
        @JsonProperty("category")
        private String category;
        
        @JsonProperty("prev_rank")
        private Integer prevRank;
        
        @JsonProperty("rank")
        private Integer rank;
    }
    
    /**
     * 店铺国家信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SellerStoreCountry {
        @JsonProperty("seller_name")
        private String sellerName;
        
        @JsonProperty("country")
        private String country;
    }
    
    /**
     * SPU信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SpuInfo {
        @JsonProperty("spu")
        private String spu;
        
        @JsonProperty("spu_name")
        private String spuName;
    }
    
    /**
     * 可用库存
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AvailableInventory {
        @JsonProperty("stock_up_num")
        private Integer stockUpNum;
        
        @JsonProperty("available_inventory")
        private Integer availableInventory;
        
        @JsonProperty("afn_unsellable_quantity")
        private Integer afnUnsellableQuantity;
        
        @JsonProperty("afn_inbound_shipped_quantity")
        private Integer afnInboundShippedQuantity;
        
        @JsonProperty("afn_inbound_working_quantity")
        private Integer afnInboundWorkingQuantity;
        
        @JsonProperty("afn_inbound_receiving_quantity")
        private Integer afnInboundReceivingQuantity;
        
        @JsonProperty("reserved_customerorders")
        private Integer reservedCustomerorders;
        
        @JsonProperty("reserved_fc_processing")
        private Integer reservedFcProcessing;
        
        @JsonProperty("reserved_fc_transfers")
        private Integer reservedFcTransfers;
        
        @JsonProperty("fbm_quantity")
        private Integer fbmQuantity;
        
        @JsonProperty("afn_fulfillable_quantity")
        private Integer afnFulfillableQuantity;
    }
    
    /**
     * 标签信息
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TagInfo {
        @JsonProperty("global_tag_id")
        private String globalTagId;
        
        @JsonProperty("tag_name")
        private String tagName;
        
        @JsonProperty("color")
        private String color;
    }
}