package cn.iocoder.yudao.module.amazon.dal.dataobject.pricerecords;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 亚马逊产品价格记录 DO
 *
 * @author Demons
 */
@TableName("amazon_price_records")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRecordsDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 产品识别码
     */
    private String asin;
    /**
     * 系列名称
     */
    private String spu;
    /**
     * 标题
     */
    private String title;
    /**
     * 图片URL
     */
    private String imageUrl;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 会员价
     */
    private BigDecimal primePrice;
    /**
     * 优惠券折扣
     */
    private String couponDiscount;
    /**
     * 优惠后价格
     */
    private BigDecimal couponPrice;
    /**
     * 货币类型
     */
    private String currency;
    /**
     * 库存状态
     */
    private String availability;
    /**
     * 频繁退货标志
     */
    private Boolean frequentlyReturned;
    /**
     * 爬取日期
     */
    private LocalDate scrapeDate;
    /**
     * 用户ID
     */
    private Long userId;


}