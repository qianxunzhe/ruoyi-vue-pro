package cn.iocoder.yudao.module.amazon.controller;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.amazon.config.LingXingConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 领星API测试控制器
 * 用于测试配置和基本功能
 *
 * @author 芋道源码
 */
@Tag(name = "领星API测试", description = "领星API配置和基本功能测试")
@RestController
@RequestMapping("/amazon/lingxing/test")
@RequiredArgsConstructor
@Slf4j
public class LingXingTestController {

    private final LingXingConfig lingXingConfig;

    @Operation(summary = "检查配置", description = "检查领星API的配置信息")
    @GetMapping("/config")
    public CommonResult<Map<String, Object>> checkConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("enabled", lingXingConfig.getEnabled());
        config.put("apiDomain", lingXingConfig.getApiDomain());
        config.put("appId", lingXingConfig.getAppId());
        config.put("appKey", lingXingConfig.getAppKey());
        config.put("appSecret", lingXingConfig.getAppSecret() != null ? "已配置" : "未配置");
        config.put("hasAccessToken", lingXingConfig.getAccessToken() != null);
        config.put("hasRefreshToken", lingXingConfig.getRefreshToken() != null);
        config.put("tokenExpireTime", lingXingConfig.getTokenExpireTime());
        
        return CommonResult.success(config);
    }

    @Operation(summary = "测试连接", description = "测试与领星API的基本连接")
    @GetMapping("/ping")
    public CommonResult<String> testConnection() {
        if (!lingXingConfig.getEnabled()) {
            return CommonResult.error(400, "领星API未启用");
        }
        
        if (lingXingConfig.getAppId() == null || lingXingConfig.getAppKey() == null || lingXingConfig.getAppSecret() == null) {
            return CommonResult.error(400, "领星API配置不完整，请检查app-id、app-key、app-secret");
        }
        
        return CommonResult.success("配置检查通过，可以尝试获取token");
    }
} 