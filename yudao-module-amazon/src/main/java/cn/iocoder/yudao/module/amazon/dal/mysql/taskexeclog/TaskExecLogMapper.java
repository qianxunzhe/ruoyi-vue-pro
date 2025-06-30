package cn.iocoder.yudao.module.amazon.dal.mysql.taskexeclog;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.taskexeclog.TaskExecLogDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.amazon.controller.admin.taskexeclog.vo.*;

/**
 * Amazon任务执行日志 Mapper
 *
 * @author Demons
 */
@Mapper
public interface TaskExecLogMapper extends BaseMapperX<TaskExecLogDO> {

    default PageResult<TaskExecLogDO> selectPage(TaskExecLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TaskExecLogDO>()
                .eqIfPresent(TaskExecLogDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(TaskExecLogDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(TaskExecLogDO::getStartTime, reqVO.getStartTime())
                .betweenIfPresent(TaskExecLogDO::getEndTime, reqVO.getEndTime())
                .eqIfPresent(TaskExecLogDO::getDurationSeconds, reqVO.getDurationSeconds())
                .eqIfPresent(TaskExecLogDO::getErrorMessage, reqVO.getErrorMessage())
                .eqIfPresent(TaskExecLogDO::getRecordsSaved, reqVO.getRecordsSaved())
                .betweenIfPresent(TaskExecLogDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(TaskExecLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(TaskExecLogDO::getZipcodeNum, reqVO.getZipcodeNum())
                .eqIfPresent(TaskExecLogDO::getKeywordNum, reqVO.getKeywordNum())
                .eqIfPresent(TaskExecLogDO::getPageNum, reqVO.getPageNum())
                .orderByDesc(TaskExecLogDO::getId));
    }

}