package cn.iocoder.yudao.module.amazon.controller.admin.keywordasinranking.vo;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 关键词排名任务结果分页 Request VO")
@Data
public class KeywordAsinRankingPageReqVO extends PageParam {

    @Schema(description = "任务ID", example = "14731")
    private Long taskId;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "关键词的哈希值")
    private String keywordHash;

    @Schema(description = "ASIN")
    private String asin;

    @Schema(description = "搜索城市/地区")
    private String city;

    @Schema(description = "排名类型", example = "1")
    private Short rankType;

    @Schema(description = "整体排名位置")
    private Integer rankPosition;

    @Schema(description = "搜索结果页码")
    private Integer pageNumber;

    @Schema(description = "页内位置")
    private Integer positionInPage;

    @Schema(description = "广告类型", example = "2")
    private Short adsType;

    @Schema(description = "采集时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] crawlTime;

    @Schema(description = "爬取日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDate[] crawlDate;

    @Schema(description = "备注信息", example = "你猜")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "用户ID", example = "32458")
    private Long userId;


}