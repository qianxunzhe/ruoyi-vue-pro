package cn.iocoder.yudao.module.amazon.service.taskexeclog;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.taskexeclog.TaskExecLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.amazon.dal.mysql.taskexeclog.TaskExecLogMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * Amazon任务执行日志 Service 实现类
 *
 * @author Demons
 */
@Service
@Validated
public class TaskExecLogServiceImpl implements TaskExecLogService {

    @Resource
    private TaskExecLogMapper taskExecLogMapper;

    @Override
    public Long createTaskExecLog(TaskExecLogSaveReqVO createReqVO) {
        // 插入
        TaskExecLogDO taskExecLog = BeanUtils.toBean(createReqVO, TaskExecLogDO.class);
        taskExecLogMapper.insert(taskExecLog);

        // 返回
        return taskExecLog.getId();
    }

    @Override
    public void updateTaskExecLog(TaskExecLogSaveReqVO updateReqVO) {
        // 校验存在
        validateTaskExecLogExists(updateReqVO.getId());
        // 更新
        TaskExecLogDO updateObj = BeanUtils.toBean(updateReqVO, TaskExecLogDO.class);
        taskExecLogMapper.updateById(updateObj);
    }

    @Override
    public void deleteTaskExecLog(Long id) {
        // 校验存在
        validateTaskExecLogExists(id);
        // 删除
        taskExecLogMapper.deleteById(id);
    }

    @Override
        public void deleteTaskExecLogListByIds(List<Long> ids) {
        // 删除
        taskExecLogMapper.deleteByIds(ids);
        }


    private void validateTaskExecLogExists(Long id) {
        if (taskExecLogMapper.selectById(id) == null) {
            throw exception(TASK_EXEC_LOG_NOT_EXISTS);
        }
    }

    @Override
    public TaskExecLogDO getTaskExecLog(Long id) {
        return taskExecLogMapper.selectById(id);
    }

    @Override
    public PageResult<TaskExecLogDO> getTaskExecLogPage(TaskExecLogPageReqVO pageReqVO) {
        return taskExecLogMapper.selectPage(pageReqVO);
    }

}