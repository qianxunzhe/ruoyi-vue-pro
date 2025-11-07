package cn.iocoder.yudao.module.amazon.dal.mysql.reviewsnapshots;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.reviewsnapshots.ReviewSnapshotsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.amazon.controller.admin.reviewsnapshots.vo.*;

/**
 * 亚马逊评论快照 Mapper
 *
 * @author Demons
 */
@Mapper
public interface ReviewSnapshotsMapper extends BaseMapperX<ReviewSnapshotsDO> {

    @Select("SELECT * FROM amazon_review_snapshots WHERE deleted = 0 AND id = #{id}")
    @ResultMap("reviewSnapshotsResultMap")
    ReviewSnapshotsDO selectByIdWithJsonb(@Param("id") Integer id);

    default PageResult<ReviewSnapshotsDO> selectPage(ReviewSnapshotsPageReqVO reqVO) {
        // 先用原始方法查询，获取分页结果
        PageResult<ReviewSnapshotsDO> pageResult = selectPage(reqVO, 
                new LambdaQueryWrapperX<ReviewSnapshotsDO>()
                .eqIfPresent(ReviewSnapshotsDO::getAsin, reqVO.getAsin())
                .betweenIfPresent(ReviewSnapshotsDO::getSnapshotDate, reqVO.getSnapshotDate())
                .eqIfPresent(ReviewSnapshotsDO::getSpu, reqVO.getSpu())
                .eqIfPresent(ReviewSnapshotsDO::getRating, reqVO.getRating())
                .eqIfPresent(ReviewSnapshotsDO::getRatingsCount, reqVO.getRatingsCount())
                .eqIfPresent(ReviewSnapshotsDO::getReviewsCount, reqVO.getReviewsCount())
                .eqIfPresent(ReviewSnapshotsDO::getUserId, reqVO.getUserId())
                .betweenIfPresent(ReviewSnapshotsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ReviewSnapshotsDO::getId));
        
        // 对于每条记录，使用带有正确 ResultMap 的方法重新查询，确保 JSONB 字段正确映射
        List<ReviewSnapshotsDO> records = pageResult.getList();
        if (records != null && !records.isEmpty()) {
            List<ReviewSnapshotsDO> updatedRecords = new ArrayList<>();
            for (ReviewSnapshotsDO record : records) {
                ReviewSnapshotsDO fullRecord = selectByIdWithJsonb(record.getId());
                if (fullRecord != null) {
                    updatedRecords.add(fullRecord);
                }
            }
            return new PageResult<>(updatedRecords, pageResult.getTotal());
        }
        
        return pageResult;
    }

    @Insert("INSERT INTO amazon_review_snapshots (asin, snapshot_date, spu, rating, ratings_count, reviews_count, " +
            "star_distribution, review_star_counts, review_star_percentages, user_id, " +
            "create_time, update_time, creator, updater, tenant_id) " +
            "VALUES (#{asin}, #{snapshotDate}, #{spu}, #{rating}, #{ratingsCount}, #{reviewsCount}, " +
            "#{starDistribution, typeHandler=cn.iocoder.yudao.module.amazon.framework.mybatis.type.JsonbTypeHandler}, " +
            "#{reviewStarCounts, typeHandler=cn.iocoder.yudao.module.amazon.framework.mybatis.type.JsonbTypeHandler}, " +
            "#{reviewStarPercentages, typeHandler=cn.iocoder.yudao.module.amazon.framework.mybatis.type.JsonbTypeHandler}, " +
            "#{userId}, #{createTime}, #{updateTime}, #{creator}, #{updater}, #{tenantId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertReviewSnapshot(ReviewSnapshotsDO snapshot);
    
    @Update("UPDATE amazon_review_snapshots SET " +
            "asin = #{asin}, snapshot_date = #{snapshotDate}, spu = #{spu}, " +
            "rating = #{rating}, ratings_count = #{ratingsCount}, reviews_count = #{reviewsCount}, " +
            "star_distribution = #{starDistribution, typeHandler=cn.iocoder.yudao.module.amazon.framework.mybatis.type.JsonbTypeHandler}, " +
            "review_star_counts = #{reviewStarCounts, typeHandler=cn.iocoder.yudao.module.amazon.framework.mybatis.type.JsonbTypeHandler}, " +
            "review_star_percentages = #{reviewStarPercentages, typeHandler=cn.iocoder.yudao.module.amazon.framework.mybatis.type.JsonbTypeHandler}, " +
            "user_id = #{userId}, update_time = #{updateTime}, updater = #{updater} " +
            "WHERE id = #{id}")
    int updateReviewSnapshot(ReviewSnapshotsDO snapshot);

}