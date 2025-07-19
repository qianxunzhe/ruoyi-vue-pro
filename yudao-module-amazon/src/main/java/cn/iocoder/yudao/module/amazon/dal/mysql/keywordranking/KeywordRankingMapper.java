package cn.iocoder.yudao.module.amazon.dal.mysql.keywordranking;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.keywordranking.KeywordRankingDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.amazon.controller.admin.keywordranking.vo.*;

import java.time.LocalDate;
import java.util.List;

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

    /**
     * 查询分析数据 - 获取最新数据（简化版本）
     */
    @Select("""
        <script>
        SELECT 
            keyword,
            asin,
            page_number,
            position_in_page,
            crawl_date,
            crawl_time
        FROM amazon_keyword_ranking 
        WHERE city = #{city}
            <if test="rankType != null">
                <if test="rankType != null">
                    AND rank_type = #{rankType}
                </if>
            </if>
            AND crawl_date BETWEEN #{startDate} AND #{endDate}
            AND deleted = 0
            <if test="taskId != null">
                AND task_id = #{taskId}
            </if>
            <if test="keywords != null and keywords.size() > 0">
                AND keyword IN 
                <foreach collection="keywords" item="keyword" open="(" separator="," close=")">
                    #{keyword}
                </foreach>
            </if>
            <if test="maxPosition != null and maxPosition > 0">
                AND (page_number - 1) * 16 + position_in_page &lt;= #{maxPosition}
            </if>
        ORDER BY keyword, page_number, position_in_page, crawl_date, crawl_time DESC
        </script>
    """)
    List<KeywordRankingDO> selectAnalysisDataLatest(
                                                    @Param("city") String city,
                                                    @Param("rankType") Short rankType,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate,
                                                    @Param("taskId") Long taskId,
                                                    @Param("keywords") List<String> keywords,
                                                    @Param("maxPosition") Integer maxPosition);

    /**
     * 查询分析数据 - 获取最早数据（简化版本）
     */
    @Select("""
        <script>
        SELECT 
            keyword,
            asin,
            page_number,
            position_in_page,
            crawl_date,
            crawl_time
        FROM amazon_keyword_ranking 
        WHERE city = #{city}
            <if test="rankType != null">
                <if test="rankType != null">
                    AND rank_type = #{rankType}
                </if>
            </if>
            AND crawl_date BETWEEN #{startDate} AND #{endDate}
            AND deleted = 0
            <if test="taskId != null">
                AND task_id = #{taskId}
            </if>
            <if test="keywords != null and keywords.size() > 0">
                AND keyword IN 
                <foreach collection="keywords" item="keyword" open="(" separator="," close=")">
                    #{keyword}
                </foreach>
            </if>
            <if test="maxPosition != null and maxPosition > 0">
                AND (page_number - 1) * 16 + position_in_page &lt;= #{maxPosition}
            </if>
        ORDER BY keyword, page_number, position_in_page, crawl_date, crawl_time ASC
        </script>
    """)
    List<KeywordRankingDO> selectAnalysisDataFirst(@Param("city") String city,
                                                   @Param("rankType") Short rankType,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   @Param("taskId") Long taskId,
                                                   @Param("keywords") List<String> keywords,
                                                   @Param("maxPosition") Integer maxPosition);

    /**
     * 查询分析数据 - 获取所有数据
     */
    @Select("""
        <script>
        SELECT 
            keyword,
            asin,
            page_number,
            position_in_page,
            crawl_date,
            crawl_time
        FROM amazon_keyword_ranking 
        WHERE 
            city = #{city}
            <if test="rankType != null">
                <if test="rankType != null">
                    AND rank_type = #{rankType}
                </if>
            </if>
            AND crawl_date BETWEEN #{startDate} AND #{endDate}
            AND deleted = 0
            <if test="taskId != null">
                AND task_id = #{taskId}
            </if>
            <if test="keywords != null and keywords.size() > 0">
                AND keyword IN 
                <foreach collection="keywords" item="keyword" open="(" separator="," close=")">
                    #{keyword}
                </foreach>
            </if>
            <if test="maxPosition != null and maxPosition > 0">
                AND (page_number - 1) * 16 + position_in_page &lt;= #{maxPosition}
            </if>
        ORDER BY keyword, page_number, position_in_page, crawl_date, crawl_time
        LIMIT 10000
        </script>
    """)
    List<KeywordRankingDO> selectAnalysisDataAll(
                                                 @Param("city") String city,
                                                 @Param("rankType") Short rankType,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("taskId") Long taskId,
                                                 @Param("keywords") List<String> keywords,
                                                 @Param("maxPosition") Integer maxPosition);

}