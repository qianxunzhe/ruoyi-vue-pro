package cn.iocoder.yudao.module.amazon.dal.dataobject.taskexeclog;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * Amazon任务执行日志 DO
 *
 * @author Demons
 */
@TableName("amazon_task_exec_log")
@KeySequence("amazon_task_exec_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecLogDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 任务ID
     */
    private Long taskId;
    /**
     * 执行状态
     */
    private String status;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 执行时长(秒)
     */
    private Double durationSeconds;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 保存记录数
     */
    private Integer recordsSaved;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 邮编数量
     */
    private Integer zipcodeNum;
    /**
     * 关键词数量
     */
    private Integer keywordNum;
    /**
     * 爬取页面数量
     */
    private Integer pageNum;


}