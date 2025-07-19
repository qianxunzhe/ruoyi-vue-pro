package cn.iocoder.yudao.module.amazon.controller.admin.test;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.module.amazon.controller.admin.test.vo.AmazonListingVO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonListingDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonReviewQueryDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.LingXingApiResponseDTO;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import cn.iocoder.yudao.module.amazon.utils.LingXingTokenManager;
import cn.iocoder.yudao.module.amazon.utils.LingXingJsonUtils;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.amazon.controller.admin.listingprice.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.listingprice.ListingPriceDO;
import cn.iocoder.yudao.module.amazon.service.listingprice.ListingPriceService;

@Slf4j
@Tag(name = "管理后台 - 领星API测试（优化版）")
@RestController
@RequestMapping("/amazon/test")
public class TestController {

    @Resource
    private LingXingApiService lingXingApiService;
    
    @Resource
    private LingXingTokenManager tokenManager;

    @PermitAll
    @GetMapping("/list")
    @Operation(summary = "获取美国正常店铺sid列表", description = "使用优化后的缓存机制")
    public CommonResult getUSNormalStoreSids() {
      List<Long> sidList = List.of(
                9366L, 4970L, 10045L, 10049L, 10046L, 4975L,
                4980L, 4982L, 10493L, 4986L, 4983L, 4965L,
                4977L, 4972L, 9100L, 4985L, 10565L, 4966L,
                9087L, 4976L, 10549L
        );
//        List<String> asinList = Arrays.asList(
//            "B08NP4KYFT", "B0C32NTNCP","B0F3D6DL6Q","B0F3D3TW43","B0F3D14K7N","B0F3D3MFR1",
//            "B0F3D4J8WH","B0F3D5QJ3Y","B0F3D6SWHR","B0F3D2ZF8M","B0F3D3CTP5","B0F3D46WQY",
//            "B0F3D6TQFW","B0F3D58KW4","B0FDBH34HQ","B0FDBD5RS8"
//        );
        String asin = "B08WWQWNQD";
//        Integer count = lingXingApiService.getAsinReviewCount(
//                "B07XKHF683",
//                "2023-10-01",
//                "2025-07-10"
//        );

        // 更精细的查询控制
        AmazonReviewQueryDTO query = new AmazonReviewQueryDTO();
        query.setStartDate("2023-10-01");
        query.setEndDate("2025-07-10");
//        query.setStar("1,2");  // 只查1-2星差评
        Integer badReviewCount = lingXingApiService.getAsinReviewCount(asin, query);
        log.info("%s 评论数据为 %d", asin, badReviewCount);
//        List<AmazonListingVO> resultList = BeanUtils.toBean(resList, AmazonListingVO.class);

        return success(badReviewCount);
    }

    @PermitAll
    @GetMapping("/token/cache-test")
    @Operation(summary = "缓存效果测试", description = "测试token缓存的性能提升")
    public CommonResult testTokenCache() {
        Map<String, Object> result = new HashMap<>();
        
        // 测试连续获取token的性能
        long startTime = System.currentTimeMillis();
        
        // 第一次获取（可能需要从API获取）
        long time1 = System.currentTimeMillis();
        String token1 = tokenManager.getValidAccessToken();
        long duration1 = System.currentTimeMillis() - time1;
        
        // 第二次获取（应该从缓存获取）
        long time2 = System.currentTimeMillis();
        String token2 = tokenManager.getValidAccessToken();
        long duration2 = System.currentTimeMillis() - time2;
        
        // 第三次获取（应该从缓存获取）
        long time3 = System.currentTimeMillis();
        String token3 = tokenManager.getValidAccessToken();
        long duration3 = System.currentTimeMillis() - time3;
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        result.put("第一次获取耗时(ms)", duration1);
        result.put("第二次获取耗时(ms)", duration2);
        result.put("第三次获取耗时(ms)", duration3);
        result.put("总耗时(ms)", totalTime);
        result.put("缓存是否生效", duration2 < duration1 && duration3 < duration1);
        result.put("token一致性", Objects.equals(token1, token2) && Objects.equals(token2, token3));
        
        // 获取token详细信息
        Map<String, Object> tokenInfo = tokenManager.getTokenInfo();
        if (tokenInfo != null) {
            result.put("token信息", tokenInfo);
        }
        
        return success(result);
    }

    @PermitAll
    @PostMapping("/token/warm-up")
    @Operation(summary = "预热token缓存", description = "预热缓存，提升后续访问性能")
    public CommonResult warmUpCache() {
        long startTime = System.currentTimeMillis();
        
        tokenManager.warmUpTokenCache();
        
        long duration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("预热耗时(ms)", duration);
        result.put("预热结果", "成功");
        
        return success(result);
    }

    @PermitAll
    @PostMapping("/token/force-refresh")
    @Operation(summary = "强制刷新token", description = "强制刷新访问令牌并更新缓存")
    public CommonResult forceRefreshToken() {
        long startTime = System.currentTimeMillis();
        
        boolean success = tokenManager.forceRefreshToken();
        
        long duration = System.currentTimeMillis() - startTime;
        
        Map<String, Object> result = new HashMap<>();
        result.put("刷新耗时(ms)", duration);
        result.put("刷新结果", success ? "成功" : "失败");
        
        if (success) {
            Map<String, Object> tokenInfo = tokenManager.getTokenInfo();
            result.put("新token信息", tokenInfo);
        }
        
        return success(result);
    }

    @PermitAll
    @GetMapping("/token/status")
    @Operation(summary = "检查token状态", description = "检查当前token的有效性和缓存状态")
    public CommonResult checkTokenStatus() {
        Map<String, Object> result = new HashMap<>();
        
        // 检查token有效性
        boolean isValid = tokenManager.ensureTokenValid();
        result.put("token有效性", isValid);
        
        // 获取token信息
        Map<String, Object> tokenInfo = tokenManager.getTokenInfo();
        if (tokenInfo != null) {
            result.put("access_token", tokenInfo.get("access_token") != null ? "已获取" : "未获取");
            result.put("refresh_token", tokenInfo.get("refresh_token") != null ? "已获取" : "未获取");
            result.put("expires_in", tokenInfo.get("expires_in"));
        }
        
        return success(result);
    }

    @PermitAll
    @GetMapping("/performance-comparison")
    @Operation(summary = "性能对比测试", description = "对比缓存前后的性能差异")
    public CommonResult performanceComparison() {
        Map<String, Object> result = new HashMap<>();
        
        // 测试多次连续调用的性能
        int testCount = 10;
        List<Long> durations = new ArrayList<>();
        
        long totalStartTime = System.currentTimeMillis();
        
        for (int i = 0; i < testCount; i++) {
            long startTime = System.currentTimeMillis();
            String token = tokenManager.getValidAccessToken();
            long duration = System.currentTimeMillis() - startTime;
            durations.add(duration);
        }
        
        long totalTime = System.currentTimeMillis() - totalStartTime;
        
        // 计算统计信息
        long minTime = durations.stream().mapToLong(Long::longValue).min().orElse(0);
        long maxTime = durations.stream().mapToLong(Long::longValue).max().orElse(0);
        double avgTime = durations.stream().mapToLong(Long::longValue).average().orElse(0);
        
        result.put("测试次数", testCount);
        result.put("总耗时(ms)", totalTime);
        result.put("平均耗时(ms)", String.format("%.2f", avgTime));
        result.put("最小耗时(ms)", minTime);
        result.put("最大耗时(ms)", maxTime);
        result.put("每次耗时详情(ms)", durations);
        result.put("缓存效果", maxTime > 100 && minTime < 10 ? "显著" : "一般");
        
        return success(result);
    }
}