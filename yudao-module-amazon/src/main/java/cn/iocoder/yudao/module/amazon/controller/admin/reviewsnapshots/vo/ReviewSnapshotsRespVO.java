package cn.iocoder.yudao.module.amazon.controller.admin.reviewsnapshots.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 亚马逊评论快照 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ReviewSnapshotsRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "20898")
    @ExcelProperty("主键")
    private Integer id;

    @Schema(description = "亚马逊商品编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("亚马逊商品编号")
    private String asin;

    @Schema(description = "快照日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("快照日期")
    private LocalDate snapshotDate;

    @Schema(description = "产品编号")
    @ExcelProperty("产品编号")
    private String spu;

    @Schema(description = "平均评分(1.0-5.0)")
    @ExcelProperty("平均评分(1.0-5.0)")
    private BigDecimal rating;

    @Schema(description = "评分总数", example = "21212")
    @ExcelProperty("评分总数")
    private Integer ratingsCount;

    @Schema(description = "评论总数", example = "2409")
    @ExcelProperty("评论总数")
    private Integer reviewsCount;

    @Schema(description = "星级分布百分比")
    @ExcelProperty("星级分布百分比")
    private Object starDistribution;

    @Schema(description = "各星级评论数量")
    @ExcelProperty("各星级评论数量")
    private Object reviewStarCounts;

    @Schema(description = "各星级评论百分比")
    @ExcelProperty("各星级评论百分比")
    private Object reviewStarPercentages;

    @Schema(description = "用户ID", example = "8892")
    @ExcelProperty("用户ID")
    private Integer userId;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}