package cn.iocoder.yudao.module.amazon.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;

/**
 * 领星API工具类
 * 主要用于处理公共参数和URL构建
 *
 * @author 芋道源码
 */
@Slf4j
public class LingXingUtils {

    /**
     * 构建公共请求参数
     * 
     * @param accessToken 访问令牌
     * @param appKey 应用Key
     * @return 公共参数Map
     */
    public static Map<String, Object> buildCommonParams(String accessToken, String appKey) {
        Map<String, Object> commonParams = new HashMap<>();
        commonParams.put("access_token", accessToken);
        commonParams.put("app_key", appKey);
        commonParams.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        return commonParams;
    }

    /**
     * 构建GET请求的查询参数字符串
     */
    public static String buildQueryString(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            try {
                sb.append(entry.getKey()).append("=")
                  .append(URLEncoder.encode(String.valueOf(entry.getValue()), StandardCharsets.UTF_8.toString()));
            } catch (Exception e) {
                log.error("参数URL编码失败: {}={}", entry.getKey(), entry.getValue(), e);
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        return sb.toString();
    }
}
