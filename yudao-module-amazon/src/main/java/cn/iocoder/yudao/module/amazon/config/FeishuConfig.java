package cn.iocoder.yudao.module.amazon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 飞书机器人配置
 * 
 * @author Claude
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "amazon.feishu")
public class FeishuConfig {
    
    /**
     * 是否启用飞书通知
     */
    private boolean enabled = false;
    
    /**
     * 默认 webhook 地址
     */
    private String defaultWebhook;
    
    /**
     * 不同场景的 webhook 地址
     * key: 场景名称（如 task-complete、error-notification）
     * value: webhook 地址
     */
    private Map<String, String> webhooks;
    
    /**
     * 超时时间（毫秒）
     */
    private int timeout = 5000;
    
    /**
     * 重试次数
     */
    private int retryTimes = 3;
    
    /**
     * 重试间隔（毫秒）
     */
    private int retryInterval = 1000;
    
    /**
     * 获取指定场景的 webhook，如果没有配置则返回默认值
     */
    public String getWebhook(String scene) {
        if (webhooks != null && webhooks.containsKey(scene)) {
            return webhooks.get(scene);
        }
        return defaultWebhook;
    }
}