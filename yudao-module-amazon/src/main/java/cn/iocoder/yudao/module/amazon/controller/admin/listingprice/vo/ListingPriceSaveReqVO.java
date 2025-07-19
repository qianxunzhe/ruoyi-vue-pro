package cn.iocoder.yudao.module.amazon.controller.admin.listingprice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 价格监控结果新增/修改 Request VO")
@Data
public class ListingPriceSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15714")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17204")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "任务执行ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28548")
    @NotNull(message = "任务执行ID不能为空")
    private Integer taskExecLogId;

    @Schema(description = "产品ASIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "产品ASIN不能为空")
    private String asin;

    @Schema(description = "市场代码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "市场代码不能为空")
    private String marketplace;

    @Schema(description = "BuyBox价格", example = "22342")
    private BigDecimal buyboxPrice;

    @Schema(description = "商品价格", example = "3431")
    private BigDecimal price;

    @Schema(description = "Prime价格", example = "23242")
    private BigDecimal primePrice;

    @Schema(description = "优惠券价格", example = "9223")
    private BigDecimal couponPrice;

    @Schema(description = "优惠券折扣", example = "15317")
    private String couponDiscount;

    @Schema(description = "促销价格", example = "18161")
    private BigDecimal dealPrice;

    @Schema(description = "促销信息")
    private String dealInfo;

    @Schema(description = "FBA价格", example = "911")
    private BigDecimal fbaPrice;

    @Schema(description = "FBM价格", example = "17533")
    private BigDecimal fbmPrice;

    @Schema(description = "标价", example = "9839")
    private BigDecimal listPrice;

    @Schema(description = "抓取时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "抓取时间不能为空")
    private LocalDateTime scrapedAt;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

}