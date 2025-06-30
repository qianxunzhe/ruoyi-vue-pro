package cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 关键词排名任务分页 Request VO")
@Data
public class KeywordTaskPageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "24987")
    private Long userId;

    @Schema(description = "任务名称", example = "赵六")
    private String taskName;

    @Schema(description = "关键词列表")
    private Object keywords;

    @Schema(description = "城市列表")
    private Object cities;

    @Schema(description = "ASIN列表")
    private Object asins;

    @Schema(description = "Cron表达式")
    private String cronTab;

    @Schema(description = "启用状态")
    private Short enabled;

    @Schema(description = "任务类型", example = "2")
    private Short taskType;

    @Schema(description = "任务共享状态")
    private Short shared;

    @Schema(description = "任务描述", example = "你说的对")
    private String description;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}