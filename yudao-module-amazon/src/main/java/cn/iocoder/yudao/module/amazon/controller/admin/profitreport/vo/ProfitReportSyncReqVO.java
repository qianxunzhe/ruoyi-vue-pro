package cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * 利润报表同步请求 VO
 */
@Schema(description = "管理后台 - 利润报表同步请求 VO")
@Data
public class ProfitReportSyncReqVO {
    
    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-01-01")
    @NotNull(message = "开始日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-01-31")
    @NotNull(message = "结束日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    @Schema(description = "店铺ID列表", example = "[101, 102]")
    private List<Long> sids;
    
    @Schema(description = "ASIN列表", example = "[\"B001234567\", \"B002345678\"]")
    private List<String> asins;
    
    @Schema(description = "交易状态", example = "Disbursed")
    private String orderStatus = "Disbursed";
    
    @Schema(description = "同步类型", example = "FULL")
    private String syncType = "FULL"; // FULL-全量, INCREMENT-增量
}