package cn.iocoder.yudao.module.amazon.service.lingXingAPI;

import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonStoreDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.LingXingApiResponseDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ListingOperateLogDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ListingOperateLogQueryDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ListingOperateLogResponseDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonListingDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonListingQueryDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonReviewDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonReviewQueryDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ReviewReportDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ReviewReportQueryDTO;

import java.util.List;
import java.util.Map;

/**
 * 领星API服务接口
 *
 * @author 芋道源码
 */
public interface LingXingApiService {

    /**
     * 获取访问令牌
     * 
     * @return 访问令牌信息
     */
    Map<String, Object> getAccessToken();

    /**
     * 续约访问令牌
     * 
     * @return 新的令牌信息
     */
    Map<String, Object> refreshAccessToken();

    /**
     * 发送GET请求
     * 
     * @param path API路径
     * @param params 业务参数
     * @return 响应结果
     */
    String sendGetRequest(String path, Map<String, Object> params);

    /**
     * 发送POST请求
     * 
     * @param path API路径
     * @param body 请求体
     * @return 响应结果
     */
    String sendPostRequest(String path, Object body);

    /**
     * 检查并刷新令牌
     */
    void checkAndRefreshToken();

    /**
     * 查询亚马逊店铺列表
     * 
     * @return 店铺列表
     */
    LingXingApiResponseDTO<List<AmazonStoreDTO>> getAmazonStoreList();

    /**
     * 获取所有美国正常店铺的sid列表
     * 
     * @return 美国正常店铺的sid列表
     */
    List<Long> getUSNormalStoreSids();


    /**
     * 根据ASIN列表和sid列表批量查询Listing信息（支持超过10个ASIN的分批处理）
     *
     * @param asinList ASIN列表，支持任意数量，会自动分批处理
     * @param sidList 店铺ID列表
     * @return Listing列表
     */
    List<AmazonListingDTO> getAllAmazonListingList(List<String> asinList, List<Long> sidList);

    /**
     * 根据ASIN列表和sid列表批量查询Listing信息
     *
     * @param asinList ASIN列表，最多10个
     * @param sidList 店铺ID列表
     * @return Listing列表
     */
    List<AmazonListingDTO> getAmazonListingList(List<String> asinList, List<Long> sidList);

    /**
     * 根据ASIN列表和sid列表批量查询Listing信息（支持更多查询参数）
     *
     * @param asinList ASIN列表，最多10个
     * @param sidList 店铺ID列表
     * @param queryParams 额外的查询参数
     * @return Listing列表响应
     */
    LingXingApiResponseDTO<List<AmazonListingDTO>> getAmazonListingList(List<String> asinList, List<Long> sidList, AmazonListingQueryDTO queryParams);

    /**
     * 获取指定ASIN的Review总数量
     *
     * @param asin ASIN编码
     * @param startDate 开始时间，格式：Y-m-d
     * @param endDate 结束时间，格式：Y-m-d
     * @return Review总数量
     */
    Integer getAsinReviewCount(String asin, String startDate, String endDate);

    /**
     * 获取指定ASIN的Review总数量（带更多查询参数）
     *
     * @param asin ASIN编码
     * @param queryParams 查询参数
     * @return Review总数量
     */
    Integer getAsinReviewCount(String asin, AmazonReviewQueryDTO queryParams);

    /**
     * 查询亚马逊Review列表
     *
     * @param queryParams 查询参数
     * @return Review列表响应
     */
    LingXingApiResponseDTO<List<AmazonReviewDTO>> getAmazonReviewList(AmazonReviewQueryDTO queryParams);

    /**
     * 查询评价统计-Review每日新增数
     *
     * @param queryParams 查询参数
     * @return Review统计响应
     */
    LingXingApiResponseDTO<List<ReviewReportDTO>> getReviewReport(ReviewReportQueryDTO queryParams);

    /**
     * 获取指定ASIN的昨日新增Review数量
     *
     * @param asin ASIN编码
     * @param mid 国家ID，默认1（美国）
     * @return 昨日新增Review数量
     */
    Integer getYesterdayReviewCount(String asin, Integer mid);

    /**
     * 获取指定ASIN在指定时间范围内的总Review数量
     *
     * @param asin ASIN编码
     * @param mid 国家ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 总Review数量
     */
    Integer getTotalReviewCount(String asin, Integer mid, String startDate, String endDate);
} 