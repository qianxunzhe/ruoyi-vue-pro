package cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - Amazon ASIN商品评论数据新增/修改 Request VO")
@Data
public class AsinReviewSaveReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "25404")
    private Integer id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4635")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "Amazon标准识别码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "Amazon标准识别码不能为空")
    private String asin;

    @Schema(description = "父级ASIN（用于变体商品）")
    private String parentAsin;

    @Schema(description = "数据采集任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1885")
    @NotNull(message = "数据采集任务ID不能为空")
    private Integer taskId;

    @Schema(description = "评论总数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评论总数不能为空")
    private Integer reviewNum;

    @Schema(description = "平均评分（1-5分）")
    private BigDecimal score;

    @Schema(description = "评级总数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "评级总数不能为空")
    private Integer ratings;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}