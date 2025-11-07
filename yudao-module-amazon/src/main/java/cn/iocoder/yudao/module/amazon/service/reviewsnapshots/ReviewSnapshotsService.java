package cn.iocoder.yudao.module.amazon.service.reviewsnapshots;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.reviewsnapshots.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.reviewsnapshots.ReviewSnapshotsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 亚马逊评论快照 Service 接口
 *
 * @author Demons
 */
public interface ReviewSnapshotsService {

    /**
     * 创建亚马逊评论快照
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createReviewSnapshots(@Valid ReviewSnapshotsSaveReqVO createReqVO);

    /**
     * 更新亚马逊评论快照
     *
     * @param updateReqVO 更新信息
     */
    void updateReviewSnapshots(@Valid ReviewSnapshotsSaveReqVO updateReqVO);

    /**
     * 删除亚马逊评论快照
     *
     * @param id 编号
     */
    void deleteReviewSnapshots(Integer id);

    /**
    * 批量删除亚马逊评论快照
    *
    * @param ids 编号
    */
    void deleteReviewSnapshotsListByIds(List<Integer> ids);

    /**
     * 获得亚马逊评论快照
     *
     * @param id 编号
     * @return 亚马逊评论快照
     */
    ReviewSnapshotsDO getReviewSnapshots(Integer id);

    /**
     * 获得亚马逊评论快照分页
     *
     * @param pageReqVO 分页查询
     * @return 亚马逊评论快照分页
     */
    PageResult<ReviewSnapshotsDO> getReviewSnapshotsPage(ReviewSnapshotsPageReqVO pageReqVO);

    /**
     * 批量同步评论快照
     * 同一天同一ASIN只保留最新数据
     *
     * @param batchSyncReqVO 批量同步请求
     * @return 成功保存的数量
     */
    Integer batchSyncReviewSnapshots(@Valid ReviewSnapshotsBatchSyncReqVO batchSyncReqVO);

}