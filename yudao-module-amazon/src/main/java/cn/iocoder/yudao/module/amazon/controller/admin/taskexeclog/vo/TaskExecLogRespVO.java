package cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - Amazon任务执行日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TaskExecLogRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2822")
    @ExcelProperty("主键ID")
    private Long id;

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11863")
    @ExcelProperty("任务ID")
    private Long taskId;

    @Schema(description = "执行状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("执行状态")
    private String status;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    @ExcelProperty("结束时间")
    private LocalDateTime endTime;

    @Schema(description = "执行时长(秒)")
    @ExcelProperty("执行时长(秒)")
    private Double durationSeconds;

    @Schema(description = "错误信息")
    @ExcelProperty("错误信息")
    private String errorMessage;

    @Schema(description = "保存记录数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("保存记录数")
    private Integer recordsSaved;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28254")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "邮编数量")
    @ExcelProperty("邮编数量")
    private Integer zipcodeNum;

    @Schema(description = "关键词数量")
    @ExcelProperty("关键词数量")
    private Integer keywordNum;

    @Schema(description = "爬取页面数量")
    @ExcelProperty("爬取页面数量")
    private Integer pageNum;

}