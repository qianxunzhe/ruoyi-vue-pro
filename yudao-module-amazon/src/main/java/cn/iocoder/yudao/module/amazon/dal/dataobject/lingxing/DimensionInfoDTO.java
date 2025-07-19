package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 尺寸信息DTO
 *
 * @author 芋道源码
 */
@Data
public class DimensionInfoDTO {

    /**
     * 项目高度
     */
    @JsonProperty("item_height")
    private String itemHeight;

    /**
     * 项目高度单位
     */
    @JsonProperty("item_height_units_type")
    private String itemHeightUnitsType;

    /**
     * 项目长度
     */
    @JsonProperty("item_length")
    private String itemLength;

    /**
     * 项目长度单位
     */
    @JsonProperty("item_length_units_type")
    private String itemLengthUnitsType;

    /**
     * 项目宽度
     */
    @JsonProperty("item_width")
    private String itemWidth;

    /**
     * 项目宽度单位
     */
    @JsonProperty("item_width_units_type")
    private String itemWidthUnitsType;

    /**
     * 项目重量
     */
    @JsonProperty("item_weight")
    private String itemWeight;

    /**
     * 项目重量单位
     */
    @JsonProperty("item_weight_units_type")
    private String itemWeightUnitsType;

    /**
     * 包装高度
     */
    @JsonProperty("package_height")
    private String packageHeight;

    /**
     * 包装高度单位
     */
    @JsonProperty("package_height_units_type")
    private String packageHeightUnitsType;

    /**
     * 包装长度
     */
    @JsonProperty("package_length")
    private String packageLength;

    /**
     * 包装长度单位
     */
    @JsonProperty("package_length_units_type")
    private String packageLengthUnitsType;

    /**
     * 包装宽度
     */
    @JsonProperty("package_width")
    private String packageWidth;

    /**
     * 包装宽度单位
     */
    @JsonProperty("package_width_units_type")
    private String packageWidthUnitsType;

    /**
     * 包装重量
     */
    @JsonProperty("package_weight")
    private String packageWeight;

    /**
     * 包装重量单位
     */
    @JsonProperty("package_weight_units_type")
    private String packageWeightUnitsType;
} 