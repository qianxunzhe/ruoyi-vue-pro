package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 领星产品表现查询参数DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPerformanceQueryDTO {
    
    /**
     * 分页偏移量
     */
    @JsonProperty("offset")
    private Integer offset;
    
    /**
     * 分页长度，最大10000
     */
    @JsonProperty("length")
    private Integer length;
    
    /**
     * 排序字段
     */
    @JsonProperty("sort_field")
    private String sortField;
    
    /**
     * 排序方式：desc【降序】、asc【升序】，默认desc
     */
    @JsonProperty("sort_type")
    private String sortType;
    
    /**
     * 搜索字段
     */
    @JsonProperty("search_field")
    private String searchField;
    
    /**
     * 搜索值，最多批量搜索50个
     */
    @JsonProperty("search_value")
    private List<String> searchValue;
    
    /**
     * 站点ID
     */
    @JsonProperty("mid")
    private Integer mid;
    
    /**
     * 店铺ID，上限200
     */
    @JsonProperty("sid")
    private Object sid;
    
    /**
     * 开始日期，格式：Y-m-d
     */
    @JsonProperty("start_date")
    private String startDate;
    
    /**
     * 结束日期，格式：Y-m-d
     */
    @JsonProperty("end_date")
    private String endDate;
    
    /**
     * 表头筛选
     */
    @JsonProperty("extend_search")
    private List<ExtendSearch> extendSearch;
    
    /**
     * 汇总行维度
     */
    @JsonProperty("summary_field")
    private String summaryField;
    
    /**
     * 货币类型
     */
    @JsonProperty("currency_code")
    private String currencyCode;
    
    /**
     * 是否仅查询活跃商品
     */
    @JsonProperty("is_recently_enum")
    private Boolean isRecentlyEnum;
    
    /**
     * 扩展搜索条件
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtendSearch {
        /**
         * 筛选字段
         */
        @JsonProperty("field")
        private String field;
        
        /**
         * 起始值
         */
        @JsonProperty("from_value")
        private String fromValue;
        
        /**
         * 结束值
         */
        @JsonProperty("to_value")
        private String toValue;
        
        /**
         * 表达式：range、gt、lt、ge、le、eq
         */
        @JsonProperty("exp")
        private String exp;
    }
}