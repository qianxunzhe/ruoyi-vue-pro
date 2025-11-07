package cn.iocoder.yudao.module.amazon.dal.mysql.pricerecords;

import java.util.*;
import java.time.LocalDate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.amazon.dal.dataobject.pricerecords.PriceRecordsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import cn.iocoder.yudao.module.amazon.controller.admin.pricerecords.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 亚马逊产品价格记录 Mapper
 *
 * @author Demons
 */
@Mapper
public interface PriceRecordsMapper extends BaseMapperX<PriceRecordsDO> {

    default PageResult<PriceRecordsDO> selectPage(PriceRecordsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PriceRecordsDO>()
                .eqIfPresent(PriceRecordsDO::getAsin, reqVO.getAsin())
                .eqIfPresent(PriceRecordsDO::getSpu, reqVO.getSpu())
                .eqIfPresent(PriceRecordsDO::getPrice, reqVO.getPrice())
                .eqIfPresent(PriceRecordsDO::getPrimePrice, reqVO.getPrimePrice())
                .eqIfPresent(PriceRecordsDO::getCouponDiscount, reqVO.getCouponDiscount())
                .eqIfPresent(PriceRecordsDO::getCouponPrice, reqVO.getCouponPrice())
                .eqIfPresent(PriceRecordsDO::getAvailability, reqVO.getAvailability())
                .eqIfPresent(PriceRecordsDO::getFrequentlyReturned, reqVO.getFrequentlyReturned())
                .betweenIfPresent(PriceRecordsDO::getScrapeDate, reqVO.getScrapeDate())
                .betweenIfPresent(PriceRecordsDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PriceRecordsDO::getId));
    }

    /**
     * 根据ASIN和爬取日期查询价格记录
     *
     * @param asin 产品识别码
     * @param scrapeDate 爬取日期
     * @param userId 用户ID
     * @return 价格记录
     */
    default PriceRecordsDO selectByAsinAndScrapeDate(@Param("asin") String asin,
                                                      @Param("scrapeDate") LocalDate scrapeDate,
                                                      @Param("userId") Long userId) {
        return selectOne(new LambdaQueryWrapper<PriceRecordsDO>()
                .eq(PriceRecordsDO::getAsin, asin)
                .eq(PriceRecordsDO::getScrapeDate, scrapeDate)
                .eq(PriceRecordsDO::getUserId, userId));
    }

    /**
     * 批量查询价格记录，用于批量保存时检查已存在的记录
     *
     * @param asinScrapeDatePairs ASIN和爬取日期的组合列表
     * @param userId 用户ID
     * @return 价格记录列表
     */
    default List<PriceRecordsDO> selectByAsinAndScrapeDateBatch(
            @Param("pairs") List<Map<String, Object>> asinScrapeDatePairs,
            @Param("userId") Long userId) {
        if (asinScrapeDatePairs == null || asinScrapeDatePairs.isEmpty()) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<PriceRecordsDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PriceRecordsDO::getUserId, userId);
        
        wrapper.and(w -> {
            for (Map<String, Object> pair : asinScrapeDatePairs) {
                w.or(orWrapper -> orWrapper
                        .eq(PriceRecordsDO::getAsin, pair.get("asin"))
                        .eq(PriceRecordsDO::getScrapeDate, pair.get("scrapeDate")));
            }
        });
        
        return selectList(wrapper);
    }

}