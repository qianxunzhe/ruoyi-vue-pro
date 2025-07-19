package cn.iocoder.yudao.module.amazon.dal.mysql.asinreview;

import java.util.*;
import java.time.LocalDate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo.AsinReviewStatItemVO;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.asinreview.AsinReviewDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.amazon.controller.admin.asinreview.vo.*;

/**
 * Amazon ASIN商品评论数据 Mapper
 *
 * @author Demons
 */
@Mapper
public interface AsinReviewMapper extends BaseMapperX<AsinReviewDO> {

    default PageResult<AsinReviewDO> selectPage(AsinReviewPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AsinReviewDO>()
                .eqIfPresent(AsinReviewDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AsinReviewDO::getAsin, reqVO.getAsin())
                .eqIfPresent(AsinReviewDO::getParentAsin, reqVO.getParentAsin())
                .eqIfPresent(AsinReviewDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(AsinReviewDO::getReviewNum, reqVO.getReviewNum())
                .eqIfPresent(AsinReviewDO::getScore, reqVO.getScore())
                .eqIfPresent(AsinReviewDO::getRatings, reqVO.getRatings())
                .betweenIfPresent(AsinReviewDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(AsinReviewDO::getRemark, reqVO.getRemark())
                .orderByDesc(AsinReviewDO::getId));
    }

    /**
     * 根据ASIN和TaskId查询记录
     */
    default AsinReviewDO selectByAsinAndTaskId(String asin, Long taskId) {
        return selectOne(new LambdaQueryWrapperX<AsinReviewDO>()
                .eq(AsinReviewDO::getAsin, asin)
                .eq(AsinReviewDO::getTaskId, taskId)
                .last("LIMIT 1"));
    }

    /**
     * 根据TaskId查询所有ASIN记录
     */
    default List<AsinReviewDO> selectByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<AsinReviewDO>()
                .eq(AsinReviewDO::getTaskId, taskId));
    }

    /**
     * 查询需要初始化的ASIN记录（未完成历史数据初始化的）
     */
    default List<AsinReviewDO> selectNeedInitializeByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<AsinReviewDO>()
                .eq(AsinReviewDO::getTaskId, taskId)
                .eq(AsinReviewDO::getInitialized, 0));
    }

    /**
     * 查询已初始化的ASIN记录（用于日常增量更新）
     */
    default List<AsinReviewDO> selectInitializedByTaskId(Long taskId) {
        return selectList(new LambdaQueryWrapperX<AsinReviewDO>()
                .eq(AsinReviewDO::getTaskId, taskId)
                .eq(AsinReviewDO::getInitialized, 1));
    }

    /**
     * 根据ASIN、TaskId和日期查询记录
     */
    default AsinReviewDO selectByAsinTaskIdAndDate(String asin, Long taskId, LocalDate recordDate) {
        return selectOne(new LambdaQueryWrapperX<AsinReviewDO>()
                .eq(AsinReviewDO::getAsin, asin)
                .eq(AsinReviewDO::getTaskId, taskId)
                .eq(AsinReviewDO::getRecordDate, recordDate)
                .last("LIMIT 1"));
    }

    /**
     * 查询ASIN在指定日期范围内的增量记录
     */
    default List<AsinReviewDO> selectIncrementsByAsinAndDateRange(String asin, Long taskId, LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<AsinReviewDO>()
                .eq(AsinReviewDO::getAsin, asin)
                .eq(AsinReviewDO::getTaskId, taskId)
                .eq(AsinReviewDO::getIsInitial, false)
                .between(AsinReviewDO::getRecordDate, startDate, endDate)
                .orderByAsc(AsinReviewDO::getRecordDate));
    }

    /**
     * 根据任务ID和日期范围统计各ASIN的评论增量数据
     */
    default List<AsinReviewStatItemVO> selectReviewStatsByTaskAndDateRange(Long taskId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> rawData = selectMaps(new LambdaQueryWrapperX<AsinReviewDO>()
                .select(AsinReviewDO::getAsin, AsinReviewDO::getRecordDate, AsinReviewDO::getReviewIncrement, AsinReviewDO::getTaskId)
                .eq(AsinReviewDO::getTaskId, taskId)
                .between(AsinReviewDO::getRecordDate, startDate, endDate)
                .orderByAsc(AsinReviewDO::getAsin, AsinReviewDO::getRecordDate));
        
        List<AsinReviewStatItemVO> result = new ArrayList<>();
        for (Map<String, Object> record : rawData) {
            AsinReviewStatItemVO item = new AsinReviewStatItemVO();
            item.setAsin((String) record.get("asin"));
            item.setReviewDate(record.get("record_date") != null ? record.get("record_date").toString() : null);
            item.setReviewCount((Integer) record.get("review_increment"));
            item.setTaskId(record.get("task_id") != null ? Long.valueOf(record.get("task_id").toString()) : null);
            result.add(item);
        }
        return result;
    }
}