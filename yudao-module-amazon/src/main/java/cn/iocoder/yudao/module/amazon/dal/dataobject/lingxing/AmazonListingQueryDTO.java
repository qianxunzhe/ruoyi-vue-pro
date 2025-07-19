package cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing;

import lombok.Data;

/**
 * 亚马逊Listing查询参数DTO
 *
 * @author 芋道源码
 */
@Data
public class AmazonListingQueryDTO {

    /**
     * 是否配对：1 已配对，2 未配对
     */
    private Integer isPair;

    /**
     * 是否删除：0 未删除，1 已删除
     */
    private Integer isDelete;

    /**
     * 配对更新时间的开始时间（此为北京时间，格式：Y-m-d H:i:s）
     */
    private String pairUpdateStartTime;

    /**
     * 配对更新时间的结束时间（此为北京时间，格式：Y-m-d H:i:s）
     */
    private String pairUpdateEndTime;

    /**
     * All Listing报表更新时间的开始时间（此为零时区时间，格式Y-m-d H:i:s）
     */
    private String listingUpdateStartTime;

    /**
     * All Listing报表更新时间的结束时间（此为零时区时间，格式Y-m-d H:i:s）
     */
    private String listingUpdateEndTime;

    /**
     * 商品类型，1-非低价商店 ，2-低价商店商品
     */
    private Integer storeType;

    /**
     * 分页偏移量，默认0
     */
    private Integer offset;

    /**
     * 分页长度，默认1000，上限1000
     */
    private Integer length;

    /**
     * 构造器：设置默认值
     */
    public AmazonListingQueryDTO() {
        this.isDelete = 0; // 默认查询未删除的
        this.offset = 0;
        this.length = 1000;
    }

    /**
     * 静态工厂方法：创建默认查询参数
     */
    public static AmazonListingQueryDTO defaultQuery() {
        return new AmazonListingQueryDTO();
    }

    /**
     * 静态工厂方法：创建已配对商品查询参数
     */
    public static AmazonListingQueryDTO pairedQuery() {
        AmazonListingQueryDTO query = new AmazonListingQueryDTO();
        query.setIsPair(1);
        return query;
    }

    /**
     * 静态工厂方法：创建分页查询参数
     */
    public static AmazonListingQueryDTO pagedQuery(int offset, int length) {
        AmazonListingQueryDTO query = new AmazonListingQueryDTO();
        query.setOffset(offset);
        query.setLength(Math.min(length, 1000)); // 限制最大1000
        return query;
    }
} 