package cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - Amazon任务执行日志分页 Request VO")
@Data
public class TaskExecLogPageReqVO extends PageParam {

    @Schema(description = "任务ID", example = "11863")
    private Long taskId;

    @Schema(description = "执行状态", example = "2")
    private String status;

    @Schema(description = "开始时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] startTime;

    @Schema(description = "结束时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] endTime;

    @Schema(description = "执行时长(秒)")
    private Double durationSeconds;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "保存记录数")
    private Integer recordsSaved;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "用户ID", example = "28254")
    private Long userId;

    @Schema(description = "邮编数量")
    private Integer zipcodeNum;

    @Schema(description = "关键词数量")
    private Integer keywordNum;

    @Schema(description = "爬取页面数量")
    private Integer pageNum;

}