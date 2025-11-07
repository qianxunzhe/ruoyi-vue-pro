package cn.iocoder.yudao.module.amazon.dal.dataobject.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 飞书响应DTO
 *
 * @author Claude
 */
@Data
public class FeishuResponseDTO {
    
    /**
     * 状态码
     */
    @JsonProperty("StatusCode")
    private Integer statusCode;
    
    /**
     * 状态消息
     */
    @JsonProperty("StatusMessage")
    private String statusMessage;
    
    /**
     * 业务状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String msg;
    
    /**
     * 数据
     */
    private Object data;
    
    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return statusCode != null && statusCode == 0 && code != null && code == 0;
    }
}