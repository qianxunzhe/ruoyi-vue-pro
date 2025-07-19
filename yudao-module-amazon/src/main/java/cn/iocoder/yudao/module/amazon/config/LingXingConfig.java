package cn.iocoder.yudao.module.amazon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 领星API配置
 *
 * @author 芋道源码
 */
@Data
@Component
@ConfigurationProperties(prefix = "amazon.lingxing")
public class LingXingConfig {

    /**
     * API域名
     */
    private String apiDomain = "https://openapi.lingxing.com";

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用Key（与appId相同或由平台分配）
     */
    private String appKey;

    /**
     * 应用Secret
     */
    private String appSecret;

    /**
     * API请求超时时间（秒）
     */
    private Integer timeoutSeconds = 30;

    /**
     * 是否启用领星API
     */
    private Boolean enabled = false;

    // 以下字段用于内存中缓存token信息，不从配置文件读取
    
    /**
     * 访问令牌（运行时获取）
     */
    private transient String accessToken;

    /**
     * 刷新令牌（运行时获取）
     */
    private transient String refreshToken;

    /**
     * 令牌过期时间（毫秒）
     */
    private transient Long tokenExpireTime;
} 