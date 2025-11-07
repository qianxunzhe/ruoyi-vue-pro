package cn.iocoder.yudao.module.amazon.controller.admin.productperformance;

import cn.hutool.core.util.NumberUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo.ProductPerformancePageReqVO;
import cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo.ProductPerformanceRespVO;
import cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo.ProductPerformanceSyncReqVO;
import cn.iocoder.yudao.module.amazon.controller.admin.shops.vo.ShopsPageReqVO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.productperformance.ProductPerformanceDO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.shops.ShopsDO;
import cn.iocoder.yudao.module.amazon.service.productperformance.ProductPerformanceService;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.FbaAgeListQueryDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.FbaAgeListResponseDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.LingXingApiResponseDTO;
import cn.iocoder.yudao.module.amazon.service.shops.ShopsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Slf4j
@Tag(name = "管理后台 - 亚马逊产品表现")
@RestController
@RequestMapping("/amazon/product-performance")
@Validated
public class ProductPerformanceController {

    @Resource
    private ProductPerformanceService productPerformanceService;

    @Resource
    private LingXingApiService lingXingApiService;

    @Resource
    private ShopsService shopsService;

    @Resource
    private cn.iocoder.yudao.framework.security.core.service.SecurityFrameworkService securityFrameworkService;

    @GetMapping("/page")
    @Operation(summary = "获得亚马逊产品表现分页")
    @PreAuthorize("@ss.hasPermission('amazon:product-performance:query')")
    public CommonResult<PageResult<ProductPerformanceRespVO>> getProductPerformancePage(@Valid ProductPerformancePageReqVO pageVO) {
        // 判断是否是运营角色，如果是则限制只能查看自己负责的店铺数据
        if (securityFrameworkService.hasRole("yunying")) {
            Long userId = SecurityFrameworkUtils.getLoginUserId();

            // 获取当前用户负责的店铺列表
            ShopsPageReqVO shopsPageReqVO = new ShopsPageReqVO();
            shopsPageReqVO.setUserId(userId);
            PageResult<ShopsDO> shopsPageResult = shopsService.getShopsPage(shopsPageReqVO);

            // 提取店铺ID列表
            List<Long> shopIds = new ArrayList<>();
            shopsPageResult.getList().forEach(shopsDO -> shopIds.add(shopsDO.getSid().longValue()));

            // 设置店铺ID过滤条件
            pageVO.setSids(shopIds);
        }

        PageResult<ProductPerformanceDO> pageResult = productPerformanceService.getProductPerformancePage(pageVO);
        return success(BeanUtils.toBean(pageResult, ProductPerformanceRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得亚马逊产品表现")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:product-performance:query')")
    public CommonResult<ProductPerformanceRespVO> getProductPerformance(@RequestParam("id") Long id) {
        ProductPerformanceDO productPerformance = productPerformanceService.getProductPerformance(id);
        return success(BeanUtils.toBean(productPerformance, ProductPerformanceRespVO.class));
    }

    @PostMapping("/sync")
    @Operation(summary = "同步亚马逊产品表现数据")
    @PreAuthorize("@ss.hasPermission('amazon:product-performance:sync')")
    public CommonResult<String> syncProductPerformance(@Valid @RequestBody ProductPerformanceSyncReqVO syncReqVO) {
        String result = productPerformanceService.syncProductPerformance(syncReqVO);
        return success(result);
    }

    @PostMapping("/sync-all")
    @Operation(summary = "同步店铺所有产品表现数据")
    @PreAuthorize("@ss.hasPermission('amazon:product-performance:sync')")
    public CommonResult<String> syncAllProductPerformance(@Valid @RequestBody ProductPerformanceSyncReqVO syncReqVO) {
        // 清空ASIN列表，表示同步所有产品
        syncReqVO.setAsins(null);

        String result = productPerformanceService.syncProductPerformance(syncReqVO);
        return success(result);
    }
    
    @PostMapping("/init-sku-spu-cache")
    @Operation(summary = "初始化SKU-SPU缓存")
    @PreAuthorize("@ss.hasPermission('amazon:product-performance:sync')")
    public CommonResult<String> initSkuSpuCache() {
        log.info("手动初始化SKU-SPU缓存");
        lingXingApiService.refreshSkuSpuCache();
        return success("SKU-SPU缓存初始化成功");
    }
    
    @PostMapping("/test-fba-age-list")
    @Operation(summary = "测试库存库龄API接口")
    @PreAuthorize("@ss.hasPermission('amazon:product-performance:query')")
    public CommonResult<LingXingApiResponseDTO<FbaAgeListResponseDTO>> testFbaAgeList(@Valid @RequestBody FbaAgeListQueryDTO queryParams) {
        log.info("测试库存库龄API接口，请求参数: sid={}, offset={}, length={}", 
            queryParams.getSid(), queryParams.getOffset(), queryParams.getLength());
        
        // 如果没有提供分页参数，设置默认值
        if (queryParams.getOffset() == null) {
            queryParams.setOffset(0);
        }
        if (queryParams.getLength() == null) {
            queryParams.setLength(20);
        }
        
        // 调用领星API服务获取库存库龄数据
        LingXingApiResponseDTO<FbaAgeListResponseDTO> response = lingXingApiService.getFbaAgeList(queryParams);
        
        // 记录响应信息
        if (response.isSuccess()) {
            log.info("库存库龄API测试成功，返回数据总数: {}", 
                response.getData() != null && response.getData().getTotal() != null ? response.getData().getTotal() : 0);
            
            // 打印返回的数据结构（用于验证字段）
            if (response.getData() != null && response.getData().getList() != null && !response.getData().getList().isEmpty()) {
                log.info("返回数据条数: {}", response.getData().getList().size());
                log.info("第一条数据示例: {}", response.getData().getList().get(0));
            }
        } else {
            log.error("库存库龄API测试失败，错误码: {}, 错误信息: {}", response.getCode(), response.getMsg());
        }
        
        return success(response);
    }
}