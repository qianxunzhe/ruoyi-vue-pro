package cn.iocoder.yudao.module.amazon.controller.admin.wordcounter.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - Amazon关键词匹配日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WordCounterReqVO {

    @Schema(description = "Keyword", requiredMode = Schema.RequiredMode.REQUIRED, example = "2822")
    @ExcelProperty("Keyword")
    private String keyword;

    @ExcelProperty("匹配次数")
    private Integer totalCount;

    @ExcelProperty("标题")
    private Boolean inTitle;

    @ExcelProperty("描述1")
    private Boolean inDescription1;

    @ExcelProperty("描述2")
    private Boolean inDescription2;

    @ExcelProperty("描述3")
    private Boolean inDescription3;

    @ExcelProperty("描述4")
    private Boolean inDescription4;

    @ExcelProperty("描述5")
    private Boolean inDescription5;
}
