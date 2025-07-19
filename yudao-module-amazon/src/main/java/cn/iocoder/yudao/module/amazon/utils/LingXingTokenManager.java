package cn.iocoder.yudao.module.amazon.utils;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 领星Token管理工具类
 * 演示如何使用优化后的LingXingApiService
 *
 * @author 芋道源码
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LingXingTokenManager {

    @Resource
    private LingXingApiService lingXingApiService;

    /**
     * 获取有效的访问令牌
     * 
     * @return 访问令牌，如果获取失败返回null
     */
    public String getValidAccessToken() {
        try {
            Map<String, Object> tokenInfo = lingXingApiService.getAccessToken();
            String accessToken = (String) tokenInfo.get("access_token");
            
            if (StrUtil.isNotBlank(accessToken)) {
                Long expiresIn = (Long) tokenInfo.get("expires_in");
                log.debug("获取到有效访问令牌，剩余有效时间: {}秒", expiresIn);
                return accessToken;
            } else {
                log.warn("获取到的访问令牌为空");
                return null;
            }
        } catch (Exception e) {
            log.error("获取访问令牌失败", e);
            return null;
        }
    }

    /**
     * 强制刷新访问令牌
     * 
     * @return 是否刷新成功
     */
    public boolean forceRefreshToken() {
        try {
            Map<String, Object> tokenInfo = lingXingApiService.refreshAccessToken();
            String accessToken = (String) tokenInfo.get("access_token");
            
            if (StrUtil.isNotBlank(accessToken)) {
                log.info("强制刷新访问令牌成功");
                return true;
            } else {
                log.warn("强制刷新访问令牌失败，返回的token为空");
                return false;
            }
        } catch (Exception e) {
            log.error("强制刷新访问令牌失败", e);
            return false;
        }
    }

    /**
     * 检查并确保token有效
     * 
     * @return 是否有有效的token
     */
    public boolean ensureTokenValid() {
        try {
            lingXingApiService.checkAndRefreshToken();
            String token = getValidAccessToken();
            return StrUtil.isNotBlank(token);
        } catch (Exception e) {
            log.error("检查token有效性失败", e);
            return false;
        }
    }

    /**
     * 获取token详细信息
     * 
     * @return token信息Map
     */
    public Map<String, Object> getTokenInfo() {
        try {
            return lingXingApiService.getAccessToken();
        } catch (Exception e) {
            log.error("获取token详细信息失败", e);
            return null;
        }
    }

    /**
     * 预热token缓存
     * 在应用启动时调用，提前获取token并缓存
     */
    public void warmUpTokenCache() {
        log.info("开始预热领星API token缓存...");
        try {
            String token = getValidAccessToken();
            if (StrUtil.isNotBlank(token)) {
                log.info("token缓存预热成功");
            } else {
                log.warn("token缓存预热失败，未能获取到有效token");
            }
        } catch (Exception e) {
            log.error("token缓存预热异常", e);
        }
    }
} 