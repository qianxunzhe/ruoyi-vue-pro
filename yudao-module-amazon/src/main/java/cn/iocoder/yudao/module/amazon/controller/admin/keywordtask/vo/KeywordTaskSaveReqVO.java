package cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 关键词排名任务新增/修改 Request VO")
@Data
public class KeywordTaskSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30685")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24987")
    private Long userId;

    @Schema(description = "任务名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "任务名称不能为空")
    private String taskName;

    @Schema(description = "关键词列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "关键词列表不能为空")
    private List<String> keywords;

    @Schema(description = "城市列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "城市列表不能为空")
    private List<String> cities;

    @Schema(description = "ASIN列表")
    private List<String> asins;

    @Schema(description = "Cron表达式")
    private String cronTab;

    @Schema(description = "启用状态")
    private Short enabled;

    @Schema(description = "任务类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "任务类型不能为空")
    private Short taskType;

    @Schema(description = "任务共享状态")
    private Integer shared;

    @Schema(description = "任务描述", example = "你说的对")
    private String description;

    @Schema(description = "开始时间")
    private String startTime;

    @Schema(description = "结束时间")
    private String endTime;

}