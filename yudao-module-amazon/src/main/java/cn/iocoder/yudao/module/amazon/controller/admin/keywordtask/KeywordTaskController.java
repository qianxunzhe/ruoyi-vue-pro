package cn.iocoder.yudao.module.amazon.controller.admin.keywordtask;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask.KeywordTaskDO;
import cn.iocoder.yudao.module.amazon.service.keywordtask.KeywordTaskService;

@Tag(name = "管理后台 - 关键词排名任务")
@RestController
@RequestMapping("/amazon/keyword-task")
@Validated
@Slf4j
public class KeywordTaskController {

    @Resource
    private KeywordTaskService keywordTaskService;

    @PostMapping("/create")
    @Operation(summary = "创建关键词排名任务")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-task:create')")
    public CommonResult<Long> createKeywordTask(@Valid @RequestBody KeywordTaskSaveReqVO createReqVO) {
        log.warn(JsonUtils.toJsonString(createReqVO));
        createReqVO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        return success(keywordTaskService.createKeywordTask(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新关键词排名任务")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-task:update')")
    public CommonResult<Boolean> updateKeywordTask(@Valid @RequestBody KeywordTaskSaveReqVO updateReqVO) {

        keywordTaskService.updateKeywordTask(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除关键词排名任务")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:keyword-task:delete')")
    public CommonResult<Boolean> deleteKeywordTask(@RequestParam("id") Long id) {
        keywordTaskService.deleteKeywordTask(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除关键词排名任务")
                @PreAuthorize("@ss.hasPermission('amazon:keyword-task:delete')")
    public CommonResult<Boolean> deleteKeywordTaskList(@RequestParam("ids") List<Long> ids) {
        keywordTaskService.deleteKeywordTaskListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得关键词排名任务")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-task:query')")
    public CommonResult<KeywordTaskRespVO> getKeywordTask(@RequestParam("id") Long id) {
        KeywordTaskDO keywordTask = keywordTaskService.getKeywordTask(id);
        return success(BeanUtils.toBean(keywordTask, KeywordTaskRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得关键词排名任务分页")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-task:query')")
    @DataPermission(enable = true)
    public CommonResult<PageResult<KeywordTaskRespVO>> getKeywordTaskPage(@Valid KeywordTaskPageReqVO pageReqVO) {
        PageResult<KeywordTaskDO> pageResult = keywordTaskService.getKeywordTaskPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, KeywordTaskRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出关键词排名任务 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-task:export')")
    @ApiAccessLog(operateType = EXPORT)
    @DataPermission(enable = true)
    public void exportKeywordTaskExcel(@Valid KeywordTaskPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<KeywordTaskDO> list = keywordTaskService.getKeywordTaskPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "关键词排名任务.xls", "数据", KeywordTaskRespVO.class,
                        BeanUtils.toBean(list, KeywordTaskRespVO.class));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新关键词排名任务的状态")
    @PreAuthorize("@ss.hasPermission('amazon:keyword-task:update')")
    public CommonResult<Boolean> updateKeywordTaskStatus(@Valid @RequestBody KeywordTaskUpdateStatusReqVO updateStatusReqVO) {
        log.warn(JsonUtils.toJsonString(updateStatusReqVO));
        keywordTaskService.updateKeywordTaskStatus(updateStatusReqVO);
        return success(true);
    }

}