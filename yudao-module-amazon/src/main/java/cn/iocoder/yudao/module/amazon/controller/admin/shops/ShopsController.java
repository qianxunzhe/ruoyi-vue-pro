package cn.iocoder.yudao.module.amazon.controller.admin.shops;

import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
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

import cn.iocoder.yudao.module.amazon.controller.admin.shops.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.shops.ShopsDO;
import cn.iocoder.yudao.module.amazon.service.shops.ShopsService;

@Slf4j
@Tag(name = "管理后台 - 亚马逊店铺信息")
@RestController
@RequestMapping("/amazon/shops")
@Validated
public class ShopsController {

    @Resource
    private ShopsService shopsService;

    @PostMapping("/create")
    @Operation(summary = "创建亚马逊店铺信息")
    @PreAuthorize("@ss.hasPermission('amazon:shops:create')")
    public CommonResult<Integer> createShops(@Valid @RequestBody ShopsSaveReqVO createReqVO) {
        return success(shopsService.createShops(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新亚马逊店铺信息")
    @PreAuthorize("@ss.hasPermission('amazon:shops:update')")
    public CommonResult<Boolean> updateShops(@Valid @RequestBody ShopsSaveReqVO updateReqVO) {
        shopsService.updateShops(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除亚马逊店铺信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:shops:delete')")
    public CommonResult<Boolean> deleteShops(@RequestParam("id") Integer id) {
        shopsService.deleteShops(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除亚马逊店铺信息")
                @PreAuthorize("@ss.hasPermission('amazon:shops:delete')")
    public CommonResult<Boolean> deleteShopsList(@RequestParam("ids") List<Integer> ids) {
        shopsService.deleteShopsListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得亚马逊店铺信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:shops:query')")
    public CommonResult<ShopsRespVO> getShops(@RequestParam("id") Integer id) {
        ShopsDO shops = shopsService.getShops(id);
        return success(BeanUtils.toBean(shops, ShopsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得亚马逊店铺信息分页")
    @PreAuthorize("@ss.hasPermission('amazon:shops:query')")
    public CommonResult<PageResult<ShopsRespVO>> getShopsPage(@Valid ShopsPageReqVO pageReqVO) {


        PageResult<ShopsDO> pageResult = shopsService.getShopsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ShopsRespVO.class));
    }



    @GetMapping("/export-excel")
    @Operation(summary = "导出亚马逊店铺信息 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:shops:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportShopsExcel(@Valid ShopsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ShopsDO> list = shopsService.getShopsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "亚马逊店铺信息.xls", "数据", ShopsRespVO.class,
                        BeanUtils.toBean(list, ShopsRespVO.class));
    }

    @PostMapping("/sync-lingxing")
    @Operation(summary = "同步领星店铺数据")
    @PreAuthorize("@ss.hasPermission('amazon:shops:sync')")
    @ApiAccessLog(operateType = CREATE)
    public CommonResult<Map<String, Object>> syncShopsFromLingXing() {
        int newCount = shopsService.syncShopsFromLingXing();
        Map<String, Object> result = new HashMap<>();
        result.put("newCount", newCount);
        result.put("message", "同步完成，新增 " + newCount + " 个店铺");
        return success(result);
    }

    @PutMapping("/assign-owner")
    @Operation(summary = "分配店铺负责人")
    @PreAuthorize("@ss.hasPermission('amazon:shops:update')")
    public CommonResult<Boolean> assignShopOwner(@Valid @RequestBody ShopsAssignOwnerReqVO reqVO) {
        // 优先使用新的userIds字段
        if (reqVO.getUserIds() != null && !reqVO.getUserIds().isEmpty()) {
            shopsService.assignShopOwners(reqVO.getSid(), reqVO.getUserIds());
        } else if (reqVO.getUserId() != null) {
            // 兼容旧的userId字段
            shopsService.assignShopOwner(reqVO.getSid(), reqVO.getUserId());
        } else {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return success(true);
    }

    @PutMapping("/update-sync-flag")
    @Operation(summary = "更新店铺同步标记")
    @PreAuthorize("@ss.hasPermission('amazon:shops:update')")
    public CommonResult<Boolean> updateShopSyncFlag(@Valid @RequestBody ShopsUpdateSyncFlagReqVO reqVO) {
        shopsService.updateShopSyncFlag(reqVO.getSid(), reqVO.getSyncFlag());
        return success(true);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新店铺状态")
    @PreAuthorize("@ss.hasPermission('amazon:shops:update')")
    public CommonResult<Boolean> updateShopStatus(@Valid @RequestBody ShopsUpdateStatusReqVO reqVO) {
        shopsService.updateShopStatus(reqVO.getSid(), reqVO.getStatus());
        return success(true);
    }

}