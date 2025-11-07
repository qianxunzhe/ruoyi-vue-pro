package cn.iocoder.yudao.module.amazon.service.feishu;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.amazon.config.FeishuConfig;
import cn.iocoder.yudao.module.amazon.dal.dataobject.feishu.FeishuMessageDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.feishu.FeishuResponseDTO;
import cn.iocoder.yudao.module.amazon.enums.FeishuMessageTypeEnum;
import cn.iocoder.yudao.module.amazon.enums.FeishuSceneEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 飞书通知服务实现
 *
 * @author Claude
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeishuNotificationServiceImpl implements FeishuNotificationService {
    
    private final FeishuConfig feishuConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public FeishuResponseDTO sendTextMessage(String text) {
        return sendTextMessage(text, FeishuSceneEnum.DEFAULT);
    }
    
    @Override
    public FeishuResponseDTO sendTextMessage(String text, FeishuSceneEnum scene) {
        String webhook = feishuConfig.getWebhook(scene.getCode());
        return sendTextMessage(text, webhook);
    }
    
    @Override
    public FeishuResponseDTO sendTextMessage(String text, String webhookUrl) {
        if (!feishuConfig.isEnabled()) {
            log.debug("飞书通知未启用，跳过发送消息: {}", text);
            return createDisabledResponse();
        }
        
        if (StrUtil.isBlank(webhookUrl)) {
            log.warn("飞书webhook地址为空，跳过发送消息: {}", text);
            return createErrorResponse("Webhook地址为空");
        }
        
        FeishuMessageDTO message = FeishuMessageDTO.builder()
                .msgType(FeishuMessageTypeEnum.TEXT.getType())
                .content(FeishuMessageDTO.TextContent.builder()
                        .text(text)
                        .build())
                .build();
        
        return sendMessage(webhookUrl, message);
    }
    
    @Override
    public FeishuResponseDTO sendPostMessage(String title, Object[][] content, FeishuSceneEnum scene) {
        if (!feishuConfig.isEnabled()) {
            log.debug("飞书通知未启用，跳过发送富文本消息");
            return createDisabledResponse();
        }
        
        String webhook = feishuConfig.getWebhook(scene.getCode());
        if (StrUtil.isBlank(webhook)) {
            log.warn("飞书webhook地址为空，跳过发送富文本消息");
            return createErrorResponse("Webhook地址为空");
        }
        
        FeishuMessageDTO message = FeishuMessageDTO.builder()
                .msgType(FeishuMessageTypeEnum.POST.getType())
                .post(FeishuMessageDTO.PostContent.builder()
                        .zhCn(FeishuMessageDTO.PostDetail.builder()
                                .title(title)
                                .content(content)
                                .build())
                        .build())
                .build();
        
        return sendMessage(webhook, message);
    }
    
    @Override
    public FeishuResponseDTO sendCardMessage(Map<String, Object> config, Map<String, Object> header,
                                            Map<String, Object> elements, FeishuSceneEnum scene) {
        if (!feishuConfig.isEnabled()) {
            log.debug("飞书通知未启用，跳过发送卡片消息");
            return createDisabledResponse();
        }
        
        String webhook = feishuConfig.getWebhook(scene.getCode());
        if (StrUtil.isBlank(webhook)) {
            log.warn("飞书webhook地址为空，跳过发送卡片消息");
            return createErrorResponse("Webhook地址为空");
        }
        
        FeishuMessageDTO message = FeishuMessageDTO.builder()
                .msgType(FeishuMessageTypeEnum.INTERACTIVE.getType())
                .card(FeishuMessageDTO.CardContent.builder()
                        .config(config)
                        .header(header)
                        .elements(elements)
                        .build())
                .build();
        
        return sendMessage(webhook, message);
    }
    
    @Override
    public void notifyTaskComplete(String taskName, String result, String details) {
        String message = String.format("✅ 任务完成\\n任务名称：%s\\n执行结果：%s\\n详细信息：%s\\n完成时间：%s",
                taskName, result, details, getCurrentTime());
        sendTextMessage(message, FeishuSceneEnum.TASK_COMPLETE);
    }
    
    @Override
    public void notifyTaskFailed(String taskName, String errorMsg, String stackTrace) {
        String message = String.format("❌ 任务失败\\n任务名称：%s\\n错误信息：%s\\n堆栈信息：%s\\n失败时间：%s",
                taskName, errorMsg, 
                StrUtil.isBlank(stackTrace) ? "无" : StrUtil.maxLength(stackTrace, 500),
                getCurrentTime());
        sendTextMessage(message, FeishuSceneEnum.TASK_FAILED);
    }
    
    @Override
    public void notifyDataSyncSuccess(String dataType, int count, long timeCost) {
        String message = String.format("✅ 数据同步成功\\n数据类型：%s\\n同步数量：%d\\n耗时：%d秒\\n完成时间：%s",
                dataType, count, timeCost, getCurrentTime());
        sendTextMessage(message, FeishuSceneEnum.DATA_SYNC_SUCCESS);
    }
    
    @Override
    public void notifyDataSyncFailed(String dataType, String errorMsg) {
        String message = String.format("❌ 数据同步失败\\n数据类型：%s\\n错误信息：%s\\n失败时间：%s",
                dataType, errorMsg, getCurrentTime());
        sendTextMessage(message, FeishuSceneEnum.DATA_SYNC_FAILED);
    }
    
    @Override
    public void notifyKeywordRankingChange(String keyword, String asin, Integer oldRank, Integer newRank) {
        String trend = "";
        if (oldRank != null && newRank != null) {
            if (newRank < oldRank) {
                trend = "↑ 排名上升";
            } else if (newRank > oldRank) {
                trend = "↓ 排名下降";
            } else {
                trend = "→ 排名不变";
            }
        }
        
        String message = String.format("📊 关键词排名变化 %s\\n关键词：%s\\nASIN：%s\\n原排名：%s\\n新排名：%s\\n变化时间：%s",
                trend, keyword, asin, 
                oldRank != null ? oldRank : "无",
                newRank != null ? newRank : "无",
                getCurrentTime());
        sendTextMessage(message, FeishuSceneEnum.KEYWORD_RANKING_CHANGE);
    }
    
    @Override
    public void notifyPriceChange(String asin, String title, Double oldPrice, Double newPrice) {
        String trend = "";
        Double changeAmount = null;
        Double changePercent = null;
        
        if (oldPrice != null && newPrice != null) {
            changeAmount = newPrice - oldPrice;
            changePercent = (changeAmount / oldPrice) * 100;
            
            if (changeAmount > 0) {
                trend = String.format("↑ 涨价 $%.2f (%.2f%%)", changeAmount, changePercent);
            } else if (changeAmount < 0) {
                trend = String.format("↓ 降价 $%.2f (%.2f%%)", Math.abs(changeAmount), Math.abs(changePercent));
            } else {
                trend = "→ 价格不变";
            }
        }
        
        String message = String.format("💰 价格变动通知\\nASIN：%s\\n商品：%s\\n原价格：$%.2f\\n新价格：$%.2f\\n变动：%s\\n时间：%s",
                asin, StrUtil.maxLength(title, 50), 
                oldPrice != null ? oldPrice : 0.0,
                newPrice != null ? newPrice : 0.0,
                trend, getCurrentTime());
        sendTextMessage(message, FeishuSceneEnum.PRICE_CHANGE);
    }
    
    @Override
    public void notifyReviewAlert(String asin, String title, Integer rating, String reviewContent) {
        String ratingStars = "";
        if (rating != null) {
            ratingStars = "⭐".repeat(Math.max(0, rating));
        }
        
        String message = String.format("⚠️ 评论预警\\nASIN：%s\\n商品：%s\\n评分：%s (%d星)\\n评论内容：%s\\n时间：%s",
                asin, StrUtil.maxLength(title, 50),
                ratingStars, rating != null ? rating : 0,
                StrUtil.maxLength(reviewContent, 200),
                getCurrentTime());
        sendTextMessage(message, FeishuSceneEnum.REVIEW_ALERT);
    }
    
    /**
     * 发送消息到飞书
     */
    private FeishuResponseDTO sendMessage(String webhook, FeishuMessageDTO message) {
        int retryTimes = feishuConfig.getRetryTimes();
        int retryInterval = feishuConfig.getRetryInterval();
        
        for (int i = 0; i <= retryTimes; i++) {
            try {
                // 使用 Jackson ObjectMapper 来序列化，确保 @JsonProperty 注解生效
                String jsonBody = objectMapper.writeValueAsString(message);
                
                HttpResponse response = HttpRequest.post(webhook)
                        .header("Content-Type", "application/json")
                        .body(jsonBody)
                        .timeout(feishuConfig.getTimeout())
                        .execute();
                
                if (response.isOk()) {
                    String body = response.body();
                    FeishuResponseDTO result = objectMapper.readValue(body, FeishuResponseDTO.class);
                    
                    if (result.isSuccess()) {
                        log.info("飞书消息发送成功: {}", jsonBody);
                        return result;
                    } else {
                        log.warn("飞书消息发送失败，响应: {}", body);
                        if (i < retryTimes) {
                            Thread.sleep(retryInterval);
                            continue;
                        }
                    }
                } else {
                    log.error("飞书消息发送失败，HTTP状态码: {}, 响应: {}", 
                            response.getStatus(), response.body());
                    if (i < retryTimes) {
                        Thread.sleep(retryInterval);
                        continue;
                    }
                }
            } catch (Exception e) {
                log.error("飞书消息发送异常", e);
                if (i < retryTimes) {
                    try {
                        Thread.sleep(retryInterval);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue;
                }
            }
        }
        
        return createErrorResponse("消息发送失败，已重试" + retryTimes + "次");
    }
    
    /**
     * 创建禁用响应
     */
    private FeishuResponseDTO createDisabledResponse() {
        FeishuResponseDTO response = new FeishuResponseDTO();
        response.setStatusCode(0);
        response.setStatusMessage("disabled");
        response.setCode(0);
        response.setMsg("飞书通知未启用");
        return response;
    }
    
    /**
     * 创建错误响应
     */
    private FeishuResponseDTO createErrorResponse(String errorMsg) {
        FeishuResponseDTO response = new FeishuResponseDTO();
        response.setStatusCode(-1);
        response.setStatusMessage("error");
        response.setCode(-1);
        response.setMsg(errorMsg);
        return response;
    }
    
    /**
     * 获取当前时间字符串
     */
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}