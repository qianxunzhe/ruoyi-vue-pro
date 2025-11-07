package cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

/**
 * 利润报表导出请求 VO
 * 支持文件上传和合并历史数据
 */
@Schema(description = "管理后台 - 利润报表导出请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class ProfitReportExportReqVO extends ProfitReportQueryVO {
    
    @Schema(description = "历史Excel文件", example = "")
    private MultipartFile historyFile;
    
    @Schema(description = "是否合并历史数据", example = "true")
    private Boolean mergeHistory = false;
    
    @Schema(description = "导出格式", example = "STANDARD")
    private String exportFormat = "STANDARD";
}