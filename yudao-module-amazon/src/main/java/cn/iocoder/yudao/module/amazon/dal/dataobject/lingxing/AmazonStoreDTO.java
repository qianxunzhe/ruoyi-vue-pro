package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 亚马逊店铺信息DTO
 *
 * @author 芋道源码
 */
@Schema(description = "亚马逊店铺信息")
@Data
public class AmazonStoreDTO {

    @Schema(description = "店铺id，领星ERP对企业已授权店铺的唯一标识", requiredMode = Schema.RequiredMode.REQUIRED, example = "1519")
    private Long sid;

    @Schema(description = "站点id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long mid;

    @Schema(description = "店铺名", requiredMode = Schema.RequiredMode.REQUIRED, example = "test账号")
    private String name;

    @Schema(description = "亚马逊店铺id", requiredMode = Schema.RequiredMode.REQUIRED, example = "AZTOL**")
    @JsonProperty("seller_id")
    private String sellerId;

    @Schema(description = "店铺账户名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "account**")
    @JsonProperty("account_name")
    private String accountName;

    @Schema(description = "店铺账号id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @JsonProperty("seller_account_id")
    private Long sellerAccountId;

    @Schema(description = "站点简称，例如NA指北美", requiredMode = Schema.RequiredMode.REQUIRED, example = "EU")
    private String region;

    @Schema(description = "商城所在国家名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "西班牙")
    private String country;

    @Schema(description = "是否授权广告：0 否 1 是", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @JsonProperty("has_ads_setting")
    private Integer hasAdsSetting;

    @Schema(description = "市场id", requiredMode = Schema.RequiredMode.REQUIRED, example = "ATVPDKIKX0DER")
    @JsonProperty("marketplace_id")
    private String marketplaceId;

    @Schema(description = "店铺状态：0 停止同步 1 正常 2 授权异常 3 欠费停服", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;
} 