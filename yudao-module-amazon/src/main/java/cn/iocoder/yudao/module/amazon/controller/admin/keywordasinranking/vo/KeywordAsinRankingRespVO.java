package cn.iocoder.yudao.module.amazon.controller.admin.keywordasinranking.vo;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;
import cn.iocoder.yudao.module.amazon.enums.DictTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import com.alibaba.excel.annotation.*;

@Schema(description = "管理后台 - 关键词排名任务结果 Response VO")
@Data
@ExcelIgnoreUnannotated
public class KeywordAsinRankingRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31580")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "城市", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("城市")
    private String city;

    @Schema(description = "关键词", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("关键词")
    private String keyword;

    @Schema(description = "ASIN", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("ASIN")
    private String asin;

    @Schema(description = "排名类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "排名类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.RANK_TYPE)
    private Short rankType;

    @ExcelProperty("位置")
    private String position;

    @Schema(description = "排名", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排名")
    private Integer rankPosition;

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("页码")
    private Integer pageNumber;

    @Schema(description = "页内位置", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("页内位置")
    private Integer positionInPage;


    @Schema(description = "采集时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("采集时间")
    private LocalDateTime crawlTime;

}