package cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - Amazon ASIN评论统计分析响应 VO")
@Data
public class AsinReviewAnalysisRespVO {

    @Schema(description = "分析数据 - 嵌套对象结构，格式：{ \"ASIN\": { \"日期\": 评论数 } }", example = "{\"B08XXXX123\": {\"2025-01-01\": 5, \"2025-01-02\": 8}}")
    private Map<String, Map<String, Integer>> analysisData;

    @Schema(description = "ASIN列表", example = "[\"B08XXXX123\", \"B09YYYY456\"]")
    private List<String> asinList;

    @Schema(description = "日期列表", example = "[\"2025-01-01\", \"2025-01-02\", \"2025-01-03\"]")
    private List<String> dateList;

    @Schema(description = "总评论数", example = "28")
    private Integer totalCount;

    @Schema(description = "任务ID", example = "132")
    private Long taskId;

    @Schema(description = "查询日期范围", example = "2025-01-01 至 2025-01-03")
    private String dateRange;
}