package cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 关键词排名数据分析使用示例
 * 
 * @author ultrathink
 */
@Schema(description = "关键词排名数据分析使用示例")
@Data
public class KeywordRankingAnalysisExampleVO {

    @Schema(description = "单关键词分析示例")
    public static final String SINGLE_KEYWORD_EXAMPLE = """
        {
          "city": "New York",
          "rankType": 1,
          "keywords": ["iPhone case"],
          "startDate": "2024-07-15",
          "endDate": "2024-07-21",
          "maxPosition": 50,
          "aggregationType": "latest",
          "displayMode": "single"
        }
        """;

    @Schema(description = "多关键词对比分析示例")
    public static final String MULTI_KEYWORD_COMPARISON_EXAMPLE = """
        {
          "city": "New York",
          "rankType": 1,
          "keywords": ["iPhone case", "phone cover", "mobile case"],
          "startDate": "2024-07-15",
          "endDate": "2024-07-21",
          "maxPosition": 50,
          "aggregationType": "latest",
          "displayMode": "comparison",
          "comparisonType": "position"
        }
        """;

    @Schema(description = "ASIN覆盖度分析示例")
    public static final String ASIN_COVERAGE_ANALYSIS_EXAMPLE = """
        {
          "city": "New York",
          "rankType": 1,
          "keywords": ["iPhone case", "phone cover"],
          "startDate": "2024-07-15",
          "endDate": "2024-07-21",
          "maxPosition": 30,
          "aggregationType": "latest",
          "displayMode": "comparison",
          "comparisonType": "coverage"
        }
        """;

    @Schema(description = "响应数据结构示例")
    public static final String RESPONSE_EXAMPLE = """
        {
          "code": 200,
          "message": "success",
          "data": {
            "summary": {
              "dateRange": ["2024-07-15", "2024-07-16", "2024-07-17"],
              "totalDays": 3,
              "totalPositions": 50,
              "keywords": ["iPhone case"],
              "city": "New York",
              "rankType": 1
            },
            "matrix": {
              "headers": ["位置", "7月15日", "7月16日", "7月17日"],
              "keywordMatrices": {
                "iPhone case": {
                  "keyword": "iPhone case",
                  "rows": [
                    {
                      "position": "P1-01",
                      "positionCode": "1-1",
                      "pageNumber": 1,
                      "positionInPage": 1,
                      "data": ["ASIN1", "ASIN1", "ASIN2"]
                    },
                    {
                      "position": "P1-02",
                      "positionCode": "1-2",
                      "pageNumber": 1,
                      "positionInPage": 2,
                      "data": ["ASIN2", "ASIN3", "ASIN2"]
                    }
                  ]
                }
              }
            },
            "statistics": {
              "asinFrequency": {
                "ASIN1": 25,
                "ASIN2": 18,
                "ASIN3": 12
              },
              "positionStability": {
                "P1-01": 0.85,
                "P1-02": 0.71
              },
              "keywordCoverage": {
                "iPhone case": {
                  "keyword": "iPhone case",
                  "coveredPositions": 25,
                  "totalPositions": 50,
                  "coverageRate": 0.5
                }
              }
            }
          }
        }
        """;
}