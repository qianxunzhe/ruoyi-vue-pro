package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Listing操作日志查询请求DTO
 *
 * @author 芋道源码
 */
@Data
public class ListingOperateLogQueryDTO {

    /**
     * 店铺id
     */
    @JsonProperty("sid")
    private String sid;

    /**
     * MSKU
     */
    @JsonProperty("msku")
    private String msku;

    /**
     * 分页偏移量，默认0
     */
    @JsonProperty("offset")
    private Integer offset = 0;

    /**
     * 分页长度，默认20
     */
    @JsonProperty("length")
    private Integer length = 20;

    /**
     * 操作人id列表
     */
    @JsonProperty("operate_uid")
    private List<Long> operateUid;

    /**
     * 操作类型列表
     * 1 调价
     * 2 调库存
     * 3 修改标题
     * 4 编辑商品
     * 5 B2B调价
     */
    @JsonProperty("operate_type")
    private List<Integer> operateType;

    /**
     * 开始时间【操作时间】，格式：Y-m-d H:i:s
     */
    @JsonProperty("operate_time_start")
    private String operateTimeStart;

    /**
     * 结束时间【操作时间】，格式：Y-m-d H:i:s
     */
    @JsonProperty("operate_time_end")
    private String operateTimeEnd;
} 