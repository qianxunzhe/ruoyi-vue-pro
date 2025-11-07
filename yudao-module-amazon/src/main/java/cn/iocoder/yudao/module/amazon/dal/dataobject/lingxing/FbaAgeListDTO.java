package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 领星API - 亚马逊库存库龄数据DTO
 * 对应Manage Inventory Health报表数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FbaAgeListDTO {
    
    /**
     * 店铺id
     */
    @JsonProperty("sid")
    private Integer sid;
    
    /**
     * 生成报告的日期
     */
    @JsonProperty("snapshot_date")
    private String snapshotDate;
    
    /**
     * SKU - 您分配给所售商品的唯一编码
     */
    @JsonProperty("sku")
    private String sku;
    
    /**
     * FNSKU - 亚马逊为存放在亚马逊运营中心并从亚马逊运营中心配送的商品分配的唯一编码
     */
    @JsonProperty("fnsku")
    private String fnsku;
    
    /**
     * ASIN - 亚马逊为通过亚马逊销售的商品分配的唯一编码，由10个字母或数字组成
     */
    @JsonProperty("asin")
    private String asin;
    
    /**
     * 可售库存数量
     */
    @JsonProperty("available")
    private Integer available;
    
    /**
     * 您请求退还或弃置的商品数量
     */
    @JsonProperty("pending_removal_quantity")
    private Integer pendingRemovalQuantity;
    
    /**
     * 已在运营中心存放0-90天的可售商品数量
     */
    @JsonProperty("inv_age_0_to_90_days")
    private Integer invAge0To90Days;
    
    /**
     * 已在运营中心存放91-180天的可售商品数量
     */
    @JsonProperty("inv_age_91_to_180_days")
    private Integer invAge91To180Days;
    
    /**
     * 已在运营中心存放181-270天的可售商品数量
     */
    @JsonProperty("inv_age_181_to_270_days")
    private Integer invAge181To270Days;
    
    /**
     * 已在运营中心存放271-365天的可售商品数量
     */
    @JsonProperty("inv_age_271_to_365_days")
    private Integer invAge271To365Days;
    
    /**
     * 已在运营中心存放超过365天的可售商品数量
     */
    @JsonProperty("inv_age_365_plus_days")
    private Integer invAge365PlusDays;
}