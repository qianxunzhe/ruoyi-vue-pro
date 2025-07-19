package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 亚马逊Listing信息DTO
 *
 * @author 芋道源码
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AmazonListingDTO {

    /**
     * 亚马逊定义的listing的id
     */
    @JsonProperty("listing_id")
    private String listingId;

    /**
     * 店铺id
     */
    @JsonProperty("sid")
    private Long sid;

    /**
     * 国家
     */
    @JsonProperty("marketplace")
    private String marketplace;

    /**
     * MSKU
     */
    @JsonProperty("seller_sku")
    private String sellerSku;

    /**
     * FNSKU
     */
    @JsonProperty("fnsku")
    private String fnsku;

    /**
     * ASIN
     */
    @JsonProperty("asin")
    private String asin;

    /**
     * 父ASIN
     */
    @JsonProperty("parent_asin")
    private String parentAsin;

    /**
     * 商品缩略图地址
     */
    @JsonProperty("small_image_url")
    private String smallImageUrl;

    /**
     * 状态：0 停售，1 在售
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 是否删除：0 否，1 是
     */
    @JsonProperty("is_delete")
    private Integer isDelete;

    /**
     * 标题
     */
    @JsonProperty("item_name")
    private String itemName;

    /**
     * 本地产品SKU
     */
    @JsonProperty("local_sku")
    private String localSku;

    /**
     * 品名
     */
    @JsonProperty("local_name")
    private String localName;

    /**
     * 币种
     */
    @JsonProperty("currency_code")
    private String currencyCode;

    /**
     * 价格（不包含促销，运费，积分）
     */
    @JsonProperty("price")
    private String price;

    /**
     * 总价（包含了促销、运费、积分）
     */
    @JsonProperty("landed_price")
    private String landedPrice;

    /**
     * 优惠价
     */
    @JsonProperty("listing_price")
    private String listingPrice;

    /**
     * 运费
     */
    @JsonProperty("shipping")
    private String shipping;

    /**
     * 积分，日本站才有
     */
    @JsonProperty("points")
    private String points;

    /**
     * FBM库存
     */
    @JsonProperty("quantity")
    private Integer quantity;

    /**
     * FBA可售
     */
    @JsonProperty("afn_fulfillable_quantity")
    private Integer afnFulfillableQuantity;

    /**
     * FBA不可售
     */
    @JsonProperty("afn_unsellable_quantity")
    private Integer afnUnsellableQuantity;

    /**
     * 待调仓
     */
    @JsonProperty("reserved_fc_transfers")
    private Integer reservedFcTransfers;

    /**
     * 调仓中
     */
    @JsonProperty("reserved_fc_processing")
    private Integer reservedFcProcessing;

    /**
     * 待发货
     */
    @JsonProperty("reserved_customerorders")
    private Integer reservedCustomerorders;

    /**
     * 在途
     */
    @JsonProperty("afn_inbound_shipped_quantity")
    private Integer afnInboundShippedQuantity;

    /**
     * 计划入库
     */
    @JsonProperty("afn_inbound_working_quantity")
    private Integer afnInboundWorkingQuantity;

    /**
     * 入库中
     */
    @JsonProperty("afn_inbound_receiving_quantity")
    private Integer afnInboundReceivingQuantity;

    /**
     * 商品创建时间
     */
    @JsonProperty("open_date")
    private String openDate;

    /**
     * 商品创建时间，格式：Y-m-d H:i:s+时区
     */
    @JsonProperty("open_date_display")
    private String openDateDisplay;

    /**
     * All Listing报表更新时间 (注意：此为零时区时间)
     */
    @JsonProperty("listing_update_date")
    private String listingUpdateDate;

    /**
     * 排名
     */
    @JsonProperty("seller_rank")
    private Integer sellerRank;

    /**
     * 亚马逊品牌
     */
    @JsonProperty("seller_brand")
    private String sellerBrand;

    /**
     * 排名所属的类别
     */
    @JsonProperty("seller_category")
    private String sellerCategory;

    /**
     * 评论条数
     */
    @JsonProperty("review_num")
    private Integer reviewNum;

    /**
     * 星级评分
     */
    @JsonProperty("last_star")
    private String lastStar;

    /**
     * 配送方式
     */
    @JsonProperty("fulfillment_channel_type")
    private String fulfillmentChannelType;

    /**
     * 负责人信息
     */
    @JsonProperty("principal_info")
    private List<PrincipalInfoDTO> principalInfo;

    /**
     * 排名所属的类别（新版）
     */
    @JsonProperty("seller_category_new")
    private List<String> sellerCategoryNew;

    /**
     * 配对更新时间 (注意：此为北京时间)
     */
    @JsonProperty("pair_update_time")
    private String pairUpdateTime;

    /**
     * 首单时间，格式：Y-m-d
     */
    @JsonProperty("first_order_time")
    private String firstOrderTime;

    /**
     * 开售时间，格式：Y-m-d
     */
    @JsonProperty("on_sale_time")
    private String onSaleTime;

    /**
     * 商品类型，1-非低价商店 ，2-低价商店商品
     */
    @JsonProperty("store_type")
    private Integer storeType;

    /**
     * 销量-7天
     */
    @JsonProperty("total_volume")
    private String totalVolume;

    /**
     * 销量-昨天
     */
    @JsonProperty("yesterday_volume")
    private String yesterdayVolume;

    /**
     * 销量-14天
     */
    @JsonProperty("fourteen_volume")
    private String fourteenVolume;

    /**
     * 销量-30天
     */
    @JsonProperty("thirty_volume")
    private String thirtyVolume;

    /**
     * 销售额-昨天
     */
    @JsonProperty("yesterday_amount")
    private String yesterdayAmount;

    /**
     * 销售额-7天
     */
    @JsonProperty("seven_amount")
    private String sevenAmount;

    /**
     * 销售额-14天
     */
    @JsonProperty("fourteen_amount")
    private String fourteenAmount;

    /**
     * 销售额-30天
     */
    @JsonProperty("thirty_amount")
    private String thirtyAmount;

    /**
     * 日均销量-7日
     */
    @JsonProperty("average_seven_volume")
    private String averageSevenVolume;

    /**
     * 日均销量-14日
     */
    @JsonProperty("average_fourteen_volume")
    private String averageFourteenVolume;

    /**
     * 日均销量-30日
     */
    @JsonProperty("average_thirty_volume")
    private String averageThirtyVolume;

    /**
     * 尺寸信息
     */
    @JsonProperty("dimension_info")
    private List<DimensionInfoDTO> dimensionInfo;

    /**
     * 小类排名信息
     */
    @JsonProperty("small_rank")
    private List<SmallRankDTO> smallRank;

    /**
     * 全局标签
     */
    @JsonProperty("global_tags")
    private List<GlobalTagDTO> globalTags;
} 