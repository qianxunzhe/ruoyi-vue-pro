package cn.iocoder.yudao.module.amazon.dal.mysql.keywordasinranking;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordasinranking.KeywordAsinRankingDO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordasinranking.vo.*;

/**
 * 关键词排名任务结果 Mapper
 *
 * @author Demons
 */
@Mapper
public interface KeywordAsinRankingMapper extends BaseMapperX<KeywordAsinRankingDO> {

    default PageResult<KeywordAsinRankingDO> selectPage(KeywordAsinRankingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<KeywordAsinRankingDO>()
                .eqIfPresent(KeywordAsinRankingDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(KeywordAsinRankingDO::getKeyword, reqVO.getKeyword())
                .eqIfPresent(KeywordAsinRankingDO::getKeywordHash, reqVO.getKeywordHash())
                .eqIfPresent(KeywordAsinRankingDO::getAsin, reqVO.getAsin())
                .eqIfPresent(KeywordAsinRankingDO::getCity, reqVO.getCity())
                .eqIfPresent(KeywordAsinRankingDO::getRankType, reqVO.getRankType())
                .eqIfPresent(KeywordAsinRankingDO::getRankPosition, reqVO.getRankPosition())
                .eqIfPresent(KeywordAsinRankingDO::getPageNumber, reqVO.getPageNumber())
                .eqIfPresent(KeywordAsinRankingDO::getPositionInPage, reqVO.getPositionInPage())
                .eqIfPresent(KeywordAsinRankingDO::getAdsType, reqVO.getAdsType())
                .betweenIfPresent(KeywordAsinRankingDO::getCrawlTime, reqVO.getCrawlTime())
                .eqIfPresent(KeywordAsinRankingDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(KeywordAsinRankingDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(KeywordAsinRankingDO::getUserId, reqVO.getUserId())
                .betweenIfPresent(KeywordAsinRankingDO::getCrawlDate, 
                        reqVO.getCrawlDate() != null ? reqVO.getCrawlDate()[0] : null,
                        reqVO.getCrawlDate() != null ? reqVO.getCrawlDate()[1] : null)
                .orderByDesc(KeywordAsinRankingDO::getId));
    }

}