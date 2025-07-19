package cn.iocoder.yudao.module.amazon.controller.admin.listingprice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "管理后台 - 价格分析响应 Response VO")
public class PriceAnalysisRespVO {

    @Schema(description = "分析数据，ASIN -> 日期 -> 价格")
    private Map<String, Map<String, BigDecimal>> analysisData;

    @Schema(description = "ASIN列表")
    private List<String> asinList;

    @Schema(description = "日期列表")
    private List<String> dateList;

    @Schema(description = "平均价格")
    private BigDecimal averagePrice;

}