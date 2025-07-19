package cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 关键词排名数据分析响应 VO")
@Data
public class KeywordRankingAnalysisRespVO {

    @Schema(description = "汇总信息")
    private SummaryInfo summary;

    @Schema(description = "矩阵数据")
    private MatrixData matrix;

    @Schema(description = "统计信息")
    private StatisticsData statistics;

    @Schema(description = "对比分析")
    private ComparisonAnalysis comparison;

    @Data
    @Schema(description = "汇总信息")
    public static class SummaryInfo {
        @Schema(description = "日期范围")
        private List<LocalDate> dateRange;

        @Schema(description = "总天数")
        private Integer totalDays;

        @Schema(description = "总位置数")
        private Integer totalPositions;

        @Schema(description = "关键词列表")
        private List<String> keywords;

        @Schema(description = "城市")
        private String city;

        @Schema(description = "排名类型")
        private Short rankType;
    }

    @Data
    @Schema(description = "矩阵数据")
    public static class MatrixData {
        @Schema(description = "表头")
        private List<String> headers;

        @Schema(description = "单关键词矩阵")
        private Map<String, KeywordMatrix> keywordMatrices;
    }

    @Data
    @Schema(description = "关键词矩阵")
    public static class KeywordMatrix {
        @Schema(description = "关键词")
        private String keyword;

        @Schema(description = "行数据")
        private List<MatrixRow> rows;
    }

    @Data
    @Schema(description = "矩阵行")
    public static class MatrixRow {
        @Schema(description = "位置编码", example = "P1-01")
        private String position;

        @Schema(description = "位置代码", example = "1-1")
        private String positionCode;

        @Schema(description = "页码")
        private Integer pageNumber;

        @Schema(description = "页内位置")
        private Integer positionInPage;

        @Schema(description = "数据", example = "[\"ASIN1\", \"ASIN2\", \"ASIN1\"]")
        private List<String> data;
    }

    @Data
    @Schema(description = "统计数据")
    public static class StatisticsData {
        @Schema(description = "ASIN出现频次")
        private Map<String, Integer> asinFrequency;

        @Schema(description = "位置稳定性")
        private Map<String, Double> positionStability;

        @Schema(description = "关键词覆盖度")
        private Map<String, KeywordCoverage> keywordCoverage;
    }

    @Data
    @Schema(description = "关键词覆盖度")
    public static class KeywordCoverage {
        @Schema(description = "关键词")
        private String keyword;

        @Schema(description = "覆盖位置数")
        private Integer coveredPositions;

        @Schema(description = "总位置数")
        private Integer totalPositions;

        @Schema(description = "覆盖率")
        private Double coverageRate;
    }

    @Data
    @Schema(description = "对比分析")
    public static class ComparisonAnalysis {
        @Schema(description = "关键词竞争分析")
        private List<PositionCompetition> positionCompetitions;

        @Schema(description = "ASIN覆盖度分析")
        private Map<String, AsinCoverage> asinCoverages;
    }

    @Data
    @Schema(description = "位置竞争")
    public static class PositionCompetition {
        @Schema(description = "位置")
        private String position;

        @Schema(description = "日期")
        private List<LocalDate> dates;

        @Schema(description = "竞争详情")
        private List<CompetitionDetail> competitions;
    }

    @Data
    @Schema(description = "竞争详情")
    public static class CompetitionDetail {
        @Schema(description = "日期")
        private LocalDate date;

        @Schema(description = "相同ASIN")
        private List<String> sameAsins;

        @Schema(description = "不同ASIN")
        private Map<String, String> differentAsins;
    }

    @Data
    @Schema(description = "ASIN覆盖度")
    public static class AsinCoverage {
        @Schema(description = "ASIN")
        private String asin;

        @Schema(description = "覆盖关键词")
        private List<String> keywords;

        @Schema(description = "覆盖位置")
        private List<String> positions;

        @Schema(description = "覆盖率")
        private Double coverageRate;

        @Schema(description = "平均排名")
        private Double averagePosition;

        @Schema(description = "稳定性评分")
        private Double stabilityScore;
    }
}