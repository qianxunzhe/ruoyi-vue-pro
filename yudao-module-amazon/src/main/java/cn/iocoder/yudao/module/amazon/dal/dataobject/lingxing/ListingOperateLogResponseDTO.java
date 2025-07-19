package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Listing操作日志响应DTO
 *
 * @author 芋道源码
 */
@Data
public class ListingOperateLogResponseDTO {

    /**
     * 状态码，0 成功
     */
    @JsonProperty("code")
    private Integer code;

    /**
     * 消息提示
     */
    @JsonProperty("message")
    private String message;

    /**
     * 错误信息
     */
    @JsonProperty("error_details")
    private List<String> errorDetails;

    /**
     * 请求链路id
     */
    @JsonProperty("request_id")
    private String requestId;

    /**
     * 响应时间
     */
    @JsonProperty("response_time")
    private String responseTime;

    /**
     * 总数
     */
    @JsonProperty("total")
    private Integer total;

    /**
     * 响应数据
     */
    @JsonProperty("data")
    private List<ListingOperateLogDTO> data;

    /**
     * 判断请求是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 0;
    }
} 