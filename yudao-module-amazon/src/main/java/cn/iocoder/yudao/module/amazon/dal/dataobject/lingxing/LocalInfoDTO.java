package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 本地信息DTO
 * 
 * @author 芋道源码
 */
@Data
public class LocalInfoDTO {

    /**
     * 本地SKU
     */
    @JsonProperty("local_sku")
    private String localSku;

    /**
     * 本地品名
     */
    @JsonProperty("local_name")
    private String localName;

    /**
     * 分类名
     */
    @JsonProperty("category_name")
    private String categoryName;
}