package cn.iocoder.yudao.module.amazon.service.keywordranking;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 关键词排名任务结果 Service 接口
 *
 * @author 芋道源码
 */
public interface KeywordRankingService {

    /**
     * 创建关键词排名任务结果
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createKeywordRanking(@Valid KeywordRankingSaveReqVO createReqVO);

    /**
     * 更新关键词排名任务结果
     *
     * @param updateReqVO 更新信息
     */
    void updateKeywordRanking(@Valid KeywordRankingSaveReqVO updateReqVO);

    /**
     * 删除关键词排名任务结果
     *
     * @param id 编号
     */
    void deleteKeywordRanking(Long id);

    /**
    * 批量删除关键词排名任务结果
    *
    * @param ids 编号
    */
    void deleteKeywordRankingListByIds(List<Long> ids);

    /**
     * 获得关键词排名任务结果
     *
     * @param id 编号
     * @return 关键词排名任务结果
     */
    KeywordRankingDO getKeywordRanking(Long id);

    /**
     * 获得关键词排名任务结果分页
     *
     * @param pageReqVO 分页查询
     * @return 关键词排名任务结果分页
     */
    PageResult<KeywordRankingDO> getKeywordRankingPage(KeywordRankingPageReqVO pageReqVO);

    /**
     * 获得关键词排名数据分析
     *
     * @param reqVO 分析请求
     * @return 分析结果
     */
    KeywordRankingAnalysisRespVO getKeywordRankingAnalysis(@Valid KeywordRankingAnalysisReqVO reqVO);

}