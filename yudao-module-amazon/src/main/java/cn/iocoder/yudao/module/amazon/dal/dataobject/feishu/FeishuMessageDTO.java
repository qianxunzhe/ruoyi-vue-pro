package cn.iocoder.yudao.module.amazon.dal.dataobject.feishu;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 飞书消息DTO
 *
 * @author Claude
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeishuMessageDTO {
    
    /**
     * 消息类型
     */
    @JsonProperty("msg_type")
    private String msgType;
    
    /**
     * 文本消息内容
     */
    private TextContent content;
    
    /**
     * 富文本消息内容
     */
    private PostContent post;
    
    /**
     * 卡片消息
     */
    private CardContent card;
    
    /**
     * 文本消息内容
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TextContent {
        /**
         * 文本内容
         */
        private String text;
    }
    
    /**
     * 富文本消息内容
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostContent {
        /**
         * 标题
         */
        @JsonProperty("zh_cn")
        private PostDetail zhCn;
    }
    
    /**
     * 富文本详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostDetail {
        /**
         * 标题
         */
        private String title;
        
        /**
         * 内容
         */
        private Object[][] content;
    }
    
    /**
     * 卡片消息内容
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardContent {
        /**
         * 配置
         */
        private Map<String, Object> config;
        
        /**
         * 元素
         */
        private Map<String, Object> elements;
        
        /**
         * 头部
         */
        private Map<String, Object> header;
    }
}