package cn.iocoder.yudao.module.amazon.controller.admin.shops.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 亚马逊店铺信息新增/修改 Request VO")
@Data
public class ShopsSaveReqVO {

    @Schema(description = "店铺ID，主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11725")
    private Integer sid;

    @Schema(description = "商户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16218")
    @NotNull(message = "商户ID不能为空")
    private Integer mid;

    @Schema(description = "店铺名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "店铺名称不能为空")
    private String name;

    @Schema(description = "卖家ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3063")
    @NotEmpty(message = "卖家ID不能为空")
    private String sellerId;

    @Schema(description = "账户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "账户名称不能为空")
    private String accountName;

    @Schema(description = "卖家账户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4391")
    @NotNull(message = "卖家账户ID不能为空")
    private Integer sellerAccountId;

    @Schema(description = "区域", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "区域不能为空")
    private String region;

    @Schema(description = "国家", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "国家不能为空")
    private String country;

    @Schema(description = "是否有广告设置 (0:无, 1:有)")
    private Integer hasAdsSetting;

    @Schema(description = "市场ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30840")
    @NotEmpty(message = "市场ID不能为空")
    private String marketplaceId;

    @Schema(description = "状态 (0:禁用, 1:启用)", example = "2")
    private Integer status;

    @Schema(description = "用户ID（已废弃，使用userIds）", example = "20484")
    private Long userId;

    @Schema(description = "负责人用户ID列表", example = "[1, 2, 3]")
    private List<Long> userIds;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "是否同步产品表现 (0:不同步, 1:同步)", example = "1")
    private Integer syncFlag;

}