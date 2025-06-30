package cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog;

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

import cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.taskexeclog.TaskExecLogDO;
import cn.iocoder.yudao.module.amazon.service.taskexeclog.TaskExecLogService;

@Tag(name = "管理后台 - Amazon任务执行日志")
@RestController
@RequestMapping("/amazon/task-exec-log")
@Validated
public class TaskExecLogController {

    @Resource
    private TaskExecLogService taskExecLogService;

    @PostMapping("/create")
    @Operation(summary = "创建Amazon任务执行日志")
    @PreAuthorize("@ss.hasPermission('amazon:task-exec-log:create')")
    public CommonResult<Long> createTaskExecLog(@Valid @RequestBody TaskExecLogSaveReqVO createReqVO) {
        return success(taskExecLogService.createTaskExecLog(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新Amazon任务执行日志")
    @PreAuthorize("@ss.hasPermission('amazon:task-exec-log:update')")
    public CommonResult<Boolean> updateTaskExecLog(@Valid @RequestBody TaskExecLogSaveReqVO updateReqVO) {
        taskExecLogService.updateTaskExecLog(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除Amazon任务执行日志")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:task-exec-log:delete')")
    public CommonResult<Boolean> deleteTaskExecLog(@RequestParam("id") Long id) {
        taskExecLogService.deleteTaskExecLog(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除Amazon任务执行日志")
                @PreAuthorize("@ss.hasPermission('amazon:task-exec-log:delete')")
    public CommonResult<Boolean> deleteTaskExecLogList(@RequestParam("ids") List<Long> ids) {
        taskExecLogService.deleteTaskExecLogListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得Amazon任务执行日志")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:task-exec-log:query')")
    public CommonResult<TaskExecLogRespVO> getTaskExecLog(@RequestParam("id") Long id) {
        TaskExecLogDO taskExecLog = taskExecLogService.getTaskExecLog(id);
        return success(BeanUtils.toBean(taskExecLog, TaskExecLogRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得Amazon任务执行日志分页")
    @PreAuthorize("@ss.hasPermission('amazon:task-exec-log:query')")
    public CommonResult<PageResult<TaskExecLogRespVO>> getTaskExecLogPage(@Valid TaskExecLogPageReqVO pageReqVO) {
        PageResult<TaskExecLogDO> pageResult = taskExecLogService.getTaskExecLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, TaskExecLogRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出Amazon任务执行日志 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:task-exec-log:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTaskExecLogExcel(@Valid TaskExecLogPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<TaskExecLogDO> list = taskExecLogService.getTaskExecLogPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "Amazon任务执行日志.xls", "数据", TaskExecLogRespVO.class,
                        BeanUtils.toBean(list, TaskExecLogRespVO.class));
    }

}