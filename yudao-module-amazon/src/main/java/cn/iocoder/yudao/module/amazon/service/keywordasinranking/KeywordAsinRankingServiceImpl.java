package cn.iocoder.yudao.module.amazon.service.keywordasinranking;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordasinranking.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordasinranking.KeywordAsinRankingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.amazon.dal.mysql.keywordasinranking.KeywordAsinRankingMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * 关键词排名任务结果 Service 实现类
 *
 * @author Demons
 */
@Service
@Validated
public class KeywordAsinRankingServiceImpl implements KeywordAsinRankingService {

    @Resource
    private KeywordAsinRankingMapper keywordAsinRankingMapper;

    @Override
    public Long createKeywordAsinRanking(KeywordAsinRankingSaveReqVO createReqVO) {
        // 插入
        KeywordAsinRankingDO keywordAsinRanking = BeanUtils.toBean(createReqVO, KeywordAsinRankingDO.class);
        keywordAsinRankingMapper.insert(keywordAsinRanking);

        // 返回
        return keywordAsinRanking.getId();
    }

    @Override
    public void updateKeywordAsinRanking(KeywordAsinRankingSaveReqVO updateReqVO) {
        // 校验存在
        validateKeywordAsinRankingExists(updateReqVO.getId());
        // 更新
        KeywordAsinRankingDO updateObj = BeanUtils.toBean(updateReqVO, KeywordAsinRankingDO.class);
        keywordAsinRankingMapper.updateById(updateObj);
    }

    @Override
    public void deleteKeywordAsinRanking(Long id) {
        // 校验存在
        validateKeywordAsinRankingExists(id);
        // 删除
        keywordAsinRankingMapper.deleteById(id);
    }

    @Override
        public void deleteKeywordAsinRankingListByIds(List<Long> ids) {
        // 删除
        keywordAsinRankingMapper.deleteByIds(ids);
        }


    private void validateKeywordAsinRankingExists(Long id) {
        if (keywordAsinRankingMapper.selectById(id) == null) {
            throw exception(KEYWORD_ASIN_RANKING_NOT_EXISTS);
        }
    }

    @Override
    public KeywordAsinRankingDO getKeywordAsinRanking(Long id) {
        return keywordAsinRankingMapper.selectById(id);
    }

    @Override
    public PageResult<KeywordAsinRankingDO> getKeywordAsinRankingPage(KeywordAsinRankingPageReqVO pageReqVO) {
        return keywordAsinRankingMapper.selectPage(pageReqVO);
    }

}