package cn.iocoder.yudao.module.amazon.service.pricerecords;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.pricerecords.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.pricerecords.PriceRecordsDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 亚马逊产品价格记录 Service 接口
 *
 * @author Demons
 */
public interface PriceRecordsService {

    /**
     * 创建亚马逊产品价格记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPriceRecords(@Valid PriceRecordsSaveReqVO createReqVO);

    /**
     * 更新亚马逊产品价格记录
     *
     * @param updateReqVO 更新信息
     */
    void updatePriceRecords(@Valid PriceRecordsSaveReqVO updateReqVO);

    /**
     * 删除亚马逊产品价格记录
     *
     * @param id 编号
     */
    void deletePriceRecords(Long id);

    /**
    * 批量删除亚马逊产品价格记录
    *
    * @param ids 编号
    */
    void deletePriceRecordsListByIds(List<Long> ids);

    /**
     * 获得亚马逊产品价格记录
     *
     * @param id 编号
     * @return 亚马逊产品价格记录
     */
    PriceRecordsDO getPriceRecords(Long id);

    /**
     * 获得亚马逊产品价格记录分页
     *
     * @param pageReqVO 分页查询
     * @return 亚马逊产品价格记录分页
     */
    PageResult<PriceRecordsDO> getPriceRecordsPage(PriceRecordsPageReqVO pageReqVO);

    /**
     * 批量保存价格记录
     * 根据爬取日期+ASIN判断是否重复，重复则更新，不重复则新增
     * 
     * @param batchSaveReqVO 批量保存请求
     * @return 成功处理的记录数
     */
    Integer batchSavePriceRecords(@Valid PriceRecordsBatchSaveReqVO batchSaveReqVO);

}