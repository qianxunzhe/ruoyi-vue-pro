package cn.iocoder.yudao.module.amazon.controller;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 领星API控制器
 *
 * @author 芋道源码
 */
@Tag(name = "领星API", description = "领星API接口测试")
@RestController
@RequestMapping("/amazon/lingxing")
@RequiredArgsConstructor
@Slf4j
public class LingXingController {

    private final LingXingApiService lingXingApiService;

    @Operation(summary = "获取访问令牌", description = "通过SDK获取领星API的访问令牌")
    @PostMapping("/token")
    public CommonResult<Map<String, Object>> getAccessToken() {
        try {
            Map<String, Object> result = lingXingApiService.getAccessToken();
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("获取访问令牌失败", e);
            return CommonResult.error(500, "获取访问令牌失败: " + e.getMessage());
        }
    }

    @Operation(summary = "刷新访问令牌", description = "刷新领星API的访问令牌")
    @PostMapping("/token/refresh")
    public CommonResult<Map<String, Object>> refreshAccessToken() {
        try {
            Map<String, Object> result = lingXingApiService.refreshAccessToken();
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("刷新访问令牌失败", e);
            return CommonResult.error(500, "刷新访问令牌失败: " + e.getMessage());
        }
    }

    @Operation(summary = "发送GET请求", description = "向领星API发送GET请求")
    @GetMapping("/api/**")
    public CommonResult<String> sendGetRequest(
            @RequestParam Map<String, Object> params,
            HttpServletRequest request) {
        try {
            // 从请求路径中提取API路径
            String path = request.getRequestURI().replace("/amazon/lingxing/api", "");
            
            String response = lingXingApiService.sendGetRequest(path, params);
            return CommonResult.success(response);
        } catch (Exception e) {
            log.error("GET请求失败", e);
            return CommonResult.error(500, "GET请求失败: " + e.getMessage());
        }
    }

    @Operation(summary = "发送POST请求", description = "向领星API发送POST请求")
    @PostMapping("/api/**")
    public CommonResult<String> sendPostRequest(
            @RequestBody(required = false) Object body,
            HttpServletRequest request) {
        try {
            // 从请求路径中提取API路径
            String path = request.getRequestURI().replace("/amazon/lingxing/api", "");
            
            String response = lingXingApiService.sendPostRequest(path, body);
            return CommonResult.success(response);
        } catch (Exception e) {
            log.error("POST请求失败", e);
            return CommonResult.error(500, "POST请求失败: " + e.getMessage());
        }
    }

    @Operation(summary = "检查令牌状态", description = "检查并刷新访问令牌")
    @GetMapping("/token/check")
    public CommonResult<String> checkToken() {
        try {
            lingXingApiService.checkAndRefreshToken();
            return CommonResult.success("令牌检查完成");
        } catch (Exception e) {
            log.error("令牌检查失败", e);
            return CommonResult.error(500, "令牌检查失败: " + e.getMessage());
        }
    }
} 