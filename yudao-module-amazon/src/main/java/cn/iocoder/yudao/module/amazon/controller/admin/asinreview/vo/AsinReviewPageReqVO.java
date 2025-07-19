package cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - Amazon ASIN商品评论数据分页 Request VO")
@Data
public class AsinReviewPageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "4635")
    private Long userId;

    @Schema(description = "Amazon标准识别码")
    private String asin;

    @Schema(description = "父级ASIN（用于变体商品）")
    private String parentAsin;

    @Schema(description = "数据采集任务ID", example = "1885")
    private Integer taskId;

    @Schema(description = "评论总数")
    private Integer reviewNum;

    @Schema(description = "平均评分（1-5分）")
    private BigDecimal score;

    @Schema(description = "评级总数")
    private Integer ratings;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "备注", example = "你猜")
    private String remark;

}