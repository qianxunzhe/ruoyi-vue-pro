package cn.iocoder.yudao.module.amazon.service.keywordranking;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.amazon.dal.mysql.keywordranking.KeywordRankingMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * 关键词排名任务结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class KeywordRankingServiceImpl implements KeywordRankingService {

    @Resource
    private KeywordRankingMapper keywordRankingMapper;

    @Override
    public Long createKeywordRanking(KeywordRankingSaveReqVO createReqVO) {
        // 插入
        KeywordRankingDO keywordRanking = BeanUtils.toBean(createReqVO, KeywordRankingDO.class);
        keywordRankingMapper.insert(keywordRanking);

        // 返回
        return keywordRanking.getId();
    }

    @Override
    public void updateKeywordRanking(KeywordRankingSaveReqVO updateReqVO) {
        // 校验存在
        validateKeywordRankingExists(updateReqVO.getId());
        // 更新
        KeywordRankingDO updateObj = BeanUtils.toBean(updateReqVO, KeywordRankingDO.class);
        keywordRankingMapper.updateById(updateObj);
    }

    @Override
    public void deleteKeywordRanking(Long id) {
        // 校验存在
        validateKeywordRankingExists(id);
        // 删除
        keywordRankingMapper.deleteById(id);
    }

    @Override
        public void deleteKeywordRankingListByIds(List<Long> ids) {
        // 删除
        keywordRankingMapper.deleteByIds(ids);
        }


    private void validateKeywordRankingExists(Long id) {
        if (keywordRankingMapper.selectById(id) == null) {
            throw exception(KEYWORD_RANKING_NOT_EXISTS);
        }
    }

    @Override
    public KeywordRankingDO getKeywordRanking(Long id) {
        return keywordRankingMapper.selectById(id);
    }

    @Override
    public PageResult<KeywordRankingDO> getKeywordRankingPage(KeywordRankingPageReqVO pageReqVO) {
        return keywordRankingMapper.selectPage(pageReqVO);
    }

}