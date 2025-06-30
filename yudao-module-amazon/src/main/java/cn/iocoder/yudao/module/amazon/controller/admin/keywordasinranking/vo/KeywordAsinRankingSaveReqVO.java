package cn.iocoder.yudao.module.amazon.controller.admin.keywordasinranking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 关键词排名任务结果新增/修改 Request VO")
@Data
public class KeywordAsinRankingSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6674")
    private Long id;

    @Schema(description = "任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "14731")
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @Schema(description = "关键词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "关键词不能为空")
    private String keyword;

    @Schema(description = "关键词的哈希值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "关键词的哈希值不能为空")
    private String keywordHash;

    @Schema(description = "ASIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "ASIN不能为空")
    private String asin;

    @Schema(description = "搜索城市/地区", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "搜索城市/地区不能为空")
    private String city;

    @Schema(description = "排名类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排名类型不能为空")
    private Short rankType;

    @Schema(description = "整体排名位置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "整体排名位置不能为空")
    private Integer rankPosition;

    @Schema(description = "搜索结果页码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "搜索结果页码不能为空")
    private Integer pageNumber;

    @Schema(description = "页内位置", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "页内位置不能为空")
    private Integer positionInPage;

    @Schema(description = "广告类型", example = "2")
    private Short adsType;

    @Schema(description = "采集时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "采集时间不能为空")
    private LocalDateTime crawlTime;

    @Schema(description = "爬取日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "爬取日期不能为空")
    private LocalDate crawlDate;

    @Schema(description = "备注信息", example = "你猜")
    private String remark;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32458")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

}