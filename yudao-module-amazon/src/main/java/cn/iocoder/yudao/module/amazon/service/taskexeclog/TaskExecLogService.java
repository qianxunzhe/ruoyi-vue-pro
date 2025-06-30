package cn.iocoder.yudao.module.amazon.service.taskexeclog;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.taskexeclog.TaskExecLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * Amazon任务执行日志 Service 接口
 *
 * @author Demons
 */
public interface TaskExecLogService {

    /**
     * 创建Amazon任务执行日志
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTaskExecLog(@Valid TaskExecLogSaveReqVO createReqVO);

    /**
     * 更新Amazon任务执行日志
     *
     * @param updateReqVO 更新信息
     */
    void updateTaskExecLog(@Valid TaskExecLogSaveReqVO updateReqVO);

    /**
     * 删除Amazon任务执行日志
     *
     * @param id 编号
     */
    void deleteTaskExecLog(Long id);

    /**
    * 批量删除Amazon任务执行日志
    *
    * @param ids 编号
    */
    void deleteTaskExecLogListByIds(List<Long> ids);

    /**
     * 获得Amazon任务执行日志
     *
     * @param id 编号
     * @return Amazon任务执行日志
     */
    TaskExecLogDO getTaskExecLog(Long id);

    /**
     * 获得Amazon任务执行日志分页
     *
     * @param pageReqVO 分页查询
     * @return Amazon任务执行日志分页
     */
    PageResult<TaskExecLogDO> getTaskExecLogPage(TaskExecLogPageReqVO pageReqVO);

}