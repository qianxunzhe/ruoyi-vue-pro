package cn.iocoder.yudao.module.amazon.controller.admin.reviewsnapshots.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 亚马逊评论快照批量同步 Request VO")
@Data
public class ReviewSnapshotsBatchSyncReqVO {

    @Schema(description = "评论数据列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "评论数据列表不能为空")
    @Valid
    private List<ReviewSnapshotItem> reviews;

    @Schema(description = "评论快照项")
    @Data
    public static class ReviewSnapshotItem {
        
        @Schema(description = "亚马逊商品编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "B08N5WRWNW")
        @NotEmpty(message = "亚马逊商品编号不能为空")
        private String asin;
        
        @Schema(description = "产品编号", example = "SKU123")
        private String spu;
        
        @Schema(description = "平均评分(1.0-5.0)", example = "4.5")
        private BigDecimal rating;
        
        @Schema(description = "评分总数", example = "1250")
        private Integer ratingsCount;
        
        @Schema(description = "评论总数", example = "890")
        private Integer reviewsCount;
        
        @Schema(description = "星级分布百分比", example = "{\"5star\": 70, \"4star\": 15, \"3star\": 8, \"2star\": 4, \"1star\": 3}")
        private Map<String, Object> starDistribution;
        
        @Schema(description = "各星级评论数量", example = "{\"5star\": 623, \"4star\": 134, \"3star\": 71, \"2star\": 36, \"1star\": 26}")
        private Map<String, Object> reviewStarCounts;
        
        @Schema(description = "各星级评论百分比", example = "{\"5star\": 70.0, \"4star\": 15.1, \"3star\": 8.0, \"2star\": 4.0, \"1star\": 2.9}")
        private Map<String, Object> reviewStarPercentages;
        
        @Schema(description = "数据抓取时间（UTC时间）", example = "2024-01-15T10:30:00Z")
        @NotNull(message = "抓取时间不能为空")
        private LocalDateTime scrapedAt;
    }
}