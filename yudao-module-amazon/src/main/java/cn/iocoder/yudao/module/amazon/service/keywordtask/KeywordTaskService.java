package cn.iocoder.yudao.module.amazon.service.keywordtask;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask.KeywordTaskDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 关键词排名任务 Service 接口
 *
 * @author 芋道源码
 */
public interface KeywordTaskService {

    /**
     * 创建关键词排名任务
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createKeywordTask(@Valid KeywordTaskSaveReqVO createReqVO);

    /**
     * 更新关键词排名任务
     *
     * @param updateReqVO 更新信息
     */
    void updateKeywordTask(@Valid KeywordTaskSaveReqVO updateReqVO);

    void updateKeywordTaskStatus(@Valid KeywordTaskUpdateStatusReqVO updateStatusReqVO);

    /**
     * 删除关键词排名任务
     *
     * @param id 编号
     */
    void deleteKeywordTask(Long id);

    /**
    * 批量删除关键词排名任务
    *
    * @param ids 编号
    */
    void deleteKeywordTaskListByIds(List<Long> ids);

    /**
     * 获得关键词排名任务
     *
     * @param id 编号
     * @return 关键词排名任务
     */
    KeywordTaskDO getKeywordTask(Long id);

    /**
     * 获得关键词排名任务分页
     *
     * @param pageReqVO 分页查询
     * @return 关键词排名任务分页
     */
    PageResult<KeywordTaskDO> getKeywordTaskPage(KeywordTaskPageReqVO pageReqVO);

}