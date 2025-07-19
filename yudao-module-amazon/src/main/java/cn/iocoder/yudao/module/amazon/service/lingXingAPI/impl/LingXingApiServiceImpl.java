package cn.iocoder.yudao.module.amazon.service.lingXingAPI.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.module.amazon.config.LingXingConfig;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonStoreDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonListingDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonListingQueryDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.LingXingApiResponseDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonReviewDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonReviewQueryDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ReviewReportDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ReviewReportQueryDTO;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import cn.iocoder.yudao.module.amazon.utils.LingXingTokenManager;
import cn.iocoder.yudao.module.amazon.utils.LingXingUtils;
import cn.iocoder.yudao.module.amazon.utils.LingXingJsonUtils;
import cn.iocoder.yudao.module.amazon.openapi.okhttp.AKRestClient;
import cn.iocoder.yudao.module.amazon.openapi.core.Config;
import cn.iocoder.yudao.module.amazon.openapi.entity.Result;
import cn.iocoder.yudao.module.amazon.openapi.sign.ApiSign;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 领星API服务实现 - 优化版本
 * 使用单例模式和Hutool TimedCache缓存accessToken
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LingXingApiServiceImpl implements LingXingApiService {

    private final LingXingConfig lingXingConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 缓存相关
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String TOKEN_EXPIRE_TIME_KEY = "token_expire_time";
    
    /**
     * Token缓存，默认过期时间25分钟（提前5分钟刷新）
     * 领星API的token有效期通常是30分钟
     */
    private TimedCache<String, Object> tokenCache;
    
    /**
     * 重入锁，确保token刷新的线程安全
     */
    private final ReentrantLock tokenLock = new ReentrantLock();
    
    /**
     * 单例实例（虽然已经通过Spring管理，但这里提供双重保障）
     */
    private static volatile LingXingApiServiceImpl instance;

    @Resource
    private LingXingTokenManager tokenManager;

    @PostConstruct
    public void init() {
        // 初始化缓存，默认25分钟过期（提前5分钟刷新）
        this.tokenCache = CacheUtil.newTimedCache(25 * DateUnit.MINUTE.getMillis());
        
        // 启动定时清理任务，每5分钟清理一次过期条目
        this.tokenCache.schedulePrune(5 * DateUnit.MINUTE.getMillis());
        
        log.info("领星API服务初始化完成，缓存过期时间: 25分钟");
        
        // 设置单例实例
        instance = this;
    }

    @PreDestroy
    public void destroy() {
        if (tokenCache != null) {
            // 取消定时清理任务
            tokenCache.cancelPruneSchedule();
            log.info("领星API服务销毁完成，缓存清理任务已取消");
        }
    }

    /**
     * 获取单例实例
     */
    public static LingXingApiServiceImpl getInstance() {
        return instance;
    }

    @Override
    public Map<String, Object> getAccessToken() {
        log.info("正在获取领星API访问令牌...");
        
        // 先检查缓存中是否有有效的token
        String cachedToken = getCachedToken();
        log.info("缓存状态检查: cachedToken={}, 缓存大小={}", 
            cachedToken != null ? "存在" : "不存在", 
            tokenCache.size());
            
        if (StrUtil.isNotBlank(cachedToken)) {
            log.info("从缓存中获取到有效的访问令牌，剩余时间: {} 秒", getRemainingTime());
            Map<String, Object> result = new HashMap<>();
            result.put("access_token", cachedToken);
            result.put("refresh_token", tokenCache.get(REFRESH_TOKEN_KEY));
            result.put("expires_in", getRemainingTime());
            return result;
        }
        
        log.info("缓存中没有有效token，需要获取新token...");
        
        // 缓存中没有有效token，从API获取新token
        tokenLock.lock();
        try {
            // 双重检查，避免多线程重复获取
            cachedToken = getCachedToken();
            if (StrUtil.isNotBlank(cachedToken)) {
                log.info("其他线程已获取到访问令牌，直接使用");
                Map<String, Object> result = new HashMap<>();
                result.put("access_token", cachedToken);
                result.put("refresh_token", tokenCache.get(REFRESH_TOKEN_KEY));
                result.put("expires_in", getRemainingTime());
                return result;
            }
            
            // 调用API获取新token（带重试机制）
            Map<String, Object> tokenResult = null;
            int retryCount = 0;
            int maxRetries = 3;
            
            while (retryCount < maxRetries) {
                try {
                    tokenResult = fetchNewAccessToken();
                    break; // 成功获取，跳出循环
                } catch (Exception e) {
                    retryCount++;
                    log.warn("获取token失败，重试次数: {}/{}, 错误: {}", retryCount, maxRetries, e.getMessage());
                    
                    if (retryCount >= maxRetries) {
                        log.error("获取token失败，已达到最大重试次数: {}", maxRetries);
                        throw new RuntimeException("获取访问令牌失败，重试" + maxRetries + "次后仍然失败: " + e.getMessage(), e);
                    }
                    
                    // 等待一段时间再重试
                    try {
                        Thread.sleep(1000 * retryCount); // 递增等待时间
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("获取token被中断", ie);
                    }
                }
            }

            // 缓存新token
            cacheTokenInfo(tokenResult);
            
            log.info("成功获取并缓存新的访问令牌");
            return tokenResult;
            
        } finally {
            tokenLock.unlock();
        }
    }

    @Override
    public Map<String, Object> refreshAccessToken() {
        log.info("正在刷新领星API访问令牌...");
        
        tokenLock.lock();
        try {
            String refreshToken = (String) tokenCache.get(REFRESH_TOKEN_KEY);
            
            if (StrUtil.isBlank(refreshToken)) {
                log.warn("refresh_token为空，重新获取access_token");
                return getAccessToken();
            }
            
            // 调用API刷新token
            Map<String, Object> tokenResult = refreshTokenFromAPI(refreshToken);
            
            // 缓存新token
            cacheTokenInfo(tokenResult);
            
            log.info("成功刷新并缓存访问令牌");
            return tokenResult;
            
        } catch (Exception e) {
            log.error("刷新访问令牌失败，尝试重新获取", e);
            // 清除缓存中的无效token
            clearTokenCache();
            // 重新获取token
            return getAccessToken();
        } finally {
            tokenLock.unlock();
        }
    }

    @Override
    public String sendGetRequest(String path, Map<String, Object> params) {
        ensureValidToken();
        
        try {
            log.info("=== GET请求开始 ===");
            log.info("请求路径: {}", path);
            
            // 构建公共参数
            Map<String, Object> allParams = LingXingUtils.buildCommonParams(tokenManager.getValidAccessToken(),
                lingXingConfig.getAppKey()
            );
            
            log.info("公共参数: {}", allParams);
            
            // 添加业务参数
            if (params != null) {
                allParams.putAll(params);
                log.info("业务参数: {}", params);
            }
            
            // 移除值为空的参数，不参与签名
            allParams.values().removeIf(value -> value instanceof String && ((String) value).isEmpty());
            log.info("移除空值后的完整参数(用于签名): {}", allParams);
            
            // 使用SDK的签名方法生成签名 - 使用appId作为密钥
            String sign = ApiSign.sign(allParams, lingXingConfig.getAppId());
            log.info("生成的签名(未编码): {}", sign);
            
            // 将签名进行URL编码
            allParams.put("sign", URLEncodeUtil.encode(sign));
            
            // 构建完整URL
            String queryString = LingXingUtils.buildQueryString(allParams);
            String fullUrl = lingXingConfig.getApiDomain() + path + "?" + queryString;
            
            log.info("=== 完整GET请求信息 ===");
            log.info("Method: GET");
            log.info("URL: {}", fullUrl);
            log.info("=== GET请求信息结束 ===");
            
            // 发送请求
            String response = HttpUtils.get(fullUrl, null);
            
            log.info("=== GET响应信息 ===");
            log.info("响应内容: {}", response);
            log.info("=== 响应信息结束 ===");
            
            return response;
        } catch (Exception e) {
            log.error("发送GET请求失败: path={}, params={}", path, params, e);
            throw new RuntimeException("GET请求失败", e);
        }
    }

    @Override
    public String sendPostRequest(String path, Object body) {
        ensureValidToken();
        
        try {
            log.info("=== POST请求开始 ===");
            log.info("请求路径: {}", path);

            // 1. 构建公共参数 (queryParam)
            Map<String, Object> queryParam = LingXingUtils.buildCommonParams(tokenManager.getValidAccessToken(), lingXingConfig.getAppKey());
            log.info("公共参数(queryParam): {}", queryParam);

            // 2. 准备业务参数 (body)
            // 按照示例，业务参数是一个Map
            if (!(body instanceof Map)) {
                log.error("POST请求的body必须是Map类型");
                throw new IllegalArgumentException("POST请求的body必须是Map类型");
            }
            Map<String, Object> bodyMap = (Map<String, Object>) body;
            log.info("业务参数(body): {}", JSONUtil.toJsonStr(bodyMap));

            // 3. 合并参数用于签名计算 (signMap = queryParam + body)
            Map<String, Object> signMap = new HashMap<>();
            signMap.putAll(queryParam);
            signMap.putAll(bodyMap);
            
            // 移除值为空的参数，不参与签名
            signMap.values().removeIf(value -> value instanceof String && ((String) value).isEmpty());
            
            // 处理集合类型参数用于签名计算（参考OrderListDemo.java的做法）
            Map<String, Object> signMapForCalculation = new HashMap<>();
            for (Map.Entry<String, Object> entry : signMap.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof List) {
                    // 集合类型转换为JSON字符串参与签名计算
                    signMapForCalculation.put(entry.getKey(), JSONUtil.toJsonStr(value));
                } else {
                    signMapForCalculation.put(entry.getKey(), value);
                }
            }
            
            log.info("移除空值后的完整签名参数(signMap): {}", signMap);
            log.info("用于签名计算的参数(signMapForCalculation): {}", signMapForCalculation);

            // 4. 生成签名 - 使用 appId 作为密钥，注意这里使用转换后的参数
            String sign = ApiSign.sign(signMapForCalculation, lingXingConfig.getAppId());
            log.info("生成的签名: {}", sign);

            // 5. 将签名添加到公共参数中，准备放入URL
            queryParam.put("sign", sign);

            // 6. 构建URL (URL只包含公共参数和签名)
            String queryString = LingXingUtils.buildQueryString(queryParam);
            String fullUrl = lingXingConfig.getApiDomain() + path + "?" + queryString;

            // 7. 准备最终发送的Body (就是原始的body)
            String finalBodyJson = JSONUtil.toJsonStr(bodyMap);

            // 8. 设置请求头
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            log.info("=== 完整POST请求信息 ===");
            log.info("Method: POST");
            log.info("URL: {}", fullUrl);
            log.info("Headers: {}", headers);
            log.info("Body(实际发送): {}", finalBodyJson);
            log.info("=== 请求信息结束 ===");

            // 9. 发送请求
            String response = HttpUtils.post(fullUrl, headers, finalBodyJson);
            
            log.info("=== POST响应信息 ===");
            log.info("响应内容: {}", response);
            log.info("=== 响应信息结束 ===");
            
            return response;
        } catch (Exception e) {
            log.error("=== POST请求失败 ===", e);
            throw new RuntimeException("POST请求失败", e);
        }
    }

    /**
     * 处理body参数用于签名
     * 将集合参数转换为字符串参与签名
     */
    // 此方法已不再需要，逻辑已内联到 sendPostRequest 中
    // private Map<String, Object> processBodyForSign(Map<String, Object> bodyMap) { ... }


    @Override
    public void checkAndRefreshToken() {
        String cachedToken = getCachedToken();
        
        // 如果没有token，先获取
        if (StrUtil.isBlank(cachedToken)) {
            log.info("缓存中无访问令牌，开始获取新令牌...");
            getAccessToken();
            return;
        }
        
        // 检查token是否即将过期（剩余时间小于5分钟）
        Long remainingTime = getRemainingTime();
        if (remainingTime != null && remainingTime < 5 * 60) { // 5分钟 = 300秒
            log.info("访问令牌即将过期（剩余{}秒），开始自动刷新...", remainingTime);
            refreshAccessToken();
        } else {
            log.debug("访问令牌有效，剩余时间: {}秒", remainingTime);
        }
    }

    @Override
    public LingXingApiResponseDTO<List<AmazonStoreDTO>> getAmazonStoreList() {
        log.info("开始查询亚马逊店铺列表...");
        
        try {
            // 调用领星API获取店铺列表
            String response = sendGetRequest("/erp/sc/data/seller/lists", null);
            
            log.debug("获取店铺列表响应: {}", response);
            
            // 解析响应为DTO
            TypeReference<LingXingApiResponseDTO<List<AmazonStoreDTO>>> typeReference =
                    new TypeReference<LingXingApiResponseDTO<List<AmazonStoreDTO>>>() {};
            LingXingApiResponseDTO<List<AmazonStoreDTO>> result = objectMapper.readValue(response, typeReference);
            
            if (result.isSuccess()) {
                log.info("成功获取店铺列表，共{}个店铺", result.getData() != null ? result.getData().size() : 0);
                
                // 记录店铺信息
                if (result.getData() != null) {
                    for (AmazonStoreDTO store : result.getData()) {
                        log.debug("店铺信息: sid={}, name={}, region={}, country={}, status={}", 
                            store.getSid(), store.getName(), store.getRegion(), store.getCountry(), store.getStatus());
                    }
                }
            } else {
                log.error("获取店铺列表失败: code={}, message={}", result.getCode(), result.getMsg());
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询亚马逊店铺列表失败", e);
            throw new RuntimeException("查询店铺列表失败", e);
        }
    }

    @Override
    public List<Long> getUSNormalStoreSids() {
        log.info("开始获取美国正常店铺sid列表...");
        
        try {
            // 获取所有店铺列表
            LingXingApiResponseDTO<List<AmazonStoreDTO>> storeListResult = getAmazonStoreList();
            
            if (!storeListResult.isSuccess() || storeListResult.getData() == null) {
                log.error("获取店铺列表失败，无法筛选美国正常店铺");
                return new ArrayList<>();
            }
            
            // 筛选美国正常店铺的sid
            List<Long> usSids = storeListResult.getData().stream()
                .filter(store -> "NA".equals(store.getRegion()) && store.getStatus() == 1)
                .map(AmazonStoreDTO::getSid)
                .collect(java.util.stream.Collectors.toList());
            
            log.info("成功获取美国正常店铺sid列表，共{}个店铺: {}", usSids.size(), usSids);
            
            return usSids;
        } catch (Exception e) {
            log.error("获取美国正常店铺sid列表失败", e);
            throw new RuntimeException("获取美国正常店铺sid列表失败", e);
        }
    }

    @Override
    public List<AmazonListingDTO> getAllAmazonListingList(List<String> asinList, List<Long> sidList) {
        log.info("开始查询亚马逊Listing列表，ASIN总数量: {}, SID数量: {}", 
            asinList != null ? asinList.size() : 0, 
            sidList != null ? sidList.size() : 0);
        
        // 先对ASIN进行去重处理
        List<String> uniqueAsinList = null;
        if (asinList != null && !asinList.isEmpty()) {
            uniqueAsinList = asinList.stream()
                .filter(asin -> asin != null && !asin.trim().isEmpty())
                .distinct()
                .collect(java.util.stream.Collectors.toList());
            
            if (uniqueAsinList.size() != asinList.size()) {
                log.info("ASIN去重处理：原始数量 {}, 去重后数量 {}", asinList.size(), uniqueAsinList.size());
            }
        }
        
        // 领星API支持一次性获取最多1000条数据，无需分批处理
        LingXingApiResponseDTO<List<AmazonListingDTO>> result = getAmazonListingList(uniqueAsinList, sidList, null);
        
        if (result.isSuccess() && result.getData() != null) {
            log.info("成功获取{}个Listing", result.getData().size());
            return result.getData();
        } else {
            log.error("查询Listing失败: code={}, message={}", result.getCode(), result.getMsg());
            return new ArrayList<>();
        }
    }

    @Override
    public List<AmazonListingDTO> getAmazonListingList(List<String> asinList, List<Long> sidList) {
        LingXingApiResponseDTO<List<AmazonListingDTO>> result = getAmazonListingList(asinList, sidList, null);
        return result.isSuccess() && result.getData() != null ? result.getData() : new ArrayList<>();
    }

    @Override
    public LingXingApiResponseDTO<List<AmazonListingDTO>> getAmazonListingList(List<String> asinList, List<Long> sidList, AmazonListingQueryDTO queryParams) {
        log.info("开始查询亚马逊Listing列表，ASIN数量: {}, SID数量: {}", 
            asinList != null ? asinList.size() : 0, 
            sidList != null ? sidList.size() : 0);
        
        try {
            // 构建请求参数
            Map<String, Object> requestBody = buildListingQueryParams(asinList, sidList, queryParams);
            
            // 调用领星API获取Listing列表
            String response = sendPostRequest("/erp/sc/data/mws/listing", requestBody);
            
            log.debug("获取Listing列表响应: {}", response);
            
            // 解析响应为DTO
            TypeReference<LingXingApiResponseDTO<List<AmazonListingDTO>>> typeReference =
                    new TypeReference<LingXingApiResponseDTO<List<AmazonListingDTO>>>() {};
            LingXingApiResponseDTO<List<AmazonListingDTO>> result = objectMapper.readValue(response, typeReference);
            
            if (result.isSuccess()) {
                log.info("成功获取Listing列表，共{}个Listing", result.getData() != null ? result.getData().size() : 0);
                
                // 记录部分Listing信息
                if (result.getData() != null && !result.getData().isEmpty()) {
                    for (int i = 0; i < Math.min(5, result.getData().size()); i++) {
                        AmazonListingDTO listing = result.getData().get(i);
                        log.debug("Listing信息[{}]: asin={}, seller_sku={}, sid={}, status={}", 
                            i, listing.getAsin(), listing.getSellerSku(), listing.getSid(), listing.getStatus());
                    }
                    if (result.getData().size() > 5) {
                        log.debug("... 还有{}个Listing", result.getData().size() - 5);
                    }
                }
            } else {
                log.error("获取Listing列表失败: code={}, message={}", result.getCode(), result.getMsg());
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询亚马逊Listing列表失败", e);
            throw new RuntimeException("查询Listing列表失败", e);
        }
    }

    /**
     * 确保有有效的token
     */
    private void ensureValidToken() {
        String cachedToken = getCachedToken();
        if (StrUtil.isBlank(cachedToken)) {
            log.info("访问令牌为空，开始获取新令牌...");
            getAccessToken();
        } else {
            checkAndRefreshToken();
        }
    }

    /**
     * 从缓存中获取access_token
     */
    private String getCachedToken() {
        return (String) tokenCache.get(ACCESS_TOKEN_KEY, false); // false 表示获取时不重新计算过期时间
    }

    /**
     * 获取token剩余有效时间（秒）
     */
    private Long getRemainingTime() {
        Long expireTime = (Long) tokenCache.get(TOKEN_EXPIRE_TIME_KEY, false);
        if (expireTime == null) {
            return null;
        }
        long remaining = (expireTime - System.currentTimeMillis()) / 1000;
        return Math.max(0, remaining);
    }

    /**
     * 缓存token信息
     */
    private void cacheTokenInfo(Map<String, Object> tokenResult) {
        if (tokenResult != null) {
            String accessToken = (String) tokenResult.get("access_token");
            String refreshToken = (String) tokenResult.get("refresh_token");
            Integer expiresIn = (Integer) tokenResult.get("expires_in");
            
            if (StrUtil.isNotBlank(accessToken)) {
                // 计算过期时间（提前5分钟）
                long expireTime = System.currentTimeMillis() + (expiresIn != null ? (expiresIn - 300) * 1000L : 25 * 60 * 1000L);
                
                // 缓存token信息
                tokenCache.put(ACCESS_TOKEN_KEY, accessToken);
                tokenCache.put(TOKEN_EXPIRE_TIME_KEY, expireTime);
                
                if (StrUtil.isNotBlank(refreshToken)) {
                    tokenCache.put(REFRESH_TOKEN_KEY, refreshToken);
                }
                
                log.info("Token信息已缓存，过期时间: {}", new java.util.Date(expireTime));
                
                // 同时更新配置中的token信息（向后兼容）
                updateTokenConfig(tokenResult);
            }
        }
    }

    /**
     * 清除token缓存
     */
    private void clearTokenCache() {
        tokenCache.remove(ACCESS_TOKEN_KEY);
        tokenCache.remove(REFRESH_TOKEN_KEY);
        tokenCache.remove(TOKEN_EXPIRE_TIME_KEY);
        log.info("已清除token缓存");
    }

    /**
     * 从API获取新的access_token
     */
    private Map<String, Object> fetchNewAccessToken() throws Exception {
        log.info("正在通过SDK获取领星API访问令牌...");
        
        // 创建SDK客户端
        AKRestClient client = new AKRestClient(lingXingConfig.getApiDomain(), Config.DEFAULT);
        
        // 调用SDK获取token
        Result result = client.getAccessToken(lingXingConfig.getAppId(), lingXingConfig.getAppSecret());
        
        log.info("SDK返回结果: {}", result);
        
        // 转换为标准格式
        Map<String, Object> tokenResult = new HashMap<>();
        if (result != null && result.getData() != null) {
            // 现在可以直接使用getData()方法了
            Object data = result.getData();
            
            if (data instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) data;
                tokenResult.put("access_token", dataMap.get("access_token"));
                tokenResult.put("refresh_token", dataMap.get("refresh_token"));
                tokenResult.put("expires_in", dataMap.get("expires_in"));
            } else {
                log.warn("SDK返回的data字段不是Map类型: {}", data.getClass().getName());
                log.info("实际数据: {}", data);
            }
        } else {
            log.warn("SDK返回结果为null或data为null: {}", result);
        }
        
        return tokenResult;
    }

    /**
     * 通过refresh_token刷新access_token
     */
    private Map<String, Object> refreshTokenFromAPI(String refreshToken) throws Exception {
        log.info("正在通过SDK续约领星API访问令牌...");
        
        // 创建SDK客户端
        AKRestClient client = new AKRestClient(lingXingConfig.getApiDomain(), Config.DEFAULT);
        
        // 调用SDK续约token
        Object result = client.refreshToken(lingXingConfig.getAppId(), refreshToken);
        
        log.info("SDK续约返回结果: {}", result);
        
        // 转换为标准格式
        Map<String, Object> tokenResult = new HashMap<>();
        if (result != null) {
            // 根据实际返回结构调整
            if (result instanceof Map) {
                Map<String, Object> data = (Map<String, Object>) result;
                tokenResult.put("access_token", data.get("access_token"));
                tokenResult.put("refresh_token", data.get("refresh_token"));
                tokenResult.put("expires_in", data.get("expires_in"));
            }
        }
        
        return tokenResult;
    }

    /**
     * 更新配置中的token信息（向后兼容）
     */
    private void updateTokenConfig(Map<String, Object> tokenResult) {
        if (tokenResult != null) {
            String accessToken = (String) tokenResult.get("access_token");
            String refreshToken = (String) tokenResult.get("refresh_token");
            Integer expiresIn = (Integer) tokenResult.get("expires_in");
            
            if (StrUtil.isNotBlank(accessToken)) {
                lingXingConfig.setAccessToken(accessToken);
            }
            if (StrUtil.isNotBlank(refreshToken)) {
                lingXingConfig.setRefreshToken(refreshToken);
            }
            if (expiresIn != null) {
                lingXingConfig.setTokenExpireTime(System.currentTimeMillis() + expiresIn * 1000L);
            }
        }
    }

    /**
     * 构建Listing查询参数
     */
    private Map<String, Object> buildListingQueryParams(List<String> asinList, List<Long> sidList, AmazonListingQueryDTO queryParams) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // 必填参数：店铺ID列表
        if (sidList != null && !sidList.isEmpty()) {
            String sidStr = sidList.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","));
            requestBody.put("sid", sidStr);
        } else {
            throw new IllegalArgumentException("sid列表不能为空");
        }
        
        // 如果有ASIN列表，设置搜索条件
        if (asinList != null && !asinList.isEmpty()) {
            requestBody.put("search_field", "asin");
            // 注意：这里直接传List，在签名时会被转换为JSON字符串
            requestBody.put("search_value", asinList);
            requestBody.put("exact_search", 1); // 精确搜索
        }
        
        // 应用额外的查询参数
        if (queryParams != null) {
            if (queryParams.getIsPair() != null) {
                requestBody.put("is_pair", queryParams.getIsPair());
            }
            if (queryParams.getIsDelete() != null) {
                requestBody.put("is_delete", queryParams.getIsDelete());
            }
            if (StrUtil.isNotBlank(queryParams.getPairUpdateStartTime())) {
                requestBody.put("pair_update_start_time", queryParams.getPairUpdateStartTime());
            }
            if (StrUtil.isNotBlank(queryParams.getPairUpdateEndTime())) {
                requestBody.put("pair_update_end_time", queryParams.getPairUpdateEndTime());
            }
            if (StrUtil.isNotBlank(queryParams.getListingUpdateStartTime())) {
                requestBody.put("listing_update_start_time", queryParams.getListingUpdateStartTime());
            }
            if (StrUtil.isNotBlank(queryParams.getListingUpdateEndTime())) {
                requestBody.put("listing_update_end_time", queryParams.getListingUpdateEndTime());
            }
            if (queryParams.getStoreType() != null) {
                requestBody.put("store_type", queryParams.getStoreType());
            }
            if (queryParams.getOffset() != null) {
                requestBody.put("offset", queryParams.getOffset());
            }
            if (queryParams.getLength() != null) {
                requestBody.put("length", Math.min(queryParams.getLength(), 1000)); // 最大1000
            }
        }
        
        // 设置默认值
        if (!requestBody.containsKey("is_delete")) {
            requestBody.put("is_delete", 0); // 默认查询未删除的
        }
        if (!requestBody.containsKey("offset")) {
            requestBody.put("offset", 0);
        }
        if (!requestBody.containsKey("length")) {
            requestBody.put("length", 1000);
        }
        
        log.debug("构建的Listing查询参数: {}", requestBody);
        return requestBody;
    }

    @Override
    public Integer getAsinReviewCount(String asin, String startDate, String endDate) {
        log.info("开始获取ASIN [{}] 的Review数量，时间范围: {} - {}", asin, startDate, endDate);
        
        // 构建查询参数，严格按照接口文档
        AmazonReviewQueryDTO queryParams = new AmazonReviewQueryDTO();
        queryParams.setSortField("review_date");  // 排序字段
        queryParams.setSortType("desc");          // 降序排列
        queryParams.setSearchField("asin");       // 搜索字段：asin
        queryParams.setSearchValue(asin);         // ASIN值
        queryParams.setStartDate(startDate);      // 开始日期
        queryParams.setEndDate(endDate);          // 结束日期
        queryParams.setDateField("create_time");  // 必填：时间字段使用create_time
        queryParams.setOffset(0);                 // 偏移量
        queryParams.setLength(20);                // 长度默认20
        
        log.info("构建的查询参数: sortField={}, sortType={}, searchField={}, searchValue={}, startDate={}, endDate={}, dateField={}, offset={}, length={}", 
            queryParams.getSortField(), queryParams.getSortType(), queryParams.getSearchField(), 
            queryParams.getSearchValue(), queryParams.getStartDate(), queryParams.getEndDate(),
            queryParams.getDateField(), queryParams.getOffset(), queryParams.getLength());
        
        return getAsinReviewCount(asin, queryParams);
    }

    @Override
    public Integer getAsinReviewCount(String asin, AmazonReviewQueryDTO queryParams) {
        if (StrUtil.isBlank(asin)) {
            throw new IllegalArgumentException("ASIN不能为空");
        }
        
        log.info("=== 开始获取ASIN [{}] 的Review数量 ===", asin);
        log.info("传入的查询参数: sortField={}, sortType={}, searchField={}, dateField={}", 
            queryParams.getSortField(), queryParams.getSortType(), queryParams.getSearchField(), queryParams.getDateField());
        
        // 确保查询参数正确，不覆盖已设置的默认值
        if (StrUtil.isBlank(queryParams.getSearchField())) {
            queryParams.setSearchField("asin");
        }
        queryParams.setSearchValue(asin);
        queryParams.setLength(20); // 默认长度20
        
        // 不设置空字符串参数，让它们保持null
        
        try {
            LingXingApiResponseDTO<List<AmazonReviewDTO>> response = getAmazonReviewList(queryParams);
            
            if (response != null && response.getCode() == 0) {
                Integer total = response.getTotal();
                log.info("ASIN [{}] 的Review总数量: {}", asin, total);
                return total != null ? total : 0;
            } else {
                log.error("获取Review数量失败，响应: {}", response);
                return 0;
            }
        } catch (Exception e) {
            log.error("获取ASIN [{}] Review数量异常", asin, e);
            return 0;
        }
    }

    @Override
    public LingXingApiResponseDTO<List<AmazonReviewDTO>> getAmazonReviewList(AmazonReviewQueryDTO queryParams) {
        log.info("开始查询Amazon Review列表");
        
        if (queryParams == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        
        // 验证必填参数
        if (StrUtil.isBlank(queryParams.getDateField())) {
            queryParams.setDateField("create_time");
        }
        if (StrUtil.isBlank(queryParams.getStartDate()) || StrUtil.isBlank(queryParams.getEndDate())) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        
        try {
            // 将DTO转换为Map，过滤null值
            Map<String, Object> requestBody = objectMapper.convertValue(queryParams, Map.class);
            requestBody.values().removeIf(Objects::isNull);
            
            log.info("=== Review查询请求详情 ===");
            log.info("API路径: /basicOpen/openapi/service/v3/data/mws/reviews");
            log.info("请求参数: {}", JSONUtil.toJsonStr(requestBody));
            log.info("期望格式: sort_field={}, sort_type={}, search_field={}, search_value={}, date_field={}, start_date={}, end_date={}, offset={}, length={}, cids={}, global_tag_ids={}, match_types={}", 
                requestBody.get("sort_field"), requestBody.get("sort_type"), requestBody.get("search_field"), 
                requestBody.get("search_value"), requestBody.get("date_field"), requestBody.get("start_date"), 
                requestBody.get("end_date"), requestBody.get("offset"), requestBody.get("length"),
                requestBody.get("cids"), requestBody.get("global_tag_ids"), requestBody.get("match_types"));
            
            // 调用API
            String response = sendPostRequest("/basicOpen/openapi/service/v3/data/mws/reviews", requestBody);
            log.info("=== Review查询响应详情 ===");
            log.info("响应原文: {}", response);
            
            // 先检查响应是否为空或格式错误
            if (StrUtil.isBlank(response)) {
                throw new RuntimeException("API响应为空");
            }
            
            // 解析响应
            TypeReference<LingXingApiResponseDTO<List<AmazonReviewDTO>>> typeRef = 
                new TypeReference<LingXingApiResponseDTO<List<AmazonReviewDTO>>>() {};
            
            LingXingApiResponseDTO<List<AmazonReviewDTO>> result;
            try {
                result = objectMapper.readValue(response, typeRef);
            } catch (Exception jsonException) {
                log.error("JSON解析失败，响应内容: {}", response, jsonException);
                throw new RuntimeException("API响应格式错误: " + jsonException.getMessage(), jsonException);
            }
            
            if (result.getCode() == 0) {
                log.info("成功获取Review列表，数量: {}, 总数: {}", 
                    result.getData() != null ? result.getData().size() : 0, 
                    result.getTotal());
            } else {
                log.error("获取Review列表失败: code={}, message={}", result.getCode(), result.getMsg());
                throw new RuntimeException("API返回错误: code=" + result.getCode() + ", message=" + result.getMsg());
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询Review列表异常，查询参数: {}", JSONUtil.toJsonStr(queryParams), e);
            throw new RuntimeException("查询Review列表失败: " + e.getMessage(), e);
        }
    }

    @Override
    public LingXingApiResponseDTO<List<ReviewReportDTO>> getReviewReport(ReviewReportQueryDTO queryParams) {
        log.info("开始查询Review统计报告");
        
        if (queryParams == null) {
            throw new IllegalArgumentException("查询参数不能为空");
        }
        
        // 验证必填参数
        if (StrUtil.isBlank(queryParams.getAsin())) {
            throw new IllegalArgumentException("ASIN不能为空");
        }
        if (StrUtil.isBlank(queryParams.getStartDate()) || StrUtil.isBlank(queryParams.getEndDate())) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        
        try {
            // 将DTO转换为Map，过滤null值
            Map<String, Object> requestBody = objectMapper.convertValue(queryParams, Map.class);
            requestBody.values().removeIf(Objects::isNull);
            
            log.info("Review统计查询请求参数: {}", JSONUtil.toJsonStr(requestBody));
            
            // 调用API
            String response = sendPostRequest("/erp/sc/cs/reviewReport/detail", requestBody);
            log.info("Review统计API响应: {}", response);
            
            // 先检查响应是否为空或格式错误
            if (StrUtil.isBlank(response)) {
                throw new RuntimeException("API响应为空");
            }
            
            // 解析响应
            TypeReference<LingXingApiResponseDTO<List<ReviewReportDTO>>> typeRef = 
                new TypeReference<LingXingApiResponseDTO<List<ReviewReportDTO>>>() {};
            
            LingXingApiResponseDTO<List<ReviewReportDTO>> result;
            try {
                result = objectMapper.readValue(response, typeRef);
            } catch (Exception jsonException) {
                log.error("JSON解析失败，响应内容: {}", response, jsonException);
                throw new RuntimeException("API响应格式错误: " + jsonException.getMessage(), jsonException);
            }
            
            if (result.getCode() == 0) {
                log.info("成功获取Review统计报告，数量: {}, 总数: {}", 
                    result.getData() != null ? result.getData().size() : 0, 
                    result.getTotal());
            } else {
                log.error("获取Review统计报告失败: code={}, message={}", result.getCode(), result.getMsg());
                throw new RuntimeException("API返回错误: code=" + result.getCode() + ", message=" + result.getMsg());
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询Review统计报告异常，查询参数: {}", JSONUtil.toJsonStr(queryParams), e);
            throw new RuntimeException("查询Review统计报告失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer getYesterdayReviewCount(String asin, Integer mid) {
        if (StrUtil.isBlank(asin)) {
            throw new IllegalArgumentException("ASIN不能为空");
        }
        
        // 计算昨天的日期
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        log.info("开始获取ASIN [{}] 昨日 [{}] 新增Review数量", asin, yesterdayStr);
        
        // 构建查询参数 - 查询昨天一天的数据
        ReviewReportQueryDTO queryParams = new ReviewReportQueryDTO();
        queryParams.setMid(mid != null ? mid : 1); // 默认美国
        queryParams.setAsin(asin);
        queryParams.setStartDate(yesterdayStr + " 00:00:00");
        queryParams.setEndDate(yesterdayStr + " 23:59:59");
        
        try {
            LingXingApiResponseDTO<List<ReviewReportDTO>> response = getReviewReport(queryParams);
            
            if (response != null && response.getCode() == 0 && response.getData() != null) {
                // 计算昨天的总新增数量
                int totalYesterdayCount = response.getData().stream()
                    .filter(report -> yesterdayStr.equals(report.getReportDate()))
                    .mapToInt(report -> report.getReviewNum() != null ? report.getReviewNum() : 0)
                    .sum();
                
                log.info("ASIN [{}] 昨日新增Review数量: {}", asin, totalYesterdayCount);
                return totalYesterdayCount;
            } else {
                log.error("获取昨日Review数量失败，响应: {}", response);
                return 0;
            }
        } catch (Exception e) {
            log.error("获取ASIN [{}] 昨日Review数量异常", asin, e);
            return 0;
        }
    }

    @Override
    public Integer getTotalReviewCount(String asin, Integer mid, String startDate, String endDate) {
        if (StrUtil.isBlank(asin)) {
            throw new IllegalArgumentException("ASIN不能为空");
        }
        if (StrUtil.isBlank(startDate) || StrUtil.isBlank(endDate)) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        
        log.info("开始获取ASIN [{}] 在时间范围 [{}] 至 [{}] 的总Review数量", asin, startDate, endDate);
        
        // 构建查询参数
        ReviewReportQueryDTO queryParams = new ReviewReportQueryDTO();
        queryParams.setMid(mid != null ? mid : 1); // 默认美国
        queryParams.setAsin(asin);
        queryParams.setStartDate(startDate);
        queryParams.setEndDate(endDate);
        
        try {
            LingXingApiResponseDTO<List<ReviewReportDTO>> response = getReviewReport(queryParams);
            
            if (response != null && response.getCode() == 0 && response.getData() != null) {
                // 计算总新增数量
                int totalCount = response.getData().stream()
                    .mapToInt(report -> report.getReviewNum() != null ? report.getReviewNum() : 0)
                    .sum();
                
                log.info("ASIN [{}] 在时间范围 [{}] 至 [{}] 的总Review数量: {}", asin, startDate, endDate, totalCount);
                return totalCount;
            } else {
                log.error("获取总Review数量失败，响应: {}", response);
                return 0;
            }
        } catch (Exception e) {
            log.error("获取ASIN [{}] 总Review数量异常", asin, e);
            return 0;
        }
    }
} 