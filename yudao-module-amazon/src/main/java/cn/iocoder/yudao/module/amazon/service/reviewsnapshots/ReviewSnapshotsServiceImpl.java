package cn.iocoder.yudao.module.amazon.service.reviewsnapshots;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import cn.iocoder.yudao.module.amazon.controller.admin.reviewsnapshots.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.reviewsnapshots.ReviewSnapshotsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.iocoder.yudao.module.amazon.dal.mysql.reviewsnapshots.ReviewSnapshotsMapper;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * 亚马逊评论快照 Service 实现类
 *
 * @author Demons
 */
@Service
@Validated
public class ReviewSnapshotsServiceImpl implements ReviewSnapshotsService {

    @Resource
    private ReviewSnapshotsMapper reviewSnapshotsMapper;

    @Override
    public Integer createReviewSnapshots(ReviewSnapshotsSaveReqVO createReqVO) {
        // 插入
        ReviewSnapshotsDO reviewSnapshots = BeanUtils.toBean(createReqVO, ReviewSnapshotsDO.class);
        reviewSnapshotsMapper.insert(reviewSnapshots);

        // 返回
        return reviewSnapshots.getId();
    }

    @Override
    public void updateReviewSnapshots(ReviewSnapshotsSaveReqVO updateReqVO) {
        // 校验存在
        validateReviewSnapshotsExists(updateReqVO.getId());
        // 更新
        ReviewSnapshotsDO updateObj = BeanUtils.toBean(updateReqVO, ReviewSnapshotsDO.class);
        reviewSnapshotsMapper.updateById(updateObj);
    }

    @Override
    public void deleteReviewSnapshots(Integer id) {
        // 校验存在
        validateReviewSnapshotsExists(id);
        // 删除
        reviewSnapshotsMapper.deleteById(id);
    }

    @Override
        public void deleteReviewSnapshotsListByIds(List<Integer> ids) {
        // 删除
        reviewSnapshotsMapper.deleteByIds(ids);
        }


    private void validateReviewSnapshotsExists(Integer id) {
        if (reviewSnapshotsMapper.selectById(id) == null) {
            throw exception(REVIEW_SNAPSHOTS_NOT_EXISTS);
        }
    }

    @Override
    public ReviewSnapshotsDO getReviewSnapshots(Integer id) {
        return reviewSnapshotsMapper.selectById(id);
    }

    @Override
    public PageResult<ReviewSnapshotsDO> getReviewSnapshotsPage(ReviewSnapshotsPageReqVO pageReqVO) {
        return reviewSnapshotsMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer batchSyncReviewSnapshots(ReviewSnapshotsBatchSyncReqVO batchSyncReqVO) {
        if (CollUtil.isEmpty(batchSyncReqVO.getReviews())) {
            return 0;
        }
        
        int successCount = 0;
        // 获取当前美国太平洋时间的日期
        ZonedDateTime nowPacific = ZonedDateTime.now(ZoneId.of("America/Los_Angeles"));
        LocalDate snapshotDate = nowPacific.toLocalDate();
        
        for (ReviewSnapshotsBatchSyncReqVO.ReviewSnapshotItem item : batchSyncReqVO.getReviews()) {
            
            // 查询该ASIN在同一天是否已存在记录
            LambdaQueryWrapper<ReviewSnapshotsDO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ReviewSnapshotsDO::getAsin, item.getAsin())
                    .eq(ReviewSnapshotsDO::getSnapshotDate, snapshotDate);
            ReviewSnapshotsDO existingSnapshot = reviewSnapshotsMapper.selectOne(queryWrapper);
            
            // 构建新的快照对象
            ReviewSnapshotsDO snapshot = new ReviewSnapshotsDO();
            snapshot.setAsin(item.getAsin());
            snapshot.setSpu(item.getSpu());
            snapshot.setSnapshotDate(snapshotDate);
            snapshot.setRating(item.getRating());
            snapshot.setRatingsCount(item.getRatingsCount());
            snapshot.setReviewsCount(item.getReviewsCount());
            
            // 直接设置Map对象，JsonbTypeHandler会自动处理JSONB转换
            snapshot.setStarDistribution(item.getStarDistribution());
            snapshot.setReviewStarCounts(item.getReviewStarCounts());
            snapshot.setReviewStarPercentages(item.getReviewStarPercentages());
            
            // 设置用户和租户信息
            Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
            String loginUserName = SecurityFrameworkUtils.getLoginUserNickname();
            Long tenantId = 1L;
            LocalDateTime now = LocalDateTime.now();
            
            if (existingSnapshot != null) {
                // 如果已存在，更新记录
                snapshot.setId(existingSnapshot.getId());
                snapshot.setUserId(existingSnapshot.getUserId());
                snapshot.setUpdateTime(now);
                snapshot.setUpdater(loginUserName);
                reviewSnapshotsMapper.updateReviewSnapshot(snapshot);
            } else {
                // 如果不存在，插入新记录
                snapshot.setUserId(loginUserId != null ? loginUserId.intValue() : null);
                snapshot.setCreateTime(now);
                snapshot.setUpdateTime(now);
                snapshot.setCreator(loginUserName);
                snapshot.setUpdater(loginUserName);
                snapshot.setTenantId(1L);
                reviewSnapshotsMapper.insertReviewSnapshot(snapshot);
            }
            successCount++;
        }
        
        return successCount;
    }

}