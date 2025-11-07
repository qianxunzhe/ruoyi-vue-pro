package cn.iocoder.yudao.module.amazon.service.profitreport;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.profitreport.ProfitReportDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 亚马逊利润报表数据 Service 接口
 *
 * @author Demons
 */
public interface ProfitReportService {

    /**
     * 创建亚马逊利润报表数据
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProfitReport(@Valid ProfitReportSaveReqVO createReqVO);

    /**
     * 更新亚马逊利润报表数据
     *
     * @param updateReqVO 更新信息
     */
    void updateProfitReport(@Valid ProfitReportSaveReqVO updateReqVO);

    /**
     * 删除亚马逊利润报表数据
     *
     * @param id 编号
     */
    void deleteProfitReport(Long id);

    /**
    * 批量删除亚马逊利润报表数据
    *
    * @param ids 编号
    */
    void deleteProfitReportListByIds(List<Long> ids);

    /**
     * 获得亚马逊利润报表数据
     *
     * @param id 编号
     * @return 亚马逊利润报表数据
     */
    ProfitReportDO getProfitReport(Long id);

    /**
     * 获得亚马逊利润报表数据分页
     *
     * @param pageReqVO 分页查询
     * @return 亚马逊利润报表数据分页
     */
    PageResult<ProfitReportDO> getProfitReportPage(ProfitReportPageReqVO pageReqVO);
    
    /**
     * 同步利润报表数据
     *
     * @param syncReqVO 同步请求参数
     * @return 同步成功的记录数
     */
    Integer syncProfitReport(ProfitReportSyncReqVO syncReqVO);
    
    /**
     * SPU维度聚合查询（考虑不同店铺）
     *
     * @param queryVO 查询参数
     * @return SPU维度聚合结果
     */
    PageResult<ProfitReportSpuAggVO> aggregateBySpu(ProfitReportQueryVO queryVO);
    
    /**
     * SPU+SID维度聚合查询
     * 按SPU和店铺ID两个维度聚合，同一个SPU在不同店铺分开统计
     *
     * @param queryVO 查询参数
     * @return SPU+SID维度聚合结果
     */
    PageResult<ProfitReportSpuSidAggVO> aggregateBySpuAndSid(ProfitReportQueryVO queryVO);
    
    /**
     * 店铺（SID）维度聚合查询
     *
     * @param queryVO 查询参数
     * @return 店铺维度聚合结果
     */
    PageResult<ProfitReportShopAggVO> aggregateBySid(ProfitReportQueryVO queryVO);
    
    /**
     * 统一聚合查询接口
     * 根据queryVO中的groupBy参数选择聚合维度（只支持SPU、SPU_SID、SID）
     *
     * @param queryVO 查询参数（包含groupBy字段）
     * @return 聚合结果（具体类型根据groupBy确定）
     */
    Object aggregate(ProfitReportQueryVO queryVO);
    
    /**
     * 将聚合数据转换为管理利润表格式
     *
     * @param aggregateData 聚合数据（可以是任意维度的聚合结果）
     * @param queryVO 查询参数（包含开始和结束日期）
     * @param groupBy 聚合维度（ASIN、SPU、SKU、SHOP等）
     * @return 管理利润表格式数据列表
     */
    List<ProfitReportManagementVO> convertToManagementReport(Object aggregateData, ProfitReportQueryVO queryVO, String groupBy);
    
    /**
     * 按SPU导出多Sheet的Excel（按店铺分文件）
     * 每个店铺生成一个Excel文件，每个SPU一个Sheet
     *
     * @param queryVO 查询参数
     * @return Map<文件名, Excel字节数组>
     * @throws Exception 导出失败时抛出
     */
    Map<String, byte[]> exportSpuSheetsByShop(ProfitReportQueryVO queryVO) throws Exception;
    
    /**
     * 纯SPU维度导出（不考虑店铺）
     * 每个SPU生成一个独立的Excel文件
     *
     * @param queryVO 查询参数
     * @return Map<文件名, Excel字节数组>
     * @throws Exception 导出失败时抛出
     */
    Map<String, byte[]> exportPureSpuSheets(ProfitReportQueryVO queryVO) throws Exception;
    
    /**
     * 纯SPU维度导出并合并历史Excel文件
     * 将新数据追加到历史Excel文件的对应Sheet中
     *
     * @param queryVO 查询参数
     * @param historyFileBytes 历史Excel文件的字节数组
     * @return Map<文件名, Excel字节数组>
     * @throws Exception 导出失败时抛出
     */
    Map<String, byte[]> exportPureSpuSheetsWithMerge(ProfitReportQueryVO queryVO, byte[] historyFileBytes) throws Exception;

}