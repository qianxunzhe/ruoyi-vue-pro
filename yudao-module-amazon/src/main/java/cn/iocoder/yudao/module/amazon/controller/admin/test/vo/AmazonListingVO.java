package cn.iocoder.yudao.module.amazon.controller.admin.test.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AmazonListingVO {


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
         * 评论条数
         */
        @JsonProperty("review_num")
        private Integer reviewNum;

        /**
         * 星级评分
         */
        @JsonProperty("last_star")
        private String lastStar;
}
