package cn.iocoder.yudao.module.amazon.service.feishu;

import cn.iocoder.yudao.module.amazon.dal.dataobject.feishu.FeishuResponseDTO;
import cn.iocoder.yudao.module.amazon.enums.FeishuSceneEnum;

import java.util.Map;

/**
 * 飞书通知服务接口
 *
 * @author Claude
 */
public interface FeishuNotificationService {
    
    /**
     * 发送文本消息
     *
     * @param text 文本内容
     * @return 发送结果
     */
    FeishuResponseDTO sendTextMessage(String text);
    
    /**
     * 发送文本消息到指定场景
     *
     * @param text 文本内容
     * @param scene 场景
     * @return 发送结果
     */
    FeishuResponseDTO sendTextMessage(String text, FeishuSceneEnum scene);
    
    /**
     * 发送文本消息到指定webhook
     *
     * @param text 文本内容
     * @param webhookUrl webhook地址
     * @return 发送结果
     */
    FeishuResponseDTO sendTextMessage(String text, String webhookUrl);
    
    /**
     * 发送富文本消息
     *
     * @param title 标题
     * @param content 内容（二维数组格式）
     * @param scene 场景
     * @return 发送结果
     */
    FeishuResponseDTO sendPostMessage(String title, Object[][] content, FeishuSceneEnum scene);
    
    /**
     * 发送卡片消息
     *
     * @param config 配置
     * @param header 头部
     * @param elements 元素
     * @param scene 场景
     * @return 发送结果
     */
    FeishuResponseDTO sendCardMessage(Map<String, Object> config, Map<String, Object> header, 
                                      Map<String, Object> elements, FeishuSceneEnum scene);
    
    /**
     * 发送任务完成通知
     *
     * @param taskName 任务名称
     * @param result 结果描述
     * @param details 详细信息
     */
    void notifyTaskComplete(String taskName, String result, String details);
    
    /**
     * 发送任务失败通知
     *
     * @param taskName 任务名称
     * @param errorMsg 错误信息
     * @param stackTrace 堆栈信息
     */
    void notifyTaskFailed(String taskName, String errorMsg, String stackTrace);
    
    /**
     * 发送数据同步成功通知
     *
     * @param dataType 数据类型
     * @param count 同步数量
     * @param timeCost 耗时（秒）
     */
    void notifyDataSyncSuccess(String dataType, int count, long timeCost);
    
    /**
     * 发送数据同步失败通知
     *
     * @param dataType 数据类型
     * @param errorMsg 错误信息
     */
    void notifyDataSyncFailed(String dataType, String errorMsg);
    
    /**
     * 发送关键词排名变化通知
     *
     * @param keyword 关键词
     * @param asin ASIN
     * @param oldRank 旧排名
     * @param newRank 新排名
     */
    void notifyKeywordRankingChange(String keyword, String asin, Integer oldRank, Integer newRank);
    
    /**
     * 发送价格变动通知
     *
     * @param asin ASIN
     * @param title 商品标题
     * @param oldPrice 旧价格
     * @param newPrice 新价格
     */
    void notifyPriceChange(String asin, String title, Double oldPrice, Double newPrice);
    
    /**
     * 发送评论预警通知
     *
     * @param asin ASIN
     * @param title 商品标题
     * @param rating 评分
     * @param reviewContent 评论内容
     */
    void notifyReviewAlert(String asin, String title, Integer rating, String reviewContent);
}