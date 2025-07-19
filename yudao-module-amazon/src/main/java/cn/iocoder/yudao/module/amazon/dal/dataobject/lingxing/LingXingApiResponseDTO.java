package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * 领星API通用响应DTO
 *
 * @author 芋道源码
 */
@Schema(description = "领星API通用响应")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LingXingApiResponseDTO<T> {

    @Schema(description = "状态码，0 成功", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    private Integer code;

    @Schema(description = "消息提示", requiredMode = Schema.RequiredMode.REQUIRED, example = "success")
    @JsonProperty("msg")
    @JsonAlias({"message"}) // 同时支持 "message" 字段名
    private String msg;

    @Schema(description = "错误信息", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("error_details")
    private List<Object> errorDetails;

    @Schema(description = "请求ID", example = "B34C8F27-F3CA-EE25-836C-32FABBD8B8CD")
    @JsonProperty("request_id")
    private String requestId;

    @Schema(description = "响应时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2021-11-10 17:28:33")
    @JsonProperty("response_time")
    private String responseTime;

    @Schema(description = "响应数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private T data;

    @Schema(description = "总记录数", example = "100")
    @JsonProperty("total")
    private Integer total;

    /**
     * 判断API调用是否成功
     */
    public boolean isSuccess() {
        return code != null && code == 0;
    }
} 