package cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

/**
 * 利润报表查询 VO
 */
@Schema(description = "管理后台 - 利润报表查询 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProfitReportQueryVO extends PageParam {
    
    @Schema(description = "开始日期", example = "2024-01-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @Schema(description = "结束日期", example = "2024-01-31")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @Schema(description = "ASIN列表", example = "[\"B001234567\"]")
    private List<String> asins;
    
    @Schema(description = "SPU列表", example = "[\"SPU001\"]")
    private List<String> spus;
    
    @Schema(description = "SKU列表", example = "[\"SKU001\"]")
    private List<String> skus;
    
    @Schema(description = "店铺ID列表", example = "[101, 102]")
    private List<Long> sids;
    
    @Schema(description = "聚合维度", example = "ASIN")
    private String groupBy; // ASIN, SPU, SKU, SID, DATE
    
    @Schema(description = "排序字段", example = "totalSales")
    private String sortField;
    
    @Schema(description = "排序方式", example = "DESC")
    private String sortOrder = "DESC"; // ASC, DESC
}