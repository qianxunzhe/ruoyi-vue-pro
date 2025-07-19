package cn.iocoder.yudao.module.amazon.controller.admin.listingprice;

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

@Tag(name = "管理后台 - 价格监控结果")
@RestController
@RequestMapping("/amazon/listing-price")
@Validated
public class ListingPriceController {

    @Resource
    private ListingPriceService listingPriceService;

    @PostMapping("/create")
    @Operation(summary = "创建价格监控结果")
    @PreAuthorize("@ss.hasPermission('amazon:listing-price:create')")
    public CommonResult<Long> createListingPrice(@Valid @RequestBody ListingPriceSaveReqVO createReqVO) {
        return success(listingPriceService.createListingPrice(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新价格监控结果")
    @PreAuthorize("@ss.hasPermission('amazon:listing-price:update')")
    public CommonResult<Boolean> updateListingPrice(@Valid @RequestBody ListingPriceSaveReqVO updateReqVO) {
        listingPriceService.updateListingPrice(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除价格监控结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:listing-price:delete')")
    public CommonResult<Boolean> deleteListingPrice(@RequestParam("id") Long id) {
        listingPriceService.deleteListingPrice(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除价格监控结果")
                @PreAuthorize("@ss.hasPermission('amazon:listing-price:delete')")
    public CommonResult<Boolean> deleteListingPriceList(@RequestParam("ids") List<Long> ids) {
        listingPriceService.deleteListingPriceListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得价格监控结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:listing-price:query')")
    public CommonResult<ListingPriceRespVO> getListingPrice(@RequestParam("id") Long id) {
        ListingPriceDO listingPrice = listingPriceService.getListingPrice(id);
        return success(BeanUtils.toBean(listingPrice, ListingPriceRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得价格监控结果分页")
    @PreAuthorize("@ss.hasPermission('amazon:listing-price:query')")
    public CommonResult<PageResult<ListingPriceRespVO>> getListingPricePage(@Valid ListingPricePageReqVO pageReqVO) {
        PageResult<ListingPriceDO> pageResult = listingPriceService.getListingPricePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ListingPriceRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出价格监控结果 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:listing-price:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportListingPriceExcel(@Valid ListingPricePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ListingPriceDO> list = listingPriceService.getListingPricePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "价格监控结果.xls", "数据", ListingPriceRespVO.class,
                        BeanUtils.toBean(list, ListingPriceRespVO.class));
    }

    @PostMapping("/analysis")
    @Operation(summary = "获取价格分析数据")
    @PreAuthorize("@ss.hasPermission('amazon:listing-price:query')")
    public CommonResult<PriceAnalysisRespVO> getPriceAnalysis(@Valid @RequestBody PriceAnalysisReqVO reqVO) {
        return success(listingPriceService.getPriceAnalysis(reqVO));
    }

}