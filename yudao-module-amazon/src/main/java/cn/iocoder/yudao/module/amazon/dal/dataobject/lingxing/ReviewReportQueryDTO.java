package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 评论统计查询DTO - 对应领星评价统计API
 * 
 * @author 芋道源码
 */
@Data
public class ReviewReportQueryDTO {

    /**
     * 国家id (必填)
     */
    @JsonProperty("mid")
    private Integer mid = 1; // 默认美国

    /**
     * ASIN (必填)
     */
    @JsonProperty("asin")
    private String asin;

    /**
     * 开始时间【时间间隔不超过1年】 (必填)
     * 格式：2024-01-01 00:00:00
     */
    @JsonProperty("start_date")
    private String startDate;

    /**
     * 结束时间【时间间隔不超过1年】 (必填)
     * 格式：2024-08-05 00:00:00
     */
    @JsonProperty("end_date")
    private String endDate;
}