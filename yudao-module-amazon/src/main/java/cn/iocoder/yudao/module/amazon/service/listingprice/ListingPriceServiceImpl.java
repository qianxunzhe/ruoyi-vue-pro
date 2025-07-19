package cn.iocoder.yudao.module.amazon.service.listingprice;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.amazon.controller.admin.listingprice.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.listingprice.ListingPriceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.common.exception.ServiceException;

import cn.iocoder.yudao.module.amazon.dal.mysql.listingprice.ListingPriceMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;

/**
 * 价格监控结果 Service 实现类
 *
 * @author Demons
 */
@Service
@Validated
public class ListingPriceServiceImpl implements ListingPriceService {

    @Resource
    private ListingPriceMapper listingPriceMapper;

    @Override
    public Long createListingPrice(ListingPriceSaveReqVO createReqVO) {
        // 插入
        ListingPriceDO listingPrice = BeanUtils.toBean(createReqVO, ListingPriceDO.class);
        listingPriceMapper.insert(listingPrice);

        // 返回
        return listingPrice.getId();
    }

    @Override
    public Boolean createListingPriceBatch(List<ListingPriceDO> createReqDOList) {
        // 插入
        Boolean result = listingPriceMapper.insertBatch(createReqDOList);

        // 返回
        return result;
    }

    @Override
    public void updateListingPrice(ListingPriceSaveReqVO updateReqVO) {
        // 校验存在
        validateListingPriceExists(updateReqVO.getId());
        // 更新
        ListingPriceDO updateObj = BeanUtils.toBean(updateReqVO, ListingPriceDO.class);
        listingPriceMapper.updateById(updateObj);
    }

    @Override
    public void deleteListingPrice(Long id) {
        // 校验存在
        validateListingPriceExists(id);
        // 删除
        listingPriceMapper.deleteById(id);
    }

    @Override
        public void deleteListingPriceListByIds(List<Long> ids) {
        // 删除
        listingPriceMapper.deleteByIds(ids);
        }


    private void validateListingPriceExists(Long id) {
        if (listingPriceMapper.selectById(id) == null) {
            throw exception(LISTING_PRICE_NOT_EXISTS);
        }
    }

    @Override
    public ListingPriceDO getListingPrice(Long id) {
        return listingPriceMapper.selectById(id);
    }

    @Override
    public PageResult<ListingPriceDO> getListingPricePage(ListingPricePageReqVO pageReqVO) {
        return listingPriceMapper.selectPage(pageReqVO);
    }

    @Override
    public PriceAnalysisRespVO getPriceAnalysis(PriceAnalysisReqVO reqVO) {
        // 解析日期范围
        LocalDate startDate = LocalDate.parse(reqVO.getStartDate());
        LocalDate endDate = LocalDate.parse(reqVO.getEndDate());

        // 验证日期范围
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("开始日期不能晚于结束日期");
        }

        // 查询价格数据
        List<ListingPriceDO> priceList = listingPriceMapper.selectPriceAnalysisData(
            reqVO.getTaskId(), startDate, endDate);

        // 构建分析数据
        return buildPriceAnalysisData(priceList, startDate, endDate, "price");
    }

    private PriceAnalysisRespVO buildPriceAnalysisData(List<ListingPriceDO> priceList, 
                                                      LocalDate startDate, 
                                                      LocalDate endDate, 
                                                      String priceType) {
        PriceAnalysisRespVO respVO = new PriceAnalysisRespVO();

        // 生成日期列表
        List<String> dateList = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateList.add(current.toString());
            current = current.plusDays(1);
        }

        // 获取所有ASIN
        Set<String> asinSet = priceList.stream()
            .map(ListingPriceDO::getAsin)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        List<String> asinList = new ArrayList<>(asinSet);
        Collections.sort(asinList);

        // 构建分析数据映射
        Map<String, Map<String, BigDecimal>> analysisData = new HashMap<>();
        List<BigDecimal> allPrices = new ArrayList<>();

        for (String asin : asinList) {
            Map<String, BigDecimal> asinData = new HashMap<>();

            for (String date : dateList) {
                BigDecimal price = findPriceByAsinAndDate(priceList, asin, date, priceType);
                asinData.put(date, price);
                if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                    allPrices.add(price);
                }
            }

            analysisData.put(asin, asinData);
        }

        // 计算平均价格
        BigDecimal averagePrice = allPrices.isEmpty() ? BigDecimal.ZERO :
            allPrices.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(allPrices.size()), 2, RoundingMode.HALF_UP);

        respVO.setAnalysisData(analysisData);
        respVO.setAsinList(asinList);
        respVO.setDateList(dateList);
        respVO.setAveragePrice(averagePrice);

        return respVO;
    }

    private BigDecimal findPriceByAsinAndDate(List<ListingPriceDO> priceList, 
                                            String asin, 
                                            String date, 
                                            String priceType) {
        return priceList.stream()
            .filter(price -> asin.equals(price.getAsin()) &&
                           date.equals(price.getScrapedAt().toLocalDate().toString()))
            .findFirst()
            .map(price -> getPriceByType(price, priceType))
            .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getPriceByType(ListingPriceDO price, String priceType) {
        switch (priceType) {
            case "buyboxPrice":
                return price.getBuyboxPrice();
            case "price":
                return price.getPrice();
            case "primePrice":
                return price.getPrimePrice();
            case "listPrice":
                return price.getListPrice();
            case "couponPrice":
                return price.getCouponPrice();
            case "dealPrice":
                return price.getDealPrice();
            case "fbaPrice":
                return price.getFbaPrice();
            case "fbmPrice":
                return price.getFbmPrice();
            default:
                return price.getBuyboxPrice();
        }
    }

}