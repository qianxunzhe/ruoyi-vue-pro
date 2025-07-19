package cn.iocoder.yudao.module.amazon.dal.mysql.listingprice;

import java.util.*;
import java.time.LocalDate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.listingprice.ListingPriceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.amazon.controller.admin.listingprice.vo.*;

/**
 * 价格监控结果 Mapper
 *
 * @author Demons
 */
@Mapper
public interface ListingPriceMapper extends BaseMapperX<ListingPriceDO> {

    default PageResult<ListingPriceDO> selectPage(ListingPricePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ListingPriceDO>()
                .eqIfPresent(ListingPriceDO::getUserId, reqVO.getUserId())
                .eqIfPresent(ListingPriceDO::getTaskId, reqVO.getTaskId())
                .eqIfPresent(ListingPriceDO::getTaskExecLogId, reqVO.getTaskExecLogId())
                .eqIfPresent(ListingPriceDO::getAsin, reqVO.getAsin())
                .eqIfPresent(ListingPriceDO::getMarketplace, reqVO.getMarketplace())
                .eqIfPresent(ListingPriceDO::getBuyboxPrice, reqVO.getBuyboxPrice())
                .eqIfPresent(ListingPriceDO::getPrice, reqVO.getPrice())
                .eqIfPresent(ListingPriceDO::getPrimePrice, reqVO.getPrimePrice())
                .eqIfPresent(ListingPriceDO::getCouponPrice, reqVO.getCouponPrice())
                .eqIfPresent(ListingPriceDO::getCouponDiscount, reqVO.getCouponDiscount())
                .eqIfPresent(ListingPriceDO::getDealPrice, reqVO.getDealPrice())
                .eqIfPresent(ListingPriceDO::getDealInfo, reqVO.getDealInfo())
                .eqIfPresent(ListingPriceDO::getFbaPrice, reqVO.getFbaPrice())
                .eqIfPresent(ListingPriceDO::getFbmPrice, reqVO.getFbmPrice())
                .eqIfPresent(ListingPriceDO::getListPrice, reqVO.getListPrice())
                .eqIfPresent(ListingPriceDO::getScrapedAt, reqVO.getScrapedAt())
                .betweenIfPresent(ListingPriceDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(ListingPriceDO::getRemark, reqVO.getRemark())
                .orderByDesc(ListingPriceDO::getId));
    }

    /**
     * 查询价格分析数据
     */
    @Select("""
        SELECT * FROM amazon_listing_price 
        WHERE task_id = #{taskId}
        AND DATE(scraped_at) BETWEEN #{startDate} AND #{endDate}
        AND asin IS NOT NULL 
        AND deleted = 0
        ORDER BY asin, scraped_at
        """)
    List<ListingPriceDO> selectPriceAnalysisData(@Param("taskId") Long taskId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

}