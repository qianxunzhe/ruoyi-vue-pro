package cn.iocoder.yudao.module.amazon.service.keywordasinranking;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordasinranking.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordasinranking.KeywordAsinRankingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 关键词排名任务结果 Service 接口
 *
 * @author Demons
 */
public interface KeywordAsinRankingService {

    /**
     * 创建关键词排名任务结果
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createKeywordAsinRanking(@Valid KeywordAsinRankingSaveReqVO createReqVO);

    /**
     * 更新关键词排名任务结果
     *
     * @param updateReqVO 更新信息
     */
    void updateKeywordAsinRanking(@Valid KeywordAsinRankingSaveReqVO updateReqVO);

    /**
     * 删除关键词排名任务结果
     *
     * @param id 编号
     */
    void deleteKeywordAsinRanking(Long id);

    /**
    * 批量删除关键词排名任务结果
    *
    * @param ids 编号
    */
    void deleteKeywordAsinRankingListByIds(List<Long> ids);

    /**
     * 获得关键词排名任务结果
     *
     * @param id 编号
     * @return 关键词排名任务结果
     */
    KeywordAsinRankingDO getKeywordAsinRanking(Long id);

    /**
     * 获得关键词排名任务结果分页
     *
     * @param pageReqVO 分页查询
     * @return 关键词排名任务结果分页
     */
    PageResult<KeywordAsinRankingDO> getKeywordAsinRankingPage(KeywordAsinRankingPageReqVO pageReqVO);

}