package cn.iocoder.yudao.module.amazon.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 飞书消息类型枚举
 *
 * @author Claude
 */
@Getter
@AllArgsConstructor
public enum FeishuMessageTypeEnum {
    
    TEXT("text", "文本消息"),
    POST("post", "富文本消息"),
    IMAGE("image", "图片消息"),
    INTERACTIVE("interactive", "卡片消息");
    
    private final String type;
    private final String desc;
}