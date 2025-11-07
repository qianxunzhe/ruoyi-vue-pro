package cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 关键词排名任务 Response VO")
@Data
@ExcelIgnoreUnannotated
public class KeywordTaskRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30685")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24987")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("任务名称")
    private String taskName;

    @Schema(description = "关键词列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("关键词列表")
    private Object keywords;

    @Schema(description = "城市列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("城市列表")
    private Object cities;

    @Schema(description = "ASIN列表")
    @ExcelProperty("ASIN列表")
    private Object asins;

    @Schema(description = "Cron表达式", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Cron表达式")
    private String cronTab;

    @Schema(description = "启用状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("启用状态")
    private Short enabled;

    @Schema(description = "任务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("任务类型")
    private Short taskType;

    @Schema(description = "任务共享状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("任务共享状态")
    private Short shared;

    @Schema(description = "任务描述", example = "你说的对")
    @ExcelProperty("任务描述")
    private String description;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;
    
    @Schema(description = "店铺ID")
    @ExcelProperty("店铺ID")
    private Long shopId;
    
    @Schema(description = "产品")
    @ExcelProperty("产品")
    private String product;

}