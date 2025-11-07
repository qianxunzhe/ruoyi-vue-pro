package cn.iocoder.yudao.module.amazon.controller.admin.shops.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 亚马逊店铺信息分页 Request VO")
@Data
public class ShopsPageReqVO extends PageParam {

    @Schema(description = "商户ID", example = "16218")
    private Integer mid;

    @Schema(description = "店铺名称", example = "芋艿")
    private String name;

    @Schema(description = "卖家ID", example = "3063")
    private String sellerId;

//    @Schema(description = "账户名称", example = "张三")
//    private String accountName;

    @Schema(description = "卖家账户ID", example = "4391")
    private Integer sellerAccountId;

    @Schema(description = "区域")
    private String region;

    @Schema(description = "国家")
    private String country;

    @Schema(description = "是否有广告设置 (0:无, 1:有)")
    private Integer hasAdsSetting;

    @Schema(description = "市场ID", example = "30840")
    private String marketplaceId;

    @Schema(description = "状态 (0:禁用, 1:启用)", example = "2")
    private Integer status;

    /**
     * 用户ID（内部使用，由Service层自动设置，前端无需传递）
     * 非管理员用户会自动过滤只查看自己负责的店铺
     */
    @Schema(description = "用户ID（内部字段，前端无需传递）", hidden = true)
    private Long userId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

}