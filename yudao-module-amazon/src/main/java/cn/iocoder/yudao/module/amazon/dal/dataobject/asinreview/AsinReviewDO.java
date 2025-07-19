package cn.iocoder.yudao.module.amazon.dal.dataobject.asinreview;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * Amazon ASIN商品评论数据 DO
 *
 * @author Demons
 */
@TableName("amazon_asin_review")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsinReviewDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * Amazon标准识别码
     */
    private String asin;
    /**
     * 父级ASIN（用于变体商品）
     */
    private String parentAsin;
    /**
     * 数据采集任务ID
     */
    private Integer taskId;
    /**
     * 评论总数
     */
    private Integer reviewNum;
    /**
     * 平均评分（1-5分）
     */
    private BigDecimal score;
    /**
     * 评级总数
     */
    private Integer ratings;
    /**
     * 备注
     */
    private String remark;

    /**
     * 最后更新的日期（用于标识最后一次更新的数据日期）
     */
    private LocalDateTime lastUpdateDate;

    /**
     * 是否初始化完成（0:未完成 1:已完成历史数据初始化）
     */
    private Integer initialized;

    /**
     * 记录日期（用于区分不同日期的数据）
     */
    private LocalDate recordDate;

    /**
     * 当日新增评论数（相比前一天的增量）
     */
    private Integer reviewIncrement;

    /**
     * 是否为初始化数据（true:初始化数据 false:增量数据）
     */
    private Boolean isInitial;
}