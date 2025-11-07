package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 领星API - 库存库龄响应数据DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FbaAgeListResponseDTO {
    
    /**
     * 总数
     */
    @JsonProperty("total")
    private Integer total;
    
    /**
     * 数据列表
     */
    @JsonProperty("list")
    private List<FbaAgeListDTO> list;
}