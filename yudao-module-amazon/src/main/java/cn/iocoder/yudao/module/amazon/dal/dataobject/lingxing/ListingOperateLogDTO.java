package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Listing操作日志DTO
 *
 * @author 芋道源码
 */
@Data
public class ListingOperateLogDTO {

    /**
     * 操作时间
     */
    @JsonProperty("operate_time")
    private String operateTime;

    /**
     * 操作人名称
     */
    @JsonProperty("operate_user")
    private String operateUser;

    /**
     * 操作类型
     */
    @JsonProperty("operate_type")
    private Integer operateType;

    /**
     * 操作类型说明
     */
    @JsonProperty("operate_type_text")
    private String operateTypeText;

    /**
     * 详情
     */
    @JsonProperty("operate_detail")
    private String operateDetail;

    /**
     * 店铺id
     */
    @JsonProperty("sid")
    private String sid;
} 