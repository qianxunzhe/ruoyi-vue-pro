package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 领星产品列表响应DTO
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductListResponseDTO extends LingXingApiResponseDTO<List<ProductListDTO>> {
    
}