package cn.iocoder.yudao.module.amazon.controller.admin.pricerecords.vo;

import lombok.*;

import java.time.LocalDate;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;
import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 亚马逊产品价格记录分页 Request VO")
@Data
public class PriceRecordsPageReqVO extends PageParam {

    @Schema(description = "ASIN")
    private String asin;

    @Schema(description = "SPU")
    private String spu;

    @Schema(description = "价格", example = "6850")
    private BigDecimal price;

    @Schema(description = "会员价", example = "22743")
    private BigDecimal primePrice;

    @Schema(description = "优惠券折扣", example = "28990")
    private String couponDiscount;

    @Schema(description = "优惠后价格", example = "8275")
    private BigDecimal couponPrice;

    @Schema(description = "库存状态")
    private String availability;

    @Schema(description = "频繁退货标志")
    private Boolean frequentlyReturned;

    @Schema(description = "爬取日期")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] scrapeDate;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;


}