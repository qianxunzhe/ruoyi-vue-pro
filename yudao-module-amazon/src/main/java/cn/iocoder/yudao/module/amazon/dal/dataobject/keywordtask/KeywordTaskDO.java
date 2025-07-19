package cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask;

import cn.iocoder.yudao.module.amazon.utils.JsonbTypeHandler;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 关键词排名任务 DO
 *
 * @author 芋道源码
 */
@TableName("amazon_keyword_task")
@KeySequence("amazon_keyword_task_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordTaskDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 关键词列表
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> keywords;
    /**
     * 城市列表
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> cities;
    /**
     * ASIN列表
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> asins;
    /**
     * Cron表达式
     */
    private String cronTab;
    /**
     * 启用状态
     */
    private Integer enabled;
    /**
     * 任务类型
     */
    private Integer taskType;
    /**
     * 任务共享状态
     */
    private Integer shared;
    /**
     * 任务描述
     */
    private String description;

    /***
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    private Integer scraperType;
}