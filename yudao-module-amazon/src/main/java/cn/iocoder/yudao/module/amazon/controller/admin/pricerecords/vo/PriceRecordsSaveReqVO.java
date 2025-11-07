package cn.iocoder.yudao.module.amazon.controller.admin.pricerecords.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 亚马逊产品价格记录新增/修改 Request VO")
@Data
public class PriceRecordsSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15137")
    private Long id;

    @Schema(description = "产品识别码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "产品识别码不能为空")
    private String asin;

    @Schema(description = "系列名称")
    private String spu;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "图片URL", example = "https://www.iocoder.cn")
    private String imageUrl;

    @Schema(description = "价格", example = "6850")
    private BigDecimal price;

    @Schema(description = "会员价", example = "22743")
    private BigDecimal primePrice;

    @Schema(description = "优惠券折扣", example = "28990")
    private String couponDiscount;

    @Schema(description = "优惠后价格", example = "8275")
    private BigDecimal couponPrice;

    @Schema(description = "货币类型")
    private String currency;

    @Schema(description = "库存状态")
    private String availability;

    @Schema(description = "频繁退货标志")
    private Boolean frequentlyReturned;

    @Schema(description = "爬取日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "爬取日期不能为空")
    private LocalDate scrapeDate;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13397")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

}