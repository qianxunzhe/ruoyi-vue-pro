package cn.iocoder.yudao.module.amazon.controller.admin.pricerecords.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 亚马逊产品价格记录 Response VO")
@Data
@ExcelIgnoreUnannotated
public class PriceRecordsRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "15137")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "产品识别码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("产品识别码")
    private String asin;

    @Schema(description = "系列名称")
    @ExcelProperty("系列名称")
    private String spu;

    @Schema(description = "标题")
    @ExcelProperty("标题")
    private String title;

    @Schema(description = "图片URL", example = "https://www.iocoder.cn")
    @ExcelProperty("图片URL")
    private String imageUrl;

    @Schema(description = "价格", example = "6850")
    @ExcelProperty("价格")
    private BigDecimal price;

    @Schema(description = "会员价", example = "22743")
    @ExcelProperty("会员价")
    private BigDecimal primePrice;

    @Schema(description = "优惠券折扣", example = "28990")
    @ExcelProperty("优惠券折扣")
    private String couponDiscount;

    @Schema(description = "优惠后价格", example = "8275")
    @ExcelProperty("优惠后价格")
    private BigDecimal couponPrice;

    @Schema(description = "货币类型")
    @ExcelProperty("货币类型")
    private String currency;

    @Schema(description = "库存状态")
    @ExcelProperty("库存状态")
    private String availability;

    @Schema(description = "频繁退货标志")
    @ExcelProperty("频繁退货标志")
    private Boolean frequentlyReturned;

    @Schema(description = "爬取日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("爬取日期")
    private LocalDate scrapeDate;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13397")
    @ExcelProperty("用户ID")
    private Long userId;

}