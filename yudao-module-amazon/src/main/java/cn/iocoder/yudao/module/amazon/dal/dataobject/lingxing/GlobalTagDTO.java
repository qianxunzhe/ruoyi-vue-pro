package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 全局标签DTO
 *
 * @author 芋道源码
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalTagDTO {

    /**
     * 全局标签ID
     */
    @JsonProperty("globalTagId")
    private String globalTagId;

    /**
     * 标签名称
     */
    @JsonProperty("tagName")
    private String tagName;

    /**
     * 颜色
     */
    @JsonProperty("color")
    private String color;

    /**
     * 标签类型
     */
    @JsonProperty("tagType")
    private String tagType;
} 