package cn.iocoder.yudao.module.amazon.service.listingprice;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.listingprice.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.listingprice.ListingPriceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 价格监控结果 Service 接口
 *
 * @author Demons
 */
public interface ListingPriceService {

    /**
     * 创建价格监控结果
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createListingPrice(@Valid ListingPriceSaveReqVO createReqVO);


    /**
     * 批量创建
     * @param createReqDOList
     * @return
     */
    public Boolean createListingPriceBatch(List<ListingPriceDO> createReqDOList);

    /**
     * 更新价格监控结果
     *
     * @param updateReqVO 更新信息
     */
    void updateListingPrice(@Valid ListingPriceSaveReqVO updateReqVO);

    /**
     * 删除价格监控结果
     *
     * @param id 编号
     */
    void deleteListingPrice(Long id);

    /**
    * 批量删除价格监控结果
    *
    * @param ids 编号
    */
    void deleteListingPriceListByIds(List<Long> ids);

    /**
     * 获得价格监控结果
     *
     * @param id 编号
     * @return 价格监控结果
     */
    ListingPriceDO getListingPrice(Long id);

    /**
     * 获得价格监控结果分页
     *
     * @param pageReqVO 分页查询
     * @return 价格监控结果分页
     */
    PageResult<ListingPriceDO> getListingPricePage(ListingPricePageReqVO pageReqVO);

    /**
     * 获取价格分析数据
     *
     * @param reqVO 价格分析请求
     * @return 价格分析结果
     */
    PriceAnalysisRespVO getPriceAnalysis(@Valid PriceAnalysisReqVO reqVO);

}