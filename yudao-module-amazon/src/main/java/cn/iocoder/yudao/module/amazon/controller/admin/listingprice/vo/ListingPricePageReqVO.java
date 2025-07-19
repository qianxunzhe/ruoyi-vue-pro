package cn.iocoder.yudao.module.amazon.controller.admin.listingprice.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 价格监控结果分页 Request VO")
@Data
public class ListingPricePageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "17204")
    private Long userId;

    @Schema(description = "任务执行ID", example = "28548")
    private Integer taskExecLogId;

    @Schema(description = "产品ASIN")
    private String asin;

    @Schema(description = "市场代码")
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

    @Schema(description = "抓取时间")
    private LocalDateTime scrapedAt;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "备注", example = "你说的对")
    private String remark;

    private Long taskId;

}