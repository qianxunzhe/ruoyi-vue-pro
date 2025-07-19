package cn.iocoder.yudao.module.amazon.controller.admin.wordcounter.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - Amazon关键词匹配日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class WordCounterListVO {

    private List<WordCounterReqVO> keywordStats;
}
