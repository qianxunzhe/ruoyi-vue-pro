package cn.iocoder.yudao.module.amazon.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.LingXingApiResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * 领星API JSON解析工具类
 * 用于处理API响应的容错解析
 *
 * @author 芋道源码
 */
@Slf4j
public class LingXingJsonUtils {

    /**
     * 配置为容忍未知字段的ObjectMapper
     */
    private static final ObjectMapper TOLERANT_MAPPER = new ObjectMapper();

    static {
        // 配置忽略未知字段
        TOLERANT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 配置忽略空字符串转换为null
        TOLERANT_MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    }

    /**
     * 容错解析领星API响应
     * 
     * @param jsonString JSON字符串
     * @param typeReference 类型引用
     * @param <T> 泛型类型
     * @return 解析结果
     */
    public static <T> LingXingApiResponseDTO<T> parseResponse(String jsonString, TypeReference<LingXingApiResponseDTO<T>> typeReference) {
        try {
            // 首先尝试直接解析
            return TOLERANT_MAPPER.readValue(jsonString, typeReference);
        } catch (Exception e) {
            log.warn("直接解析失败，尝试容错解析: {}", e.getMessage());
            return parseResponseWithFallback(jsonString, typeReference);
        }
    }

    /**
     * 容错解析的回退方案
     */
    private static <T> LingXingApiResponseDTO<T> parseResponseWithFallback(String jsonString, TypeReference<LingXingApiResponseDTO<T>> typeReference) {
        try {
            // 使用Hutool JSON先预处理
            JSONObject jsonObject = JSONUtil.parseObj(jsonString);
            
            // 处理字段名不一致的问题
            normalizeFieldNames(jsonObject);
            
            // 再次尝试解析
            String normalizedJson = jsonObject.toString();
            log.debug("标准化后的JSON: {}", normalizedJson);
            
            return TOLERANT_MAPPER.readValue(normalizedJson, typeReference);
        } catch (Exception e) {
            log.error("容错解析也失败了: {}", e.getMessage());
            log.error("原始JSON: {}", jsonString);
            throw new RuntimeException("JSON解析失败", e);
        }
    }

    /**
     * 标准化字段名
     */
    private static void normalizeFieldNames(JSONObject jsonObject) {
        // 处理 message 字段名不一致问题
        if (jsonObject.containsKey("message") && !jsonObject.containsKey("msg")) {
            jsonObject.set("msg", jsonObject.get("message"));
            jsonObject.remove("message");
            log.debug("将 'message' 字段重命名为 'msg'");
        }
        
        // 可以在这里添加其他字段名标准化逻辑
        // 例如：error_message -> error_details 等
    }

    /**
     * 安全解析，返回JsonNode用于调试
     */
    public static JsonNode parseToJsonNode(String jsonString) {
        try {
            return TOLERANT_MAPPER.readTree(jsonString);
        } catch (Exception e) {
            log.error("解析为JsonNode失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取容忍未知字段的ObjectMapper实例
     */
    public static ObjectMapper getTolerantMapper() {
        return TOLERANT_MAPPER;
    }
} 