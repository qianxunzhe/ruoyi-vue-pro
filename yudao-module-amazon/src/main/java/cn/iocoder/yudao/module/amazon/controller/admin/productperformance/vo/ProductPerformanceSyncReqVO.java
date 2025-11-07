package cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;


import java.util.List;

@Schema(description = "管理后台 - 亚马逊产品表现同步 Request VO")
@Data
@ToString
public class ProductPerformanceSyncReqVO {

    @Schema(description = "ASIN列表，为空时查询店铺所有产品", example = "[\"B085M7NH7K\", \"B085M7NH7L\"]")
    private List<String> asins;

    @Schema(description = "店铺ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[109, 110]")
    @NotEmpty(message = "店铺ID列表不能为空")
    private List<Long> sids;

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-08-01")
    @NotNull(message = "开始日期不能为空")
    private String startDate;

    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-08-07")
    @NotNull(message = "结束日期不能为空")
    private String endDate;

    @Schema(description = "汇总维度", example = "asin")
    private String summaryField = "asin";

    @Schema(description = "站点ID", example = "1")
    private Integer mid;

    @Schema(description = "货币类型", example = "CNY")
    private String currencyCode;

    @Schema(description = "是否仅查询活跃商品", example = "true")
    private Boolean isRecentlyEnum = true;
}