package cn.iocoder.yudao.module.amazon.dal.dataobject.reviewsnapshots;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.module.amazon.framework.mybatis.type.JsonbTypeHandler;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 亚马逊评论快照 DO
 *
 * @author Demons
 */
@TableName("amazon_review_snapshots")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSnapshotsDO extends BaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 亚马逊商品编号
     */
    private String asin;
    /**
     * 快照日期
     */
    private LocalDate snapshotDate;
    /**
     * 产品编号
     */
    private String spu;
    /**
     * 平均评分(1.0-5.0)
     */
    private BigDecimal rating;
    /**
     * 评分总数
     */
    private Integer ratingsCount;
    /**
     * 评论总数
     */
    private Integer reviewsCount;
    /**
     * 星级分布百分比
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> starDistribution;
    /**
     * 各星级评论数量
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> reviewStarCounts;
    /**
     * 各星级评论百分比
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> reviewStarPercentages;
    /**
     * 用户ID
     */
    private Integer userId;

    private Long tenantId;


}