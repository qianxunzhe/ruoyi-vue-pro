package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 产品表现API响应数据包装类
 */
@Data
public class ProductPerformanceResponseDTO {
    
    /**
     * 总数
     */
    @JsonProperty("total")
    private Integer total;
    
    /**
     * 数据列表
     */
    @JsonProperty("list")
    private List<ProductPerformanceDTO> list;
    
    /**
     * 环比开始日期
     */
    @JsonProperty("chain_start_date")
    private String chainStartDate;
    
    /**
     * 环比结束日期
     */
    @JsonProperty("chain_end_date")
    private String chainEndDate;
    
    /**
     * 可用库存计算公式
     */
    @JsonProperty("available_inventory_formula_zh")
    private String availableInventoryFormulaZh;
}