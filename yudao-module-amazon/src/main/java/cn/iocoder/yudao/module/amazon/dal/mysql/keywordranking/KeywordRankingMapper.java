package cn.iocoder.yudao.module.amazon.dal.mysql.keywordranking;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo.*;

/**
 * 关键词排名任务结果 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface KeywordRankingMapper extends BaseMapperX<KeywordRankingDO> {

    default PageResult<KeywordRankingDO> selectPage(KeywordRankingPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<KeywordRankingDO>()
                .eqIfPresent(KeywordRankingDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(KeywordRankingDO::getKeyword, reqVO.getKeyword())
                .eqIfPresent(KeywordRankingDO::getKeywordHash, reqVO.getKeywordHash())
                .eqIfPresent(KeywordRankingDO::getAsin, reqVO.getAsin())
                .eqIfPresent(KeywordRankingDO::getCity, reqVO.getCity())
                .eqIfPresent(KeywordRankingDO::getRankType, reqVO.getRankType())
                .eqIfPresent(KeywordRankingDO::getAdsType, reqVO.getAdsType())
                .betweenIfPresent(KeywordRankingDO::getCrawlTime, reqVO.getCrawlTime())
                .eqIfPresent(KeywordRankingDO::getCrawlDate, reqVO.getCrawlDate())
                .orderByDesc(KeywordRankingDO::getId));
    }

}