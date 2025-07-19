package cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - Amazon ASIN商品评论数据 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AsinReviewRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "25404")
    @ExcelProperty("主键ID")
    private Integer id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4635")
    @ExcelProperty("用户ID")
    private Long userId;

    @Schema(description = "Amazon标准识别码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("Amazon标准识别码")
    private String asin;

    @Schema(description = "父级ASIN（用于变体商品）")
    @ExcelProperty("父级ASIN（用于变体商品）")
    private String parentAsin;

    @Schema(description = "数据采集任务ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1885")
    @ExcelProperty("数据采集任务ID")
    private Integer taskId;

    @Schema(description = "评论总数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("评论总数")
    private Integer reviewNum;

    @Schema(description = "平均评分（1-5分）")
    @ExcelProperty("平均评分（1-5分）")
    private BigDecimal score;

    @Schema(description = "评级总数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("评级总数")
    private Integer ratings;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "备注", example = "你猜")
    @ExcelProperty("备注")
    private String remark;

}