package cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - Amazon任务执行日志新增/修改 Request VO")
@Data
public class TaskExecLogSaveReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2822")
    private Long id;

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11863")
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @Schema(description = "执行状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "执行状态不能为空")
    private String status;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "执行时长(秒)")
    private Double durationSeconds;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "保存记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "保存记录数不能为空")
    private Integer recordsSaved;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28254")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "邮编数量")
    private Integer zipcodeNum;

    @Schema(description = "关键词数量")
    private Integer keywordNum;

    @Schema(description = "爬取页面数量")
    private Integer pageNum;

}