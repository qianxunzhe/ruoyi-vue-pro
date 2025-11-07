package cn.iocoder.yudao.module.amazon.dal.mysql.productperformance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo.ProductPerformancePageReqVO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.productperformance.ProductPerformanceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 亚马逊产品表现 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface ProductPerformanceMapper extends BaseMapperX<ProductPerformanceDO> {

    default PageResult<ProductPerformanceDO> selectPage(ProductPerformancePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProductPerformanceDO>()
                .eqIfPresent(ProductPerformanceDO::getAsin, reqVO.getAsin())
                .eqIfPresent(ProductPerformanceDO::getParentAsin, reqVO.getParentAsin())
                .eqIfPresent(ProductPerformanceDO::getMsku, reqVO.getMsku())
                .eqIfPresent(ProductPerformanceDO::getSku, reqVO.getSku())
                .eqIfPresent(ProductPerformanceDO::getSpu, reqVO.getSpu())
                .eqIfPresent(ProductPerformanceDO::getSid, reqVO.getSid())
                .inIfPresent(ProductPerformanceDO::getSid, reqVO.getSids())
                .eqIfPresent(ProductPerformanceDO::getMid, reqVO.getMid())
                .eqIfPresent(ProductPerformanceDO::getSummaryField, reqVO.getSummaryField())
                .geIfPresent(ProductPerformanceDO::getStartDate, reqVO.getStartDate())
                .leIfPresent(ProductPerformanceDO::getEndDate, reqVO.getEndDate())
                .likeIfPresent(ProductPerformanceDO::getItemName, reqVO.getItemName())
                .orderByDesc(ProductPerformanceDO::getRecordDate)  // 先按日期降序
                .orderByDesc(ProductPerformanceDO::getVolume));     // 再按销量降序
    }

    /**
     * 批量插入或更新
     * @param list 数据列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<ProductPerformanceDO> list);
    
    /**
     * 批量插入（用于先删除后插入的方式）
     * @param list 数据列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<ProductPerformanceDO> list);

    /**
     * 根据唯一键查询
     * @param asin ASIN
     * @param sid 店铺ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param summaryField 汇总维度
     * @return 产品表现数据
     */
    default ProductPerformanceDO selectByUniqueKey(String asin, Long sid, LocalDate startDate, 
                                                   LocalDate endDate, String summaryField) {
        return selectOne(new LambdaQueryWrapperX<ProductPerformanceDO>()
                .eq(ProductPerformanceDO::getAsin, asin)
                .eq(ProductPerformanceDO::getSid, sid)
                .eq(ProductPerformanceDO::getStartDate, startDate)
                .eq(ProductPerformanceDO::getEndDate, endDate)
                .eq(ProductPerformanceDO::getSummaryField, summaryField));
    }

    /**
     * 根据ASIN列表和店铺ID列表查询
     * @param asins ASIN列表
     * @param sids 店铺ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 产品表现数据列表
     */
    default List<ProductPerformanceDO> selectByAsinsAndSids(List<String> asins, List<Long> sids,
                                                           LocalDate startDate, LocalDate endDate) {
        return selectList(new LambdaQueryWrapperX<ProductPerformanceDO>()
                .in(ProductPerformanceDO::getAsin, asins)
                .in(ProductPerformanceDO::getSid, sids)
                .eq(ProductPerformanceDO::getStartDate, startDate)
                .eq(ProductPerformanceDO::getEndDate, endDate));
    }

    /**
     * 删除指定日期范围的数据
     * @param sids 店铺ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 删除行数
     */
    default int deleteByDateRange(List<Long> sids, LocalDate startDate, LocalDate endDate) {
        return delete(new LambdaQueryWrapperX<ProductPerformanceDO>()
                .in(ProductPerformanceDO::getSid, sids)
                .eq(ProductPerformanceDO::getStartDate, startDate)
                .eq(ProductPerformanceDO::getEndDate, endDate));
    }
    
    /**
     * 物理删除指定条件的数据（绕过逻辑删除）
     * @param asin ASIN
     * @param sid 店铺ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param summaryField 汇总字段
     * @return 删除行数
     */
    int physicalDelete(@Param("asin") String asin, 
                      @Param("sid") Long sid,
                      @Param("startDate") LocalDate startDate,
                      @Param("endDate") LocalDate endDate,
                      @Param("summaryField") String summaryField);

    /**
     * 按SPU分组查询分页数据
     * @param reqVO 分页查询条件
     * @return 按SPU聚合后的数据列表
     */
    List<ProductPerformanceDO> selectPageGroupBySpu(@Param("reqVO") ProductPerformancePageReqVO reqVO);
    
    /**
     * 按SPU分组查询总数
     * @param reqVO 查询条件
     * @return 总记录数
     */
    Long selectCountGroupBySpu(@Param("reqVO") ProductPerformancePageReqVO reqVO);
}