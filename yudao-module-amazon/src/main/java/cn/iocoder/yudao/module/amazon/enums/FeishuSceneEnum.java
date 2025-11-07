package cn.iocoder.yudao.module.amazon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 飞书通知场景枚举
 *
 * @author Claude
 */
@Getter
@AllArgsConstructor
public enum FeishuSceneEnum {
    
    DEFAULT("default", "默认通知"),
    TASK_START("task-start", "任务开始"),
    TASK_COMPLETE("task-complete", "任务完成"),
    TASK_FAILED("task-failed", "任务失败"),
    ERROR_NOTIFICATION("error-notification", "错误通知"),
    DATA_SYNC_SUCCESS("data-sync-success", "数据同步成功"),
    DATA_SYNC_FAILED("data-sync-failed", "数据同步失败"),
    KEYWORD_RANKING_CHANGE("keyword-ranking-change", "关键词排名变化"),
    PRICE_CHANGE("price-change", "价格变动通知"),
    REVIEW_ALERT("review-alert", "评论预警"),
    PROFIT_REPORT("profit-report", "利润报表通知");
    
    private final String code;
    private final String desc;
}