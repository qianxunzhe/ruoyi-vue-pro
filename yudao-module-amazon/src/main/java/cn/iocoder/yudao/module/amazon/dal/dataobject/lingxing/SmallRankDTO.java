package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 小类排名信息DTO
 *
 * @author 芋道源码
 */
@Data
public class SmallRankDTO {

    /**
     * 小类名称
     */
    @JsonProperty("category")
    private String category;

    /**
     * 小类排名
     */
    @JsonProperty("rank")
    private String rank;
} 