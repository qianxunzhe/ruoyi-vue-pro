package cn.iocoder.yudao.module.amazon.controller.admin.reviewsnapshots.vo;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 亚马逊评论快照分页 Request VO")
@Data
public class ReviewSnapshotsPageReqVO extends PageParam {

    @Schema(description = "亚马逊商品编号")
    private String asin;

    @Schema(description = "快照日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDate[] snapshotDate;

    @Schema(description = "产品编号")
    private String spu;

    @Schema(description = "平均评分(1.0-5.0)")
    private BigDecimal rating;

    @Schema(description = "评分总数", example = "21212")
    private Integer ratingsCount;

    @Schema(description = "评论总数", example = "2409")
    private Integer reviewsCount;

    @Schema(description = "星级分布百分比")
    private Object starDistribution;

    @Schema(description = "各星级评论数量")
    private Object reviewStarCounts;

    @Schema(description = "各星级评论百分比")
    private Object reviewStarPercentages;

    @Schema(description = "用户ID", example = "8892")
    private Integer userId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}