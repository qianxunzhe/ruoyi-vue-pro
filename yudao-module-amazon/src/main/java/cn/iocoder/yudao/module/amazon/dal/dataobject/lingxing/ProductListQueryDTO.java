package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 领星产品列表查询参数DTO
 *
 * @author 芋道源码
 */
@Data
public class ProductListQueryDTO {
    
    /**
     * 分页偏移量，默认0
     */
    private Integer offset = 0;
    
    /**
     * 分页长度，默认1000，上限1000
     */
    private Integer length = 1000;
    
    /**
     * 更新时间-开始时间【时间戳，单位：秒，左闭右开】
     */
    @JsonProperty("update_time_start")
    private Long updateTimeStart;
    
    /**
     * 更新时间-结束时间【时间戳，单位：秒，左闭右开】
     */
    @JsonProperty("update_time_end")
    private Long updateTimeEnd;
    
    /**
     * 创建时间-开始时间【时间戳，单位：秒，左闭右开】
     */
    @JsonProperty("create_time_start")
    private Long createTimeStart;
    
    /**
     * 创建时间-结束时间【时间戳，单位：秒，左闭右开】
     */
    @JsonProperty("create_time_end")
    private Long createTimeEnd;
    
    /**
     * 本地产品sku列表
     */
    @JsonProperty("sku_list")
    private List<String> skuList;
    
    /**
     * sku识别码列表
     */
    @JsonProperty("sku_identifier_list")
    private List<String> skuIdentifierList;
}