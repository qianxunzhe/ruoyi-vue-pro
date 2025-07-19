package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 亚马逊评论查询DTO
 * 
 * @author 芋道源码
 */
@Data
public class AmazonReviewQueryDTO {

    /**
     * 排序类型
     */
    @JsonProperty("sort_field")
    private String sortField = "review_date";

    /**
     * 排序方式
     */
    @JsonProperty("sort_type")
    private String sortType = "desc";

    /**
     * 店铺id，多个用逗号分隔
     */
    @JsonProperty("sids")
    private String sids;

    /**
     * 站点id，多个用逗号分隔
     */
    @JsonProperty("mids")
    private String mids;

    /**
     * listing负责人，多个用逗号分隔
     */
    @JsonProperty("principal_uids")
    private String principalUids;

    /**
     * 搜索字段: asin、parent_asin、remark
     */
    @JsonProperty("search_field")
    private String searchField = "asin";

    /**
     * 搜索值
     */
    @JsonProperty("search_value")
    private String searchValue;

    /**
     * 时间搜索类型: review_time、create_time、last_update_time
     */
    @JsonProperty("date_field")
    private String dateField = "create_time";

    /**
     * 开始时间，格式：Y-m-d
     */
    @JsonProperty("start_date")
    private String startDate;

    /**
     * 结束时间，格式：Y-m-d
     */
    @JsonProperty("end_date")
    private String endDate;

    /**
     * 状态，多个用逗号分隔: 0 待处理, 1 处理中, 2 已完成
     */
    @JsonProperty("status")
    private String status;

    /**
     * 星级，多个用逗号分隔
     */
    @JsonProperty("star")
    private String star;

    /**
     * 内容，多个用逗号分隔: -1 已删除, 0 未标识, 1 已变更
     */
    @JsonProperty("review_modified_status")
    private String reviewModifiedStatus;

    /**
     * 标识，多个用逗号分隔: is_vp, is_er, is_topc, is_topr, is_vine
     */
    @JsonProperty("mark")
    private String mark;

    /**
     * 处理人，多个用逗号分隔
     */
    @JsonProperty("cs_principal_uids")
    private String csPrincipalUids;

    /**
     * 分页偏移量，默认0
     */
    @JsonProperty("offset")
    private Integer offset = 0;

    /**
     * 分页长度，默认20，上限200
     */
    @JsonProperty("length")
    private Integer length = 20;

    /**
     * 分类id，多个用逗号分隔
     */
    @JsonProperty("cids")
    private String cids;

    /**
     * 标签id，多个用逗号分隔
     */
    @JsonProperty("global_tag_ids")
    private String globalTagIds;

    /**
     * 匹配类型，多个用逗号分隔
     */
    @JsonProperty("match_types")
    private String matchTypes;
}