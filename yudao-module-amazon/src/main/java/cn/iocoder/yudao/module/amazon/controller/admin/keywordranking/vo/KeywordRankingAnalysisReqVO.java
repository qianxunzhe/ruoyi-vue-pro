package cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 关键词排名数据分析请求 VO")
@Data
public class KeywordRankingAnalysisReqVO {

    @Schema(description = "开始日期", example = "2024-07-15")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-07-21")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

    @Schema(description = "城市", requiredMode = Schema.RequiredMode.REQUIRED, example = "New York")
    @NotBlank(message = "城市不能为空")
    private String city;

    @Schema(description = "排名类型", example = "1")
    private Short rankType; // 1=广告位, 2=自然位, 不传=查询全部

    @Schema(description = "关键词列表", example = "[\"iPhone case\", \"phone cover\"]")
    @Size(max = 5, message = "最多支持5个关键词同时查询")
    private List<String> keywords;

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "123")
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @Schema(description = "最大位置数", example = "50")
    @Max(value = 100, message = "最大位置数不能超过100")
    private Integer maxPosition = 50;

    @Schema(description = "聚合方式", example = "latest")
    private String aggregationType = "latest"; // latest, first, all

    @Schema(description = "显示模式", example = "single")
    private String displayMode = "single"; // single, comparison

    @Schema(description = "对比类型", example = "position")
    private String comparisonType = "position"; // position, asin, coverage

}