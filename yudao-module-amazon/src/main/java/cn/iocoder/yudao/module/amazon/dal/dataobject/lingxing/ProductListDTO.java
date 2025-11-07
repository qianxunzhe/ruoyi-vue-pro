package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 领星产品列表数据DTO（仅包含SKU、SPU、产品名称）
 *
 * @author 芋道源码
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductListDTO {
    
    /**
     * 本地产品SKU
     */
    private String sku;
    
    /**
     * SPU
     */
    private String spu;
    
    /**
     * 品名
     */
    @JsonProperty("product_name")
    private String productName;
}