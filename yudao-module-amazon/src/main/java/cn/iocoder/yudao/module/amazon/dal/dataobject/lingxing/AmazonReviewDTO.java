package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

/**
 * 亚马逊评论DTO
 * 
 * @author 芋道源码
 */
@Data
public class AmazonReviewDTO {

    /**
     * 图片
     */
    @JsonProperty("small_image_url")
    private String smallImageUrl;

    /**
     * ASIN
     */
    @JsonProperty("asin")
    private String asin;

    /**
     * MSKU
     */
    @JsonProperty("seller_sku")
    private List<String> sellerSku;

    /**
     * 评级 - 星级
     */
    @JsonProperty("last_star")
    private Integer lastStar;

    /**
     * 评级 - 标题
     */
    @JsonProperty("last_title")
    private String lastTitle;

    /**
     * 点赞数
     */
    @JsonProperty("review_likes")
    private Integer reviewLikes;

    /**
     * Review ID
     */
    @JsonProperty("review_id")
    private String reviewId;

    /**
     * 评价内容
     */
    @JsonProperty("last_content")
    private String lastContent;

    /**
     * 父ASIN
     */
    @JsonProperty("parent_asin")
    private List<String> parentAsin;

    /**
     * 标题
     */
    @JsonProperty("item_name")
    private List<String> itemName;

    /**
     * 本地信息
     */
    @JsonProperty("local_info")
    private List<LocalInfoDTO> localInfo;

    /**
     * 买家信息
     */
    @JsonProperty("author")
    private String author;

    /**
     * VP标识
     */
    @JsonProperty("is_vp")
    private Integer isVp;

    /**
     * 店铺
     */
    @JsonProperty("seller_name")
    private List<String> sellerName;

    /**
     * 国家
     */
    @JsonProperty("marketplace")
    private String marketplace;

    /**
     * 评价时间
     */
    @JsonProperty("review_date")
    private String reviewDate;

    /**
     * 创建时间
     */
    @JsonProperty("create_time")
    private String createTime;

    /**
     * 更新时间
     */
    @JsonProperty("update_time")
    private String updateTime;

    /**
     * 买家邮箱
     */
    @JsonProperty("buyer_email")
    private List<String> buyerEmail;

    /**
     * 备注
     */
    @JsonProperty("remark")
    private String remark;

    /**
     * 处理状态：0 待处理，1 处理中，2 已完成
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 操作时间
     */
    @JsonProperty("crawl_date")
    private String crawlDate;

    /**
     * 订单号列表
     */
    @JsonProperty("amazon_order_list")
    private List<Object> amazonOrderList;

    /**
     * 标签
     */
    @JsonProperty("tags")
    private List<Object> tags;

    /**
     * 处理人
     */
    @JsonProperty("cs_principals")
    private List<Object> csPrincipals;
}