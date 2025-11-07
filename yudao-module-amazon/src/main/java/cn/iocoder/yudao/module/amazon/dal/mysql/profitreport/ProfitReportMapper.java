package cn.iocoder.yudao.module.amazon.dal.mysql.profitreport;

import java.util.*;
import java.time.LocalDate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.profitreport.ProfitReportDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo.*;

/**
 * 亚马逊利润报表数据 Mapper
 *
 * @author Demons
 */
@Mapper
public interface ProfitReportMapper extends BaseMapperX<ProfitReportDO> {
    
    /**
     * 批量插入
     * @param list 数据列表
     * @return 是否成功
     */
    default Boolean insertBatchData(List<ProfitReportDO> list) {
        return insertBatch(list, 1000);
    }
    

    default PageResult<ProfitReportDO> selectPage(ProfitReportPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ProfitReportDO>()
                .betweenIfPresent(ProfitReportDO::getSyncDate, reqVO.getSyncDate())
                .eqIfPresent(ProfitReportDO::getAsin, reqVO.getAsin())
                .eqIfPresent(ProfitReportDO::getParentAsin, reqVO.getParentAsin())
                .eqIfPresent(ProfitReportDO::getLocalSku, reqVO.getLocalSku())
                .eqIfPresent(ProfitReportDO::getSpu, reqVO.getSpu())
                .eqIfPresent(ProfitReportDO::getSid, reqVO.getSid())
                .likeIfPresent(ProfitReportDO::getStoreName, reqVO.getStoreName())
                .eqIfPresent(ProfitReportDO::getCountryCode, reqVO.getCountryCode())
                .eqIfPresent(ProfitReportDO::getTotalSalesQuantity, reqVO.getTotalSalesQuantity())
                .eqIfPresent(ProfitReportDO::getFbaSalesQuantity, reqVO.getFbaSalesQuantity())
                .eqIfPresent(ProfitReportDO::getFbmSalesQuantity, reqVO.getFbmSalesQuantity())
                .eqIfPresent(ProfitReportDO::getRefundsQuantity, reqVO.getRefundsQuantity())
                .eqIfPresent(ProfitReportDO::getFbaReturnsQuantity, reqVO.getFbaReturnsQuantity())
                .eqIfPresent(ProfitReportDO::getTotalSalesAmount, reqVO.getTotalSalesAmount())
                .eqIfPresent(ProfitReportDO::getFbaSaleAmount, reqVO.getFbaSaleAmount())
                .eqIfPresent(ProfitReportDO::getFbmSaleAmount, reqVO.getFbmSaleAmount())
                .eqIfPresent(ProfitReportDO::getTotalSalesRefunds, reqVO.getTotalSalesRefunds())
                .eqIfPresent(ProfitReportDO::getTotalAdsSales, reqVO.getTotalAdsSales())
                .eqIfPresent(ProfitReportDO::getTotalAdsCost, reqVO.getTotalAdsCost())
                .eqIfPresent(ProfitReportDO::getAdsSpSales, reqVO.getAdsSpSales())
                .eqIfPresent(ProfitReportDO::getAdsSpCost, reqVO.getAdsSpCost())
                .eqIfPresent(ProfitReportDO::getAdsSdSales, reqVO.getAdsSdSales())
                .eqIfPresent(ProfitReportDO::getAdsSdCost, reqVO.getAdsSdCost())
                .eqIfPresent(ProfitReportDO::getAdsSbSales, reqVO.getAdsSbSales())
                .eqIfPresent(ProfitReportDO::getAdsSbCost, reqVO.getAdsSbCost())
                .eqIfPresent(ProfitReportDO::getPlatformFee, reqVO.getPlatformFee())
                .eqIfPresent(ProfitReportDO::getTotalFbaDeliveryFee, reqVO.getTotalFbaDeliveryFee())
                .eqIfPresent(ProfitReportDO::getTotalStorageFee, reqVO.getTotalStorageFee())
                .eqIfPresent(ProfitReportDO::getPromotionFee, reqVO.getPromotionFee())
                .eqIfPresent(ProfitReportDO::getCgPriceTotal, reqVO.getCgPriceTotal())
                .eqIfPresent(ProfitReportDO::getCgTransportCostsTotal, reqVO.getCgTransportCostsTotal())
                .eqIfPresent(ProfitReportDO::getCgOtherCostsTotal, reqVO.getCgOtherCostsTotal())
                .eqIfPresent(ProfitReportDO::getTotalCost, reqVO.getTotalCost())
                .eqIfPresent(ProfitReportDO::getGrossProfit, reqVO.getGrossProfit())
                .eqIfPresent(ProfitReportDO::getGrossRate, reqVO.getGrossRate())
                .eqIfPresent(ProfitReportDO::getCurrencyCode, reqVO.getCurrencyCode())
                .eqIfPresent(ProfitReportDO::getTransactionStatus, reqVO.getTransactionStatus())
                .likeIfPresent(ProfitReportDO::getLocalName, reqVO.getLocalName())
                .eqIfPresent(ProfitReportDO::getOtherFeeStr, reqVO.getOtherFeeStr())
                .betweenIfPresent(ProfitReportDO::getSyncTime, reqVO.getSyncTime())
                .betweenIfPresent(ProfitReportDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ProfitReportDO::getId));
    }
    
    /**
     * 根据查询条件获取报表数据（用于聚合）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param sids 店铺ID列表
     * @param asins ASIN列表
     * @param skus SKU列表
     * @return 报表数据列表
     */
    default List<ProfitReportDO> selectListForAggregation(LocalDate startDate, LocalDate endDate,
                                                         List<String> sids, List<String> asins, List<String> skus) {
        return selectList(new LambdaQueryWrapperX<ProfitReportDO>()
                .betweenIfPresent(ProfitReportDO::getSyncDate, new LocalDate[]{startDate, endDate})
                .inIfPresent(ProfitReportDO::getSid, sids)
                .inIfPresent(ProfitReportDO::getAsin, asins)
                .inIfPresent(ProfitReportDO::getLocalSku, skus));
    }
    
    /**
     * 删除指定日期和店铺的数据
     * @param syncDate 同步日期
     * @param sids 店铺ID列表
     * @return 删除的记录数
     */
    default int deleteByDateAndSids(LocalDate syncDate, List<String> sids) {
        return delete(new LambdaQueryWrapperX<ProfitReportDO>()
                .eqIfPresent(ProfitReportDO::getSyncDate, syncDate)
                .inIfPresent(ProfitReportDO::getSid, sids));
    }
    
    /**
     * 查询指定日期已存在的ASIN列表
     * @param syncDate 同步日期
     * @param asins ASIN列表
     * @return 已存在的ASIN列表
     */
    default List<String> selectExistingAsins(LocalDate syncDate, List<String> asins) {
        return selectObjs(new LambdaQueryWrapperX<ProfitReportDO>()
                .eq(ProfitReportDO::getSyncDate, syncDate)
                .in(ProfitReportDO::getAsin, asins)
                .select(ProfitReportDO::getAsin));
    }

}