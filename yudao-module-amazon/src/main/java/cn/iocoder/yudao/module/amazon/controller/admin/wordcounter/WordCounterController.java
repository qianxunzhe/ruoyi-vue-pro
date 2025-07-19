package cn.iocoder.yudao.module.amazon.controller.admin.wordcounter;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo.TaskExecLogPageReqVO;
import cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo.TaskExecLogRespVO;
import cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo.TaskExecLogSaveReqVO;
import cn.iocoder.yudao.module.amazon.controller.admin.wordcounter.vo.WordCounterListVO;
import cn.iocoder.yudao.module.amazon.controller.admin.wordcounter.vo.WordCounterReqVO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.taskexeclog.TaskExecLogDO;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.EXPORT;


@Tag(name = "管理后台 - 关键词匹配任务执行日志")
@RestController
@RequestMapping("/amazon/word-counter")
@Validated
public class WordCounterController {


    private static final Logger log = LoggerFactory.getLogger(WordCounterController.class);

    @PostMapping("/export-excel")
    @Operation(summary = "导出Amazon任务执行日志 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:task-exec-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTaskExecLogExcel(@Valid @RequestBody WordCounterListVO listVO,
                                       HttpServletResponse response) throws IOException {
        log.info("<UNK> Amazon <UNK> Excel",  JSON.toJSONString(listVO));
        // 导出 Excel
        ExcelUtils.write(response, "关键词匹配.xls", "数据", WordCounterReqVO.class,
                BeanUtils.toBean(listVO.getKeywordStats(), WordCounterReqVO.class));
    }
}
