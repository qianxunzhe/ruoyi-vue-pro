package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 领星API - 查询亚马逊库存库龄请求参数DTO
 * 对应接口: /erp/sc/routing/fba/fbaStock/getFbaAgeList
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FbaAgeListQueryDTO {
    
    /**
     * 店铺id, 多个使用英文逗号分隔
     * 对应查询亚马逊店铺列表接口对应字段【sid】
     * 必填 - 类型是string，示例值: "109" 或 "109,110,111"
     */
    @JsonProperty("sid")
    private String sid;
    
    /**
     * 分页偏移量
     * 非必填，默认0
     */
    @JsonProperty("offset")
    private Integer offset;
    
    /**
     * 分页长度
     * 非必填，默认20
     */
    @JsonProperty("length")
    private Integer length;
}