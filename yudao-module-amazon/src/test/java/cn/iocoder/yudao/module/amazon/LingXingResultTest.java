package cn.iocoder.yudao.module.amazon;

import cn.iocoder.yudao.module.amazon.openapi.entity.Result;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Result类测试
 * 验证手动添加的getter和setter方法
 */
public class LingXingResultTest {

    @Test
    public void testResultGetterSetter() {
        // 创建Result实例
        Result<Map<String, Object>> result = new Result<>();
        
        // 测试code字段
        result.setCode("200");
        assertEquals("200", result.getCode());
        
        // 测试msg字段
        result.setMsg("Success");
        assertEquals("Success", result.getMsg());
        
        // 测试data字段
        Map<String, Object> data = new HashMap<>();
        data.put("access_token", "test_token");
        data.put("refresh_token", "test_refresh");
        data.put("expires_in", 3600);
        
        result.setData(data);
        assertNotNull(result.getData());
        assertEquals(data, result.getData());
        
        // 验证可以正常访问data中的内容
        Map<String, Object> resultData = result.getData();
        assertEquals("test_token", resultData.get("access_token"));
        assertEquals("test_refresh", resultData.get("refresh_token"));
        assertEquals(3600, resultData.get("expires_in"));
        
        // 测试toString方法
        String toStringResult = result.toString();
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("code='200'"));
        assertTrue(toStringResult.contains("Success"));
        
        System.out.println("测试通过！Result类的getter和setter方法工作正常");
        System.out.println("Result toString: " + toStringResult);
    }
} 