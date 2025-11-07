package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * 领星利润报表响应DTO
 *
 * @author 芋道源码
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfitReportResponseDTO {
    
    /**
     * 总记录数
     */
    private Integer total;
    
    /**
     * 数据列表
     */
    private List<ProfitReportDTO> records;
}