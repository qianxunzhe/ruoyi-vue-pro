package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 领星利润报表查询参数DTO
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfitReportQueryDTO {
    
    /**
     * 分页偏移量
     */
    private Integer offset;
    
    /**
     * 分页长度，上限10000
     */
    private Integer length;
    
    /**
     * 站点id列表
     */
    private List<Integer> mids;
    
    /**
     * 店铺id列表
     */
    private List<Long> sids;
    
    /**
     * 是否按月查询：false 按天【默认值】，true 按月
     */
    private Boolean monthlyQuery;
    
    /**
     * 开始时间【结算时间，双闭区间】
     * 按天：格式：Y-m-d
     * 按月：格式：Y-m
     */
    private String startDate;
    
    /**
     * 结束时间【结算时间，双闭区间】
     * 按天：格式：Y-m-d
     * 按月：格式：Y-m
     */
    private String endDate;
    
    /**
     * 搜索值类型，如：asin
     */
    private String searchField;
    
    /**
     * 搜索的值列表
     */
    private List<String> searchValue;
    
    /**
     * 币种code
     */
    private String currencyCode;
    
    /**
     * 是否按asin汇总返回：false 默认值，true
     */
    private Boolean summaryEnabled;
    
    /**
     * 交易状态
     * Deferred 已推迟
     * Disbursed 已发放【默认】
     * DisbursedAndPreSettled 已发放（含预结算）
     * All 全部
     */
    private String orderStatus;
}