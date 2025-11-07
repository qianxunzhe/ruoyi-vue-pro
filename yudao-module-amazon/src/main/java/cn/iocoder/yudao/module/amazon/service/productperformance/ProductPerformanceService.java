package cn.iocoder.yudao.module.amazon.service.productperformance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo.ProductPerformancePageReqVO;
import cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo.ProductPerformanceSyncReqVO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.productperformance.ProductPerformanceDO;

import java.time.LocalDate;
import java.util.List;

/**
 * 亚马逊产品表现 Service 接口
 *
 * @author 芋道源码
 */
public interface ProductPerformanceService {

    /**
     * 获取产品表现分页
     *
     * @param pageReqVO 分页查询
     * @return 产品表现分页
     */
    PageResult<ProductPerformanceDO> getProductPerformancePage(ProductPerformancePageReqVO pageReqVO);

    /**
     * 获取产品表现
     *
     * @param id 编号
     * @return 产品表现
     */
    ProductPerformanceDO getProductPerformance(Long id);

    /**
     * 同步产品表现数据
     *
     * @param syncReqVO 同步请求参数
     * @return 同步结果
     */
    String syncProductPerformance(ProductPerformanceSyncReqVO syncReqVO);

    /**
     * 批量保存或更新产品表现数据
     *
     * @param list 产品表现数据列表
     * @return 影响行数
     */
    int saveOrUpdateBatch(List<ProductPerformanceDO> list);

    /**
     * 根据ASIN、店铺ID和日期范围查询
     *
     * @param asins ASIN列表
     * @param sids 店铺ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 产品表现数据列表
     */
    List<ProductPerformanceDO> getByAsinsAndSids(List<String> asins, List<Long> sids,
                                                 LocalDate startDate, LocalDate endDate);

    /**
     * 删除指定日期范围的数据
     *
     * @param sids 店铺ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 删除行数
     */
    int deleteByDateRange(List<Long> sids, LocalDate startDate, LocalDate endDate);
}