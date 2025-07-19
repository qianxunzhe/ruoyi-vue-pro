package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 负责人信息DTO
 *
 * @author 芋道源码
 */
@Data
public class PrincipalInfoDTO {

    /**
     * 负责人用户id
     */
    @JsonProperty("principal_uid")
    private Long principalUid;

    /**
     * 负责人姓名
     */
    @JsonProperty("principal_name")
    private String principalName;
} 