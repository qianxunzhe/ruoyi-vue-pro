package cn.iocoder.yudao.module.amazon.controller.admin.profitreport;

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
import java.util.Map;
import java.math.BigDecimal;
import java.time.LocalDate;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.profitreport.ProfitReportDO;
import cn.iocoder.yudao.module.amazon.service.profitreport.ProfitReportService;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "管理后台 - 亚马逊利润报表数据")
@RestController
@RequestMapping("/amazon/profit-report")
@Validated
@Slf4j
public class ProfitReportController {

    @Resource
    private ProfitReportService profitReportService;

    @PostMapping("/create")
    @Operation(summary = "创建亚马逊利润报表数据")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:create')")
    public CommonResult<Long> createProfitReport(@Valid @RequestBody ProfitReportSaveReqVO createReqVO) {
        return success(profitReportService.createProfitReport(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新亚马逊利润报表数据")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:update')")
    public CommonResult<Boolean> updateProfitReport(@Valid @RequestBody ProfitReportSaveReqVO updateReqVO) {
        profitReportService.updateProfitReport(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除亚马逊利润报表数据")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:delete')")
    public CommonResult<Boolean> deleteProfitReport(@RequestParam("id") Long id) {
        profitReportService.deleteProfitReport(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除亚马逊利润报表数据")
                @PreAuthorize("@ss.hasPermission('amazon:profit-report:delete')")
    public CommonResult<Boolean> deleteProfitReportList(@RequestParam("ids") List<Long> ids) {
        profitReportService.deleteProfitReportListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得亚马逊利润报表数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:query')")
    public CommonResult<ProfitReportRespVO> getProfitReport(@RequestParam("id") Long id) {
        ProfitReportDO profitReport = profitReportService.getProfitReport(id);
        ProfitReportRespVO respVO = BeanUtils.toBean(profitReport, ProfitReportRespVO.class);
        // 计算衍生字段
        calculateDerivedFields(respVO);
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得亚马逊利润报表数据分页")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:query')")
    public CommonResult<PageResult<ProfitReportRespVO>> getProfitReportPage(@Valid ProfitReportPageReqVO pageReqVO) {
        PageResult<ProfitReportDO> pageResult = profitReportService.getProfitReportPage(pageReqVO);
        PageResult<ProfitReportRespVO> respPageResult = BeanUtils.toBean(pageResult, ProfitReportRespVO.class);
        // 计算每条记录的衍生字段
        respPageResult.getList().forEach(this::calculateDerivedFields);
        return success(respPageResult);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出亚马逊利润报表数据 Excel")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportProfitReportExcel(@Valid ProfitReportPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ProfitReportDO> list = profitReportService.getProfitReportPage(pageReqVO).getList();
        // 转换为VO并计算衍生字段
        List<ProfitReportRespVO> respList = BeanUtils.toBean(list, ProfitReportRespVO.class);
        respList.forEach(this::calculateDerivedFields);
        // 导出 Excel
        ExcelUtils.write(response, "亚马逊利润报表数据.xls", "数据", ProfitReportRespVO.class, respList);
    }
    
    @GetMapping("/export-spu-sheets")
    @Operation(summary = "按SPU导出多Sheet Excel", description = "按店铺分文件，每个文件中每个SPU一个Sheet，多店铺时打包成ZIP")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSpuSheets(@Valid ProfitReportQueryVO queryVO,
                                HttpServletResponse response) throws Exception {
        log.info("按SPU导出多Sheet Excel - 参数: startDate={}, endDate={}, sids={}", 
            queryVO.getStartDate(), queryVO.getEndDate(), queryVO.getSids());
        
        // 获取按店铺和SPU分组的Excel文件Map
        Map<String, byte[]> excelMap = profitReportService.exportSpuSheetsByShop(queryVO);
        
        if (excelMap.isEmpty()) {
            throw new IllegalArgumentException("没有可导出的数据");
        }
        
        // 生成导出文件名的日期后缀
        String dateSuffix = "";
        if (queryVO.getStartDate() != null && queryVO.getEndDate() != null) {
            dateSuffix = "_" + queryVO.getStartDate() + "至" + queryVO.getEndDate();
        } else if (queryVO.getStartDate() != null) {
            dateSuffix = "_" + queryVO.getStartDate();
        }
        
        // 根据文件数量决定导出方式
        if (excelMap.size() == 1) {
            // 只有一个店铺，直接导出Excel
            Map.Entry<String, byte[]> entry = excelMap.entrySet().iterator().next();
            String fileName = entry.getKey().replace(".xlsx", dateSuffix + ".xlsx");
            
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
            response.addHeader("Content-Disposition", 
                              "attachment;filename=" + cn.iocoder.yudao.framework.common.util.http.HttpUtils.encodeUtf8(fileName));
            response.getOutputStream().write(entry.getValue());
            response.getOutputStream().flush();
        } else {
            // 多个店铺，打包成ZIP
            String zipFileName = "利润报表_批量导出" + dateSuffix + ".zip";
            cn.iocoder.yudao.framework.excel.core.util.ZipExportUtils.exportZip(response, zipFileName, excelMap);
        }
    }
    
    @PostMapping(value = "/export-aggregate-excel", consumes = "multipart/form-data")
    @Operation(summary = "导出聚合利润报表数据 Excel", description = "根据groupBy参数导出不同维度的聚合数据；当groupBy=SPU时，按店铺分文件、按SPU分Sheet导出；支持合并历史Excel文件")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAggregateExcel(
              @RequestParam(value = "groupBy", required = false) String groupBy,
              @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
              @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
              @RequestParam(value = "sid", required = false) String sidStr,
              @RequestParam(value = "mergeHistory", required = false, defaultValue = "false") Boolean mergeHistory,
              @RequestParam(value = "exportFormat", required = false, defaultValue = "STANDARD") String exportFormat,
              @RequestPart(value = "historyFile", required = false) MultipartFile historyFile,
              HttpServletResponse response) throws Exception {
        
        // 构建queryVO对象
        ProfitReportQueryVO queryVO = new ProfitReportQueryVO();
        queryVO.setGroupBy(groupBy);
        queryVO.setStartDate(startDate);
        queryVO.setEndDate(endDate);
        
        // 处理sid参数，从逗号分隔的字符串转换为List<Long>
        if (StrUtil.isNotBlank(sidStr)) {
            List<Long> sids = new ArrayList<>();
            for (String sid : sidStr.split(",")) {
                if (StrUtil.isNotBlank(sid.trim())) {
                    sids.add(Long.parseLong(sid.trim()));
                }
            }
            queryVO.setSids(sids);
        }
        
        // 添加日志记录查询参数，便于排查问题
        log.info("导出聚合数据 - 参数: groupBy={}, exportFormat={}, startDate={}, endDate={}, sids={}, mergeHistory={}", 
            groupBy, exportFormat, startDate, endDate, queryVO.getSids(), mergeHistory);
        
        // 设置不分页，导出所有数据
        queryVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        if (StrUtil.isBlank(groupBy)) {
            groupBy = "SPU"; // 默认按SPU聚合
        }
        
        // 如果是按SPU+SID分组导出（每个店铺一个Excel文件，每个SPU一个Sheet）
        if ("SPU_SID".equalsIgnoreCase(groupBy)) {
            log.info("按SPU+SID分组导出，每个店铺生成一个Excel，每个SPU一个Sheet");
            
            // 获取按店铺和SPU分组的Excel文件Map
            Map<String, byte[]> excelMap = profitReportService.exportSpuSheetsByShop(queryVO);
            
            if (excelMap.isEmpty()) {
                throw new IllegalArgumentException("没有可导出的数据");
            }
            
            log.info("导出Excel文件数量: {}, 文件名列表: {}", excelMap.size(), excelMap.keySet());
            
            // 生成导出文件名的日期后缀
            String dateSuffix = "";
            if (queryVO.getStartDate() != null && queryVO.getEndDate() != null) {
                dateSuffix = "_" + queryVO.getStartDate() + "至" + queryVO.getEndDate();
            } else if (queryVO.getStartDate() != null) {
                dateSuffix = "_" + queryVO.getStartDate();
            }
            
            // 根据文件数量决定导出方式
            if (excelMap.size() == 1) {
                // 只有一个店铺，直接导出Excel
                Map.Entry<String, byte[]> entry = excelMap.entrySet().iterator().next();
                String fileName = entry.getKey().replace(".xlsx", dateSuffix + ".xlsx");
                
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
                response.addHeader("Content-Disposition", 
                                  "attachment;filename=" + cn.iocoder.yudao.framework.common.util.http.HttpUtils.encodeUtf8(fileName));
                response.getOutputStream().write(entry.getValue());
                response.getOutputStream().flush();
            } else {
                // 多个店铺，打包成ZIP
                String zipFileName = "利润报表_批量导出" + dateSuffix + ".zip";
                log.info("准备导出ZIP文件: {}, 包含{}个Excel文件", zipFileName, excelMap.size());
                cn.iocoder.yudao.framework.excel.core.util.ZipExportUtils.exportZip(response, zipFileName, excelMap);
                log.info("ZIP文件导出完成");
            }
            return;
        }
        
        // 如果是管理利润表格式
        if ("MANAGEMENT".equalsIgnoreCase(exportFormat)) {
            // 获取聚合数据
            Object aggregateData = profitReportService.aggregate(queryVO);
            
            // 转换为管理利润表格式
            List<ProfitReportManagementVO> managementReport = profitReportService.convertToManagementReport(aggregateData, queryVO, groupBy);
            
            // 导出管理利润表
            ExcelUtils.write(response, "管理利润表.xlsx", "管理利润表", 
                ProfitReportManagementVO.class, managementReport);
            return;
        }
        
        // 如果是纯SPU维度导出（所有SPU放在一个Excel的不同sheet中）
        if ("SPU_PURE".equalsIgnoreCase(groupBy) || "SPU".equalsIgnoreCase(groupBy)) {
            log.info("纯SPU维度导出，所有SPU放在一个Excel的不同sheet中, mergeHistory={}", mergeHistory);
            
            Map<String, byte[]> excelMap;
            
            // 如果需要合并历史文件
            if (Boolean.TRUE.equals(mergeHistory) && historyFile != null && !historyFile.isEmpty()) {
                log.info("合并历史Excel文件，文件名: {}, 大小: {} bytes", 
                    historyFile.getOriginalFilename(), historyFile.getSize());
                
                // 调用支持合并的导出方法
                excelMap = profitReportService.exportPureSpuSheetsWithMerge(
                    queryVO, historyFile.getBytes());
            } else {
                // 使用原有的导出方法
                excelMap = profitReportService.exportPureSpuSheets(queryVO);
            }
            
            if (excelMap.isEmpty()) {
                throw new IllegalArgumentException("没有可导出的数据");
            }
            
            // 生成导出文件名的日期后缀
            String dateSuffix = "";
            if (queryVO.getStartDate() != null && queryVO.getEndDate() != null) {
                dateSuffix = "_" + queryVO.getStartDate() + "至" + queryVO.getEndDate();
            } else if (queryVO.getStartDate() != null) {
                dateSuffix = "_" + queryVO.getStartDate();
            }
            
            // 直接导出单一的Excel文件
            Map.Entry<String, byte[]> entry = excelMap.entrySet().iterator().next();
            String fileName = entry.getKey().replace(".xlsx", dateSuffix + ".xlsx");
            
            log.info("导出Excel文件: {}", fileName);
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
            response.addHeader("Content-Disposition", 
                              "attachment;filename=" + cn.iocoder.yudao.framework.common.util.http.HttpUtils.encodeUtf8(fileName));
            response.getOutputStream().write(entry.getValue());
            response.getOutputStream().flush();
            log.info("Excel文件导出完成");
            return;
        }
        
        // 标准格式导出（SPU_SID已经在上面处理过）
        switch (groupBy.toUpperCase()) {
            case "SPU_SID":
                PageResult<ProfitReportSpuSidAggVO> spuSidResult = profitReportService.aggregateBySpuAndSid(queryVO);
                ExcelUtils.write(response, "利润报表-SPU店铺聚合.xls", "SPU店铺聚合", 
                    ProfitReportSpuSidAggVO.class, spuSidResult.getList());
                break;
            case "SID":
            case "SHOP":
                PageResult<ProfitReportShopAggVO> shopResult = profitReportService.aggregateBySid(queryVO);
                ExcelUtils.write(response, "利润报表-店铺聚合.xls", "店铺聚合", 
                    ProfitReportShopAggVO.class, shopResult.getList());
                break;
            default:
                throw new IllegalArgumentException("不支持的聚合维度: " + groupBy);
        }
    }
    
    @PostMapping("/sync")
    @Operation(summary = "同步利润报表数据")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:sync')")
    public CommonResult<Integer> syncProfitReport(@Valid @RequestBody ProfitReportSyncReqVO syncReqVO) {
        // 校验日期有效性
        if (syncReqVO.getStartDate() == null || syncReqVO.getEndDate() == null) {
            return CommonResult.error(400, "日期不能为空");
        }
        
        // 校验日期不能是未来日期
        if (syncReqVO.getStartDate().isAfter(java.time.LocalDate.now()) || 
            syncReqVO.getEndDate().isAfter(java.time.LocalDate.now())) {
            return CommonResult.error(400, "日期不能超过今天");
        }
        
        // 校验日期范围
        if (syncReqVO.getStartDate().isAfter(syncReqVO.getEndDate())) {
            return CommonResult.error(400, "开始日期不能晚于结束日期");
        }
        
        // 日期跨度不能超过31天（领星API限制）
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(
            syncReqVO.getStartDate(), syncReqVO.getEndDate()
        );
        if (daysBetween > 31) {
            return CommonResult.error(400, "日期范围不能超过31天");
        }
        
        Integer count = profitReportService.syncProfitReport(syncReqVO);
        return success(count);
    }
    
//    @GetMapping("/aggregate/sku")
//    @Operation(summary = "SKU维度聚合查询")
//    @PreAuthorize("@ss.hasPermission('amazon:profit-report:query')")
//    public CommonResult<PageResult<ProfitReportSkuAggVO>> aggregateBySku(@Valid ProfitReportQueryVO queryVO) {
//        log.info("查询SKU聚合数据 - 参数: startDate={}, endDate={}, sids={}, asins={}, skus={}",
//            queryVO.getStartDate(), queryVO.getEndDate(),
//            queryVO.getSids(), queryVO.getAsins(), queryVO.getSkus());
//        return success(profitReportService.aggregateBySku(queryVO));
//    }
    
    @GetMapping("/aggregate/spu")
    @Operation(summary = "SPU维度聚合查询")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:query')")
    public CommonResult<PageResult<ProfitReportSpuAggVO>> aggregateBySpu(@Valid ProfitReportQueryVO queryVO) {
        return success(profitReportService.aggregateBySpu(queryVO));
    }
    
    @GetMapping("/aggregate/spu-sid")
    @Operation(summary = "SPU+SID维度聚合查询", description = "按SPU和店铺ID两个维度聚合，同一个SPU在不同店铺分开统计")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:query')")
    public CommonResult<PageResult<ProfitReportSpuSidAggVO>> aggregateBySpuAndSid(@Valid ProfitReportQueryVO queryVO) {
        return success(profitReportService.aggregateBySpuAndSid(queryVO));
    }
    
    @GetMapping("/aggregate/shop")
    @Operation(summary = "店铺维度聚合查询")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:query')")
    public CommonResult<PageResult<ProfitReportShopAggVO>> aggregateByShop(@Valid ProfitReportQueryVO queryVO) {
        return success(profitReportService.aggregateBySid(queryVO));
    }
    
//    @GetMapping("/aggregate/asin")
//    @Operation(summary = "ASIN维度聚合查询")
//    @PreAuthorize("@ss.hasPermission('amazon:profit-report:query')")
//    public CommonResult<PageResult<ProfitReportAsinAggVO>> aggregateByAsin(@Valid ProfitReportQueryVO queryVO) {
//        return success(profitReportService.aggregateByAsin(queryVO));
//    }
    
//    @GetMapping("/aggregate/date")
//    @Operation(summary = "日期维度聚合查询")
//    @PreAuthorize("@ss.hasPermission('amazon:profit-report:query')")
//    public CommonResult<PageResult<ProfitReportDateAggVO>> aggregateByDate(@Valid ProfitReportQueryVO queryVO) {
//        return success(profitReportService.aggregateByDate(queryVO));
//    }
    
    @GetMapping("/aggregate")
    @Operation(summary = "统一聚合查询接口", description = "根据groupBy参数动态选择聚合维度：ASIN、SPU、SKU、SID/SHOP、DATE")
    @PreAuthorize("@ss.hasPermission('amazon:profit-report:query')")
    public CommonResult<?> aggregate(@Valid ProfitReportQueryVO queryVO) {
        return success(profitReportService.aggregate(queryVO));
    }
    
    /**
     * 计算衍生字段
     * 1. 退货退款 = 收入退款额 + 费用退款额
     * 2. 企业其他业务收入 = 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入
     * 3. 其他费用 = 其他订单费用 + 入库配置费 + FBA国际物流货运费 + 调整费用 + 平台其他费
     * 4. 站外费用 = otherFeeStr中所有feeAllocation的总和
     * 5. 毛利率转换为百分比
     */
    private void calculateDerivedFields(ProfitReportRespVO vo) {
        if (vo == null) {
            return;
        }
        
        // 1. 计算退货退款 = 收入退款额 + 费用退款额
        BigDecimal totalRefunds = BigDecimal.ZERO;
        if (vo.getTotalSalesRefunds() != null) {
            totalRefunds = totalRefunds.add(vo.getTotalSalesRefunds());
        }
        if (vo.getTotalFeeRefunds() != null) {
            totalRefunds = totalRefunds.add(vo.getTotalFeeRefunds());
        }
        vo.setTotalRefunds(totalRefunds);
        
        // 2. 计算企业其他业务收入 = 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入
        BigDecimal otherRevenue = BigDecimal.ZERO;
        if (vo.getShippingCredits() != null) {
            otherRevenue = otherRevenue.add(vo.getShippingCredits());
        }
        if (vo.getPromotionalRebates() != null) {
            otherRevenue = otherRevenue.add(vo.getPromotionalRebates());
        }
        if (vo.getFbaInventoryCredit() != null) {
            otherRevenue = otherRevenue.add(vo.getFbaInventoryCredit());
        }
        if (vo.getCashOnDelivery() != null) {
            otherRevenue = otherRevenue.add(vo.getCashOnDelivery());
        }
        if (vo.getOtherInAmount() != null) {
            otherRevenue = otherRevenue.add(vo.getOtherInAmount());
        }
        vo.setOtherRevenue(otherRevenue);
        
        // 3. 计算其他费用 = 其他订单费用 + 入库配置费 + FBA国际物流货运费 + 调整费用 + 平台其他费
        BigDecimal otherFee = BigDecimal.ZERO;
        if (vo.getOtherTransactionFees() != null) {
            otherFee = otherFee.add(vo.getOtherTransactionFees());
        }
        if (vo.getSharedFbaInboundTransportationProgramFee() != null) {
            otherFee = otherFee.add(vo.getSharedFbaInboundTransportationProgramFee());
        }
        if (vo.getSharedFbaIntegerernationalInboundFee() != null) {
            otherFee = otherFee.add(vo.getSharedFbaIntegerernationalInboundFee());
        }
        if (vo.getAdjustments() != null) {
            otherFee = otherFee.add(vo.getAdjustments());
        }
        if (vo.getTotalPlatformOtherFee() != null) {
            otherFee = otherFee.add(vo.getTotalPlatformOtherFee());
        }
        vo.setOtherFee(otherFee);
        
        // 4. 计算站外费用 = otherFeeStr中所有feeAllocation的总和
        BigDecimal offsiteFee = BigDecimal.ZERO;
        if (vo.getOtherFeeStr() != null && !vo.getOtherFeeStr().trim().isEmpty()) {
            try {
                JSONArray feeArray = JSONUtil.parseArray(vo.getOtherFeeStr());
                for (int i = 0; i < feeArray.size(); i++) {
                    JSONObject feeObj = feeArray.getJSONObject(i);
                    if (feeObj.containsKey("feeAllocation")) {
                        BigDecimal feeAllocation = feeObj.getBigDecimal("feeAllocation");
                        if (feeAllocation != null) {
                            offsiteFee = offsiteFee.add(feeAllocation);
                        }
                    }
                }
            } catch (Exception e) {
                // 如果解析失败，记录日志但不影响其他计算
                // log.warn("解析otherFeeStr失败: {}", e.getMessage());
            }
        }
        vo.setOffsiteFee(offsiteFee);
        
        // 5. 生成毛利率百分比字符串（原值保持不变）
        if (vo.getGrossRate() != null) {
            // 保留2位小数并添加百分号
            BigDecimal rate = vo.getGrossRate().setScale(2, java.math.RoundingMode.HALF_UP);
            vo.setGrossRateStr(rate + "%");
        }
    }

}