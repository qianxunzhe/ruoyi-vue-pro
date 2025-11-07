package cn.iocoder.yudao.module.amazon.controller.admin.reviewsnapshots.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 亚马逊评论快照新增/修改 Request VO")
@Data
public class ReviewSnapshotsSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "20898")
    private Integer id;

    @Schema(description = "亚马逊商品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "亚马逊商品编号不能为空")
    private String asin;

    @Schema(description = "快照日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "快照日期不能为空")
    private LocalDate snapshotDate;

    @Schema(description = "产品编号")
    private String spu;

    @Schema(description = "平均评分(1.0-5.0)")
    private BigDecimal rating;

    @Schema(description = "评分总数", example = "21212")
    private Integer ratingsCount;

    @Schema(description = "评论总数", example = "2409")
    private Integer reviewsCount;

    @Schema(description = "星级分布百分比")
    private Map<String, Object> starDistribution;

    @Schema(description = "各星级评论数量")
    private Map<String, Object> reviewStarCounts;

    @Schema(description = "各星级评论百分比")
    private Map<String, Object> reviewStarPercentages;

    @Schema(description = "用户ID", example = "8892")
    private Integer userId;

}