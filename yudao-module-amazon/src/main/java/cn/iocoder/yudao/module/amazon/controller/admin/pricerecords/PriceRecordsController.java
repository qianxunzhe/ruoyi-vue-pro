package cn.iocoder.yudao.module.amazon.controller.admin.pricerecords;

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

import cn.iocoder.yudao.module.amazon.controller.admin.pricerecords.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.pricerecords.PriceRecordsDO;
import cn.iocoder.yudao.module.amazon.service.pricerecords.PriceRecordsService;

@Tag(name = "管理后台 - 亚马逊产品价格记录")
@RestController
@RequestMapping("/amazon/price-records")
@Validated
public class PriceRecordsController {

    @Resource
    private PriceRecordsService priceRecordsService;

    @PostMapping("/create")
    @Operation(summary = "创建亚马逊产品价格记录")
    @PreAuthorize("@ss.hasPermission('amazon:price-records:create')")
    public CommonResult<Long> createPriceRecords(@Valid @RequestBody PriceRecordsSaveReqVO createReqVO) {
        return success(priceRecordsService.createPriceRecords(createReqVO));
    }

    @PostMapping("/batch-save")
    @Operation(summary = "批量保存亚马逊产品价格记录")
    @PreAuthorize("@ss.hasPermission('amazon:price-records:create')")
    public CommonResult<Integer> batchSavePriceRecords(@Valid @RequestBody PriceRecordsBatchSaveReqVO batchSaveReqVO) {
        return success(priceRecordsService.batchSavePriceRecords(batchSaveReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新亚马逊产品价格记录")
    @PreAuthorize("@ss.hasPermission('amazon:price-records:update')")
    public CommonResult<Boolean> updatePriceRecords(@Valid @RequestBody PriceRecordsSaveReqVO updateReqVO) {
        priceRecordsService.updatePriceRecords(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除亚马逊产品价格记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:price-records:delete')")
    public CommonResult<Boolean> deletePriceRecords(@RequestParam("id") Long id) {
        priceRecordsService.deletePriceRecords(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除亚马逊产品价格记录")
                @PreAuthorize("@ss.hasPermission('amazon:price-records:delete')")
    public CommonResult<Boolean> deletePriceRecordsList(@RequestParam("ids") List<Long> ids) {
        priceRecordsService.deletePriceRecordsListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得亚马逊产品价格记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:price-records:query')")
    public CommonResult<PriceRecordsRespVO> getPriceRecords(@RequestParam("id") Long id) {
        PriceRecordsDO priceRecords = priceRecordsService.getPriceRecords(id);
        return success(BeanUtils.toBean(priceRecords, PriceRecordsRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得亚马逊产品价格记录分页")
    @PreAuthorize("@ss.hasPermission('amazon:price-records:query')")
    public CommonResult<PageResult<PriceRecordsRespVO>> getPriceRecordsPage(@Valid PriceRecordsPageReqVO pageReqVO) {
        PageResult<PriceRecordsDO> pageResult = priceRecordsService.getPriceRecordsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PriceRecordsRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出亚马逊产品价格记录 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:price-records:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportPriceRecordsExcel(@Valid PriceRecordsPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PriceRecordsDO> list = priceRecordsService.getPriceRecordsPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "亚马逊产品价格记录.xls", "数据", PriceRecordsRespVO.class,
                        BeanUtils.toBean(list, PriceRecordsRespVO.class));
    }

}