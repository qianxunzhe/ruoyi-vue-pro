package cn.iocoder.yudao.module.amazon.controller.admin.shops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 亚马逊店铺更新同步标记 Request VO")
@Data
public class ShopsUpdateSyncFlagReqVO {

    @Schema(description = "店铺ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11725")
    @NotNull(message = "店铺ID不能为空")
    private Integer sid;

    @Schema(description = "是否同步产品表现 (0:不同步, 1:同步)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "同步标记不能为空")
    @Min(value = 0, message = "同步标记必须为0或1")
    @Max(value = 1, message = "同步标记必须为0或1")
    private Integer syncFlag;

}