package cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Schema(description = "管理后台 - 亚马逊产品表现分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProductPerformancePageReqVO extends PageParam {

    @Schema(description = "ASIN", example = "B085M7NH7K")
    private String asin;

    @Schema(description = "父ASIN", example = "B085M7NH7K")
    private String parentAsin;

    @Schema(description = "MSKU", example = "DEE-216")
    private String msku;

    @Schema(description = "SKU", example = "Yajie003")
    private String sku;

    @Schema(description = "SPU", example = "Yajie003")
    private String spu;

    @Schema(description = "店铺ID", example = "109")
    private Long sid;

    @Schema(description = "店铺ID列表", example = "[109, 110]")
    private List<Long> sids;

    @Schema(description = "站点ID", example = "1")
    private Integer mid;

    @Schema(description = "汇总维度", example = "asin")
    private String summaryField;

    @Schema(description = "开始日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate startDate;

    @Schema(description = "结束日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate endDate;

    @Schema(description = "产品标题", example = "Car Floor Mats")
    private String itemName;
}