package cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Amazon ASIN评论统计数据项")
@Data
public class AsinReviewStatItemVO {

    @Schema(description = "ASIN编码", example = "B08XXXX123")
    private String asin;

    @Schema(description = "评论日期", example = "2025-01-01")
    private String reviewDate;

    @Schema(description = "评论数量", example = "5")
    private Integer reviewCount;

    @Schema(description = "任务ID", example = "132")
    private Long taskId;
}