package cn.iocoder.yudao.module.amazon.controller.admin.shops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 亚马逊店铺更新状态 Request VO")
@Data
public class ShopsUpdateStatusReqVO {

    @Schema(description = "店铺ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11725")
    @NotNull(message = "店铺ID不能为空")
    private Integer sid;

    @Schema(description = "状态 (0:禁用, 1:启用)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态必须为0或1")
    @Max(value = 1, message = "状态必须为0或1")
    private Integer status;

}