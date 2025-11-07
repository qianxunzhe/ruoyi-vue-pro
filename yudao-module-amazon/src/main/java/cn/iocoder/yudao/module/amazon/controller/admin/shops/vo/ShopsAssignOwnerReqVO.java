package cn.iocoder.yudao.module.amazon.controller.admin.shops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "管理后台 - 店铺分配负责人 Request VO")
@Data
public class ShopsAssignOwnerReqVO {

    @Schema(description = "店铺ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "店铺ID不能为空")
    private Integer sid;

    @Schema(description = "负责人用户ID（已废弃，请使用userIds）", example = "1")
    @Deprecated
    private Long userId;

    @Schema(description = "负责人用户ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    @NotEmpty(message = "负责人用户ID列表不能为空")
    private List<Long> userIds;

}