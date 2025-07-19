package cn.iocoder.yudao.module.amazon.service.asinreview;

import java.util.*;
import java.time.LocalDate;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.asinreview.AsinReviewDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * Amazon ASIN商品评论数据 Service 接口
 *
 * @author Demons
 */
public interface AsinReviewService {

    /**
     * 创建Amazon ASIN商品评论数据
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAsinReview(@Valid AsinReviewSaveReqVO createReqVO);

    /**
     * 更新Amazon ASIN商品评论数据
     *
     * @param updateReqVO 更新信息
     */
    void updateAsinReview(@Valid AsinReviewSaveReqVO updateReqVO);

    /**
     * 删除Amazon ASIN商品评论数据
     *
     * @param id 编号
     */
    void deleteAsinReview(Integer id);

    /**
    * 批量删除Amazon ASIN商品评论数据
    *
    * @param ids 编号
    */
    void deleteAsinReviewListByIds(List<Integer> ids);

    /**
     * 获得Amazon ASIN商品评论数据
     *
     * @param id 编号
     * @return Amazon ASIN商品评论数据
     */
    AsinReviewDO getAsinReview(Integer id);

    /**
     * 获得Amazon ASIN商品评论数据分页
     *
     * @param pageReqVO 分页查询
     * @return Amazon ASIN商品评论数据分页
     */
    PageResult<AsinReviewDO> getAsinReviewPage(AsinReviewPageReqVO pageReqVO);

    /**
     * 处理指定任务的ASIN评论监控（核心业务方法）
     * 
     * @param taskId 任务ID
     * @return 处理结果统计
     */
    Map<String, Object> processAsinReviewMonitoring(Long taskId);

    /**
     * 初始化ASIN的历史评论数据
     * 
     * @param asin ASIN编码
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean initializeAsinReviewHistory(String asin, Long taskId, Long userId);

    /**
     * 更新ASIN的增量评论数据（昨日新增）
     * 
     * @param asin ASIN编码
     * @param taskId 任务ID
     * @return 昨日新增数量
     */
    int updateAsinReviewIncrement(String asin, Long taskId);

    /**
     * 根据ASIN和TaskId获取评论记录
     * 
     * @param asin ASIN编码
     * @param taskId 任务ID
     * @return 评论记录
     */
    AsinReviewDO getByAsinAndTaskId(String asin, Long taskId);

    /**
     * 查询ASIN在指定日期范围内的增量记录
     * 
     * @param asin ASIN编码
     * @param taskId 任务ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 增量记录列表
     */
    List<AsinReviewDO> getIncrementsByAsinAndDateRange(String asin, Long taskId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取评论统计分析数据
     * 
     * @param reqVO 统计请求参数
     * @return 统计分析响应数据
     */
    AsinReviewAnalysisRespVO getReviewAnalysisData(AsinReviewAnalysisReqVO reqVO);
}