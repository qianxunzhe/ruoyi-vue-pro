package cn.iocoder.yudao.module.amazon.service.keywordtask;


import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;

import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.ObjectUtils;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordtask.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordtask.KeywordTaskDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.amazon.dal.mysql.keywordtask.KeywordTaskMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * 关键词排名任务 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class KeywordTaskServiceImpl implements KeywordTaskService {

    @Resource
    private KeywordTaskMapper keywordTaskMapper;

    @Value("${amazon.keyword-task-url}")
    private String AMAZON_KEYWODTASK_URL;

    @Override
    public Long createKeywordTask(KeywordTaskSaveReqVO createReqVO) {
        // 插入
        KeywordTaskDO keywordTask = BeanUtils.toBean(createReqVO, KeywordTaskDO.class);
        keywordTaskMapper.insert(keywordTask);

        if (keywordTask.getTaskType() == 1) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("task_id", keywordTask.getId().toString());

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json;charset=UTF-8");
            headers.put("Accept", "application/json;charset=UTF-8");
            String result=  HttpUtils.post(AMAZON_KEYWODTASK_URL, headers, JsonUtils.toJsonString(paramMap));

            log.info("<OK>[{}]<OK>", result);
        }

        // 返回
        return keywordTask.getId();
    }

    @Override
    public void updateKeywordTask(KeywordTaskSaveReqVO updateReqVO) {
        // 校验存在
        validateKeywordTaskExists(updateReqVO.getId());
        // 更新
        KeywordTaskDO updateObj = BeanUtils.toBean(updateReqVO, KeywordTaskDO.class);
        keywordTaskMapper.updateById(updateObj);
    }

    @Override
    public void updateKeywordTaskStatus(@Valid KeywordTaskUpdateStatusReqVO updateStatusReqVO) {
        // 校验存在
        validateKeywordTaskExists(updateStatusReqVO.getId());
//        KeywordTaskDO updateObj = new KeywordTaskDO();
//        updateObj.setEnabled(updateStatusReqVO.getEnabled());
//        UpdateWrapper<KeywordTaskDO> whereWrapper = new UpdateWrapper<>();
//        whereWrapper.eq("id", updateStatusReqVO.getId());
//        keywordTaskMapper.update(updateObj, whereWrapper);

        KeywordTaskDO keywordTaskDO = keywordTaskMapper.selectById(updateStatusReqVO.getId());
        keywordTaskDO.setEnabled(updateStatusReqVO.getEnabled());
        keywordTaskMapper.updateById(keywordTaskDO);
    }



    @Override
    public void deleteKeywordTask(Long id) {
        // 校验存在
        validateKeywordTaskExists(id);
        // 删除
        keywordTaskMapper.deleteById(id);
    }

    @Override
        public void deleteKeywordTaskListByIds(List<Long> ids) {
        // 删除
        keywordTaskMapper.deleteByIds(ids);
        }


    private void validateKeywordTaskExists(Long id) {
        if (keywordTaskMapper.selectById(id) == null) {
            throw exception(KEYWORD_TASK_NOT_EXISTS);
        }
    }

    @Override
    public KeywordTaskDO getKeywordTask(Long id) {
        return keywordTaskMapper.selectById(id);
    }

    @Override
    public PageResult<KeywordTaskDO> getKeywordTaskPage(KeywordTaskPageReqVO pageReqVO) {
        return keywordTaskMapper.selectPage(pageReqVO);
    }

}