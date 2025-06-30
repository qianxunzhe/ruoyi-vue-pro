package cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo;

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
public class KeywordRankingPageReqVO extends PageParam {

    @Schema(description = "任务ID", example = "3512")
    private Long taskId;

    @Schema(description = "关键词")
    private String keyword;

    @Schema(description = "关键词的哈希值")
    private String keywordHash;

    @Schema(description = "ASIN")
    private String asin;

    @Schema(description = "搜索城市/地区")
    private String city;

    @Schema(description = "排名类型", example = "2")
    private Short rankType;

    @Schema(description = "广告类型", example = "2")
    private Short adsType;

    @Schema(description = "采集时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] crawlTime;

    @Schema(description = "爬取日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDate crawlDate;

}