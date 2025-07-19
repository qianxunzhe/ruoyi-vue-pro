package cn.iocoder.yudao.module.amazon.dal.dataobject.listingprice;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 价格监控结果 DO
 *
 * @author Demons
 */
@TableName("amazon_listing_price")
@KeySequence("amazon_listing_price_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingPriceDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 任务执行ID
     */
    private Integer taskExecLogId;
    /**
     * 产品ASIN
     */
    private String asin;
    /**
     * 市场代码
     */
    private String marketplace;
    /**
     * BuyBox价格
     */
    private BigDecimal buyboxPrice;
    /**
     * 商品价格
     */
    private BigDecimal price;
    /**
     * Prime价格
     */
    private BigDecimal primePrice;
    /**
     * 优惠券价格
     */
    private BigDecimal couponPrice;
    /**
     * 优惠券折扣
     */
    private String couponDiscount;
    /**
     * 促销价格
     */
    private BigDecimal dealPrice;
    /**
     * 促销信息
     */
    private String dealInfo;
    /**
     * FBA价格
     */
    private BigDecimal fbaPrice;
    /**
     * FBM价格
     */
    private BigDecimal fbmPrice;
    /**
     * 标价
     */
    private BigDecimal listPrice;

    /**
     *
     */
    private BigDecimal landedPrice;
    /**
     * 抓取时间
     */
    private LocalDateTime scrapedAt;
    /**
     * 备注
     */
    private String remark;

    private Long taskId;


}