package cn.iocoder.yudao.module.amazon.service.feishu;

import cn.iocoder.yudao.module.amazon.config.FeishuConfig;
import cn.iocoder.yudao.module.amazon.dal.dataobject.feishu.FeishuResponseDTO;
import cn.iocoder.yudao.module.amazon.enums.FeishuSceneEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 飞书通知服务测试类
 * 
 * 注意：运行测试前需要配置正确的webhook地址
 * 
 * @author Claude
 */
@SpringBootTest
@TestPropertySource(properties = {
    "amazon.feishu.enabled=true",
    "amazon.feishu.default-webhook=https://open.feishu.cn/open-apis/bot/v2/hook/your-test-webhook"
})
public class FeishuNotificationServiceTest {
    
    @Autowired
    private FeishuNotificationService feishuNotificationService;
    
    @Autowired
    private FeishuConfig feishuConfig;
    
    @BeforeEach
    public void setUp() {
        // 确保测试环境启用了飞书通知
        feishuConfig.setEnabled(true);
    }
    
    /**
     * 测试发送简单文本消息
     * 注意：需要配置正确的webhook才能实际发送
     */
    @Test
    public void testSendTextMessage() {
        String testMessage = "测试消息 - " + LocalDateTime.now();
        FeishuResponseDTO response = feishuNotificationService.sendTextMessage(testMessage);
        
        assertNotNull(response);
        // 如果webhook配置正确且网络正常，应该返回成功
        // 如果webhook是示例地址，会返回失败但不应抛出异常
    }
    
    /**
     * 测试发送到指定场景的文本消息
     */
    @Test
    public void testSendTextMessageWithScene() {
        String testMessage = "任务完成测试 - " + LocalDateTime.now();
        FeishuResponseDTO response = feishuNotificationService.sendTextMessage(
            testMessage, 
            FeishuSceneEnum.TASK_COMPLETE
        );
        
        assertNotNull(response);
    }
    
    /**
     * 测试任务完成通知
     */
    @Test
    public void testNotifyTaskComplete() {
        feishuNotificationService.notifyTaskComplete(
            "测试任务",
            "执行成功",
            "处理了100条数据，耗时5秒"
        );
        // 验证方法能正常执行，不抛出异常即可
    }
    
    /**
     * 测试任务失败通知
     */
    @Test
    public void testNotifyTaskFailed() {
        feishuNotificationService.notifyTaskFailed(
            "测试任务",
            "连接超时",
            "java.net.SocketTimeoutException: Read timed out"
        );
    }
    
    /**
     * 测试数据同步成功通知
     */
    @Test
    public void testNotifyDataSyncSuccess() {
        feishuNotificationService.notifyDataSyncSuccess(
            "产品数据",
            500,
            30
        );
    }
    
    /**
     * 测试数据同步失败通知
     */
    @Test
    public void testNotifyDataSyncFailed() {
        feishuNotificationService.notifyDataSyncFailed(
            "订单数据",
            "API调用次数超限"
        );
    }
    
    /**
     * 测试关键词排名变化通知
     */
    @Test
    public void testNotifyKeywordRankingChange() {
        // 测试排名上升
        feishuNotificationService.notifyKeywordRankingChange(
            "wireless charger",
            "B08ABC123",
            15,
            8
        );
        
        // 测试排名下降
        feishuNotificationService.notifyKeywordRankingChange(
            "phone case",
            "B08DEF456",
            5,
            12
        );
    }
    
    /**
     * 测试价格变动通知
     */
    @Test
    public void testNotifyPriceChange() {
        // 测试降价
        feishuNotificationService.notifyPriceChange(
            "B08ABC123",
            "Wireless Charger 15W Fast Charging",
            29.99,
            19.99
        );
        
        // 测试涨价
        feishuNotificationService.notifyPriceChange(
            "B08DEF456",
            "Phone Case Premium",
            15.99,
            18.99
        );
    }
    
    /**
     * 测试评论预警通知
     */
    @Test
    public void testNotifyReviewAlert() {
        feishuNotificationService.notifyReviewAlert(
            "B08ABC123",
            "Wireless Charger 15W",
            1,
            "产品充电很慢，而且发热严重，非常失望！"
        );
    }
    
    /**
     * 测试发送富文本消息
     */
    @Test
    public void testSendPostMessage() {
        Object[][] content = new Object[][]{
            {new Object[]{"tag", "text", "text", "测试富文本消息第一行"}},
            {new Object[]{"tag", "text", "text", "第二行内容", "style", new String[]{"bold"}}},
            {new Object[]{"tag", "a", "text", "点击查看详情", "href", "https://example.com"}}
        };
        
        FeishuResponseDTO response = feishuNotificationService.sendPostMessage(
            "富文本消息测试",
            content,
            FeishuSceneEnum.DEFAULT
        );
        
        assertNotNull(response);
    }
    
    /**
     * 测试禁用状态下的消息发送
     */
    @Test
    public void testSendMessageWhenDisabled() {
        // 临时禁用飞书通知
        feishuConfig.setEnabled(false);
        
        FeishuResponseDTO response = feishuNotificationService.sendTextMessage("测试消息");
        
        assertNotNull(response);
        assertEquals(0, response.getStatusCode());
        assertEquals("飞书通知未启用", response.getMsg());
        
        // 恢复启用状态
        feishuConfig.setEnabled(true);
    }
    
    /**
     * 测试空webhook的处理
     */
    @Test
    public void testSendMessageWithEmptyWebhook() {
        FeishuResponseDTO response = feishuNotificationService.sendTextMessage("测试消息", "");
        
        assertNotNull(response);
        assertEquals(-1, response.getStatusCode());
        assertEquals("Webhook地址为空", response.getMsg());
    }
}