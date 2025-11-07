package cn.iocoder.yudao.module.amazon.service.productperformance;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo.ProductPerformancePageReqVO;
import cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo.ProductPerformanceSyncReqVO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ProductPerformanceDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ProductPerformanceQueryDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ProductPerformanceResponseDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.LingXingApiResponseDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.FbaAgeListDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.productperformance.ProductPerformanceDO;
import cn.iocoder.yudao.module.amazon.dal.mysql.productperformance.ProductPerformanceMapper;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
// import cn.iocoder.yudao.module.amazon.service.taskexeclog.TaskExecLogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 亚马逊产品表现 Service 实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
@Validated
public class ProductPerformanceServiceImpl implements ProductPerformanceService {

    @Resource
    private ProductPerformanceMapper productPerformanceMapper;

    @Resource
    private LingXingApiService lingXingApiService;

    // 暂时注释掉，等TaskExecLogService完善后再使用
    // @Resource
    // private TaskExecLogService taskExecLogService;

    @Override
    public PageResult<ProductPerformanceDO> getProductPerformancePage(ProductPerformancePageReqVO pageReqVO) {
        // 判断是否需要按SPU聚合
        if ("spu".equals(pageReqVO.getSummaryField())) {
            // 按SPU维度聚合查询
            List<ProductPerformanceDO> list = productPerformanceMapper.selectPageGroupBySpu(pageReqVO);
            Long total = productPerformanceMapper.selectCountGroupBySpu(pageReqVO);
            
            // 对SPU聚合后的数据重新计算比率指标
            recalculateSpuMetrics(list);
            
            return new PageResult<>(list, total);
        } else {
            // 默认按ASIN维度查询（保持原有逻辑）
            return productPerformanceMapper.selectPage(pageReqVO);
        }
    }
    
    /**
     * 重新计算SPU聚合后的比率指标
     * @param list SPU聚合后的数据列表
     */
    private void recalculateSpuMetrics(List<ProductPerformanceDO> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        
        for (ProductPerformanceDO item : list) {
            // 1. 均价 = 净销售额 / 销量
            if (item.getVolume() != null && item.getVolume() > 0 && item.getNetAmount() != null) {
                item.setAvgCustomPrice(item.getNetAmount().divide(
                    BigDecimal.valueOf(item.getVolume()), 2, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setAvgCustomPrice(BigDecimal.ZERO);
            }
            
            // 2. 退货率 = 退货量 / 销量 (保留4位小数)
            if (item.getVolume() != null && item.getVolume() > 0 && item.getReturnCount() != null) {
                item.setReturnRate(BigDecimal.valueOf(item.getReturnCount())
                    .divide(BigDecimal.valueOf(item.getVolume()), 4, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setReturnRate(BigDecimal.ZERO);
            }
            
            // 3. 毛利率 = 毛利 / 净销售额 (保留4位小数)
            if (item.getNetAmount() != null && item.getNetAmount().compareTo(BigDecimal.ZERO) > 0 
                && item.getGrossProfit() != null) {
                item.setGrossMargin(item.getGrossProfit()
                    .divide(item.getNetAmount(), 4, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setGrossMargin(BigDecimal.ZERO);
            }
            
            // 4. CTR = 点击 / 曝光 (保留4位小数)
            if (item.getImpressions() != null && item.getImpressions() > 0 && item.getAdClicks() != null) {
                item.setCtr(BigDecimal.valueOf(item.getAdClicks())
                    .divide(BigDecimal.valueOf(item.getImpressions()), 4, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setCtr(BigDecimal.ZERO);
            }
            
            // 5. CR (广告转化率) = 广告订单 / 点击 (保留4位小数)
            if (item.getAdClicks() != null && item.getAdClicks() > 0 && item.getAdOrderQuantity() != null) {
                item.setAdCvr(BigDecimal.valueOf(item.getAdOrderQuantity())
                    .divide(BigDecimal.valueOf(item.getAdClicks()), 4, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setAdCvr(BigDecimal.ZERO);
            }
            
            // 6. CPC = 广告花费 / 广告点击 (取绝对值)
            if (item.getAdClicks() != null && item.getAdClicks() > 0 && item.getSpend() != null) {
                item.setCpc(item.getSpend().divide(
                    BigDecimal.valueOf(item.getAdClicks()), 2, BigDecimal.ROUND_HALF_UP).abs());
            } else {
                item.setCpc(BigDecimal.ZERO);
            }
            
            // 7. CPA (CPO) = 花费 / 广告订单 (取绝对值)
            if (item.getAdOrderQuantity() != null && item.getAdOrderQuantity() > 0 && item.getSpend() != null) {
                item.setCpo(item.getSpend().divide(
                    BigDecimal.valueOf(item.getAdOrderQuantity()), 2, BigDecimal.ROUND_HALF_UP).abs());
            } else {
                item.setCpo(BigDecimal.ZERO);
            }
            
            // 8. ACOS = 广告花费 / 广告销售额 (保留4位小数，取绝对值)
            if (item.getAdSalesAmount() != null && item.getAdSalesAmount().compareTo(BigDecimal.ZERO) > 0 
                && item.getSpend() != null) {
                item.setAcos(item.getSpend()
                    .divide(item.getAdSalesAmount(), 4, BigDecimal.ROUND_HALF_UP).abs());
            } else {
                item.setAcos(BigDecimal.ZERO);
            }
            
            // 9. TACOS = 广告花费 / 净销售额 (保留4位小数，取绝对值)
            if (item.getNetAmount() != null && item.getNetAmount().compareTo(BigDecimal.ZERO) > 0 
                && item.getSpend() != null) {
                item.setTacos(item.getSpend()
                    .divide(item.getNetAmount(), 4, BigDecimal.ROUND_HALF_UP).abs());
            } else {
                item.setTacos(BigDecimal.ZERO);
            }
            
            // 10. 广告订单占比 = 广告订单数量 / 销量
            BigDecimal adOrderRatio = BigDecimal.ZERO;
            if (item.getVolume() != null && item.getVolume() > 0 && item.getAdOrderQuantity() != null) {
                adOrderRatio = BigDecimal.valueOf(item.getAdOrderQuantity())
                    .divide(BigDecimal.valueOf(item.getVolume()), 4, BigDecimal.ROUND_HALF_UP);
            }
            
            // 11. 盈亏平衡点 = ACOS * 广告订单占比 (保留4位小数)
            if (item.getAcos() != null && adOrderRatio.compareTo(BigDecimal.ZERO) > 0) {
                item.setBreakEvenTacos(item.getAcos().multiply(adOrderRatio)
                    .setScale(4, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setBreakEvenTacos(BigDecimal.ZERO);
            }
            
            // 12. ROAS = 广告销售额 / 广告花费
            if (item.getSpend() != null && item.getSpend().compareTo(BigDecimal.ZERO) > 0 
                && item.getAdSalesAmount() != null) {
                item.setRoas(item.getAdSalesAmount()
                    .divide(item.getSpend(), 2, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setRoas(BigDecimal.ZERO);
            }
            
            // 13. 页面转化率 CVR = 销量 / sessions (保留4位小数)
            if (item.getSessions() != null && item.getSessions() > 0 && item.getVolume() != null) {
                item.setCvr(BigDecimal.valueOf(item.getVolume())
                    .divide(BigDecimal.valueOf(item.getSessions()), 4, BigDecimal.ROUND_HALF_UP));
            } else {
                item.setCvr(BigDecimal.ZERO);
            }
        }
    }

    @Override
    public ProductPerformanceDO getProductPerformance(Long id) {
        return productPerformanceMapper.selectById(id);
    }

    @Override
    public String syncProductPerformance(ProductPerformanceSyncReqVO syncReqVO) {
        // 先在事务外刷新缓存
        try {
            log.info("开始刷新SKU-SPU缓存");
            lingXingApiService.refreshSkuSpuCache();
            log.info("SKU-SPU缓存刷新完成");
        } catch (Exception e) {
            log.error("刷新SKU-SPU缓存失败，但继续执行同步", e);
            // 缓存刷新失败不影响同步，继续执行
        }
        
        // 调用实际的同步方法（带事务）
        return doSyncProductPerformance(syncReqVO);
    }
    
    @Transactional(rollbackFor = Exception.class)
    protected String doSyncProductPerformance(ProductPerformanceSyncReqVO syncReqVO) {
        log.info("开始同步产品表现数据，参数: {}", syncReqVO);
        
        // TODO: 记录任务执行日志
        // Long logId = taskExecLogService.createLog("SYNC_PRODUCT_PERFORMANCE", 
        //     "查询产品表现", syncReqVO.toString());


        
        try {
            // 验证参数
            if (CollUtil.isEmpty(syncReqVO.getSids())) {
                throw new IllegalArgumentException("店铺ID列表不能为空");
            }
            if (StrUtil.isBlank(syncReqVO.getStartDate()) || StrUtil.isBlank(syncReqVO.getEndDate())) {
                throw new IllegalArgumentException("开始时间和结束时间不能为空");
            }
            
            // 解析日期范围
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(syncReqVO.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(syncReqVO.getEndDate(), formatter);
            
            // 先获取所有店铺的库龄信息（一次性获取，避免重复调用）
            log.info("获取店铺库龄信息");
            List<FbaAgeListDTO> allFbaAgeData = lingXingApiService.getAllFbaAgeList(syncReqVO.getSids(), 0, 1000);
            
            // 将库龄数据按ASIN和SID建立索引
            Map<String, FbaAgeListDTO> fbaAgeMap = new HashMap<>();
            if (CollUtil.isNotEmpty(allFbaAgeData)) {
                for (FbaAgeListDTO ageData : allFbaAgeData) {
                    String key = ageData.getAsin() + "_" + ageData.getSid();
                    fbaAgeMap.put(key, ageData);
                }
                log.info("获取到 {} 条库龄数据", allFbaAgeData.size());
            } else {
                log.warn("未获取到库龄数据");
            }
            
            // 按天分别拉取数据
            int totalCount = 0;
            LocalDate currentDate = startDate;
            
            while (!currentDate.isAfter(endDate)) {
                String dateStr = currentDate.format(formatter);
                log.info("同步日期 {} 的产品表现数据", dateStr);
                
                List<ProductPerformanceDTO> dtoList;
                
                // 如果没有指定ASIN，则查询店铺的所有产品
                if (CollUtil.isEmpty(syncReqVO.getAsins())) {
                    log.info("未指定ASIN，将查询店铺的所有产品");
                    // 创建单日期的查询请求
                    ProductPerformanceSyncReqVO dayReqVO = new ProductPerformanceSyncReqVO();
                    dayReqVO.setSids(syncReqVO.getSids());
                    dayReqVO.setStartDate(dateStr);
                    dayReqVO.setEndDate(dateStr);
                    dayReqVO.setSummaryField(syncReqVO.getSummaryField());
                    dayReqVO.setMid(syncReqVO.getMid());
                    dayReqVO.setCurrencyCode(syncReqVO.getCurrencyCode());
                    dayReqVO.setIsRecentlyEnum(syncReqVO.getIsRecentlyEnum());
                    
                    dtoList = syncAllProductsForStores(dayReqVO);
                } else {
                    // 调用领星API获取指定ASIN的数据
                    dtoList = lingXingApiService.getAllProductPerformance(
                        syncReqVO.getAsins(),
                        syncReqVO.getSids(),
                        dateStr,
                        dateStr,
                        syncReqVO.getSummaryField()
                    );
                }
                
                if (CollUtil.isNotEmpty(dtoList)) {
                    // 转换DTO为DO，同时传入库龄数据进行匹配
                    List<ProductPerformanceDO> doList = convertDtoToDo(dtoList, dateStr, dateStr, fbaAgeMap);
                    
                    // 设置每条记录的日期
                    for (ProductPerformanceDO performanceDO : doList) {
                        performanceDO.setRecordDate(currentDate);
                    }
                    
                    // 批量保存或更新
                    int count = saveOrUpdateBatch(doList);
                    totalCount += count;
                    log.info("日期 {} 同步成功，处理数据 {} 条", dateStr, count);
                } else {
                    log.info("日期 {} 未查询到数据", dateStr);
                }
                
                // 移动到下一天
                currentDate = currentDate.plusDays(1);
                
                // 避免请求过快，延迟500毫秒
                if (!currentDate.isAfter(endDate)) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("线程被中断", e);
                        break;
                    }
                }
            }
            
            String result = String.format("同步成功，处理产品表现数据 %d 条", totalCount);
            // TODO: taskExecLogService.updateLog(logId, "SUCCESS", result, totalCount);
            log.info(result);
            
            return result;
        } catch (Exception e) {
            log.error("同步产品表现数据失败", e);
            // TODO: taskExecLogService.updateLog(logId, "FAIL", e.getMessage(), 0);
            throw new RuntimeException("同步产品表现数据失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveOrUpdateBatch(List<ProductPerformanceDO> list) {
        if (CollUtil.isEmpty(list)) {
            return 0;
        }
        
        // 由于PostgreSQL和MySQL的语法差异，采用先删除后插入的方式
        // 按照唯一键分组
        Map<String, List<ProductPerformanceDO>> groupedData = list.stream()
            .collect(Collectors.groupingBy(item -> 
                item.getAsin() + "_" + item.getSid() + "_" + 
                item.getStartDate() + "_" + item.getEndDate() + "_" + 
                (item.getSummaryField() != null ? item.getSummaryField() : "asin")
            ));
        
        int totalCount = 0;
        
        // 批量处理
        for (Map.Entry<String, List<ProductPerformanceDO>> entry : groupedData.entrySet()) {
            List<ProductPerformanceDO> group = entry.getValue();
            if (CollUtil.isNotEmpty(group)) {
                ProductPerformanceDO first = group.get(0);
                
                // 先删除旧数据
                ProductPerformanceDO deleteCondition = new ProductPerformanceDO();
                deleteCondition.setAsin(first.getAsin());
                deleteCondition.setSid(first.getSid());
                deleteCondition.setStartDate(first.getStartDate());
                deleteCondition.setEndDate(first.getEndDate());
                deleteCondition.setSummaryField(first.getSummaryField());
                
                // 使用物理删除而不是逻辑删除，避免PostgreSQL类型转换问题
                productPerformanceMapper.physicalDelete(
                    first.getAsin(),
                    first.getSid(),
                    first.getStartDate(),
                    first.getEndDate(),
                    first.getSummaryField() != null ? first.getSummaryField() : "asin"
                );
                
                // 再批量插入新数据
                // 调试：确保所有数据都有正确的字段值
                for (ProductPerformanceDO item : group) {
                    // 再次确保deleted字段有值
                    if (item.getDeleted() == null) {
                        try {
                            java.lang.reflect.Field deletedField = BaseDO.class.getDeclaredField("deleted");
                            deletedField.setAccessible(true);
                            deletedField.set(item, Boolean.FALSE);
                        } catch (Exception e) {
                            log.warn("设置deleted字段失败", e);
                        }
                    }
                    // 确保tenantId有值
                    if (item.getTenantId() == null) {
                        item.setTenantId(1L);
                    }
                }
                
                // 调试：打印第一条数据的关键字段
                if (!group.isEmpty()) {
                    ProductPerformanceDO one = group.get(0);
                    log.info("插入数据调试 - deleted: {}, tenantId: {}, creator: {}",
                            one.getDeleted(), one.getTenantId(), one.getCreator());
                }
                totalCount += productPerformanceMapper.insertBatch(group);
            }
        }
        
        return totalCount;
    }

    @Override
    public List<ProductPerformanceDO> getByAsinsAndSids(List<String> asins, List<Long> sids,
                                                       LocalDate startDate, LocalDate endDate) {
        return productPerformanceMapper.selectByAsinsAndSids(asins, sids, startDate, endDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByDateRange(List<Long> sids, LocalDate startDate, LocalDate endDate) {
        return productPerformanceMapper.deleteByDateRange(sids, startDate, endDate);
    }

    /**
     * 同步店铺的所有产品
     */
    private List<ProductPerformanceDTO> syncAllProductsForStores(ProductPerformanceSyncReqVO syncReqVO) {
        List<ProductPerformanceDTO> allData = new ArrayList<>();
        
        // 构建查询参数，不指定ASIN
        ProductPerformanceQueryDTO queryParams = ProductPerformanceQueryDTO.builder()
            .offset(0)
            .length(10000) // 每页最大值
            .sortField("volume")
            .sortType("desc")
            .sid(syncReqVO.getSids()) // 店铺ID列表
            .startDate(syncReqVO.getStartDate())
            .endDate(syncReqVO.getEndDate())
            .summaryField(syncReqVO.getSummaryField() != null ? syncReqVO.getSummaryField() : "asin")
            .mid(syncReqVO.getMid())
            .currencyCode(syncReqVO.getCurrencyCode())
            .isRecentlyEnum(syncReqVO.getIsRecentlyEnum())
            .build();
        
        try {
            // 分页查询所有数据
            int offset = 0;
            int pageSize = 10000;
            boolean hasMore = true;
            
            while (hasMore) {
                queryParams.setOffset(offset);
                queryParams.setLength(pageSize);
                
                log.info("查询第 {} 页，offset: {}", (offset / pageSize + 1), offset);
                
                LingXingApiResponseDTO<ProductPerformanceResponseDTO> response = 
                    lingXingApiService.getProductPerformance(queryParams);
                
                if (response.getCode() == 0 && response.getData() != null && response.getData().getList() != null) {
                    ProductPerformanceResponseDTO responseData = response.getData();
                    List<ProductPerformanceDTO> pageData = responseData.getList();
                    allData.addAll(pageData);
                    
                    log.info("第 {} 页获取成功，数据量: {}, 总数: {}", 
                        (offset / pageSize + 1), pageData.size(), responseData.getTotal());
                    
                    // 判断是否还有更多数据
                    if (pageData.size() < pageSize || allData.size() >= responseData.getTotal()) {
                        hasMore = false;
                    } else {
                        offset += pageSize;
                        
                        // 避免请求过快，延迟1秒
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            log.warn("线程被中断", e);
                        }
                    }
                } else {
                    log.error("查询失败: {}", response.getMsg());
                    hasMore = false;
                }
            }
            
            log.info("店铺所有产品查询完成，总数据量: {}", allData.size());
        } catch (Exception e) {
            log.error("查询店铺所有产品失败", e);
            throw new RuntimeException("查询店铺所有产品失败: " + e.getMessage(), e);
        }
        
        return allData;
    }

    /**
     * 将DTO转换为DO（重载方法，兼容旧调用）
     */
    private List<ProductPerformanceDO> convertDtoToDo(List<ProductPerformanceDTO> dtoList, 
                                                     String startDateStr, String endDateStr) {
        return convertDtoToDo(dtoList, startDateStr, endDateStr, new HashMap<>());
    }
    
    /**
     * 将DTO转换为DO，同时匹配库龄数据
     */
    private List<ProductPerformanceDO> convertDtoToDo(List<ProductPerformanceDTO> dtoList, 
                                                     String startDateStr, String endDateStr,
                                                     Map<String, FbaAgeListDTO> fbaAgeMap) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(startDateStr, formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        
        // 提取所有SKU，批量查询SPU信息
        List<String> skuList = new ArrayList<>();
        for (ProductPerformanceDTO dto : dtoList) {
            if (CollUtil.isNotEmpty(dto.getPriceList())) {
                for (ProductPerformanceDTO.PriceInfo priceInfo : dto.getPriceList()) {
                    if (StrUtil.isNotBlank(priceInfo.getLocalSku())) {
                        skuList.add(priceInfo.getLocalSku());
                    }
                }
            }
        }
        
        // 批量获取SKU对应的SPU信息
        Map<String, Map<String, String>> skuSpuMap = new HashMap<>();
        if (CollUtil.isNotEmpty(skuList)) {
            try {
                log.info("批量获取 {} 个SKU对应的SPU信息", skuList.size());
                skuSpuMap = lingXingApiService.getSpuBySkuBatch(skuList);
                log.info("成功获取 {} 个SKU的SPU信息", skuSpuMap.size());
            } catch (Exception e) {
                log.warn("批量获取SPU信息失败: {}", e.getMessage());
                // 不影响主流程，继续执行
            }
        }
        
        final Map<String, Map<String, String>> finalSkuSpuMap = skuSpuMap;
        
        return dtoList.stream().map(dto -> {
            ProductPerformanceDO performanceDO = new ProductPerformanceDO();
            
            // 设置基础字段默认值
            performanceDO.setCreator("1");
            performanceDO.setUpdater("1");
            performanceDO.setCreateTime(LocalDateTime.now());
            performanceDO.setUpdateTime(LocalDateTime.now());
            performanceDO.setTenantId(1L); // 设置默认租户ID
            
            // 基础信息
            // 从asins列表获取第一个ASIN
            if (CollUtil.isNotEmpty(dto.getAsins())) {
                ProductPerformanceDTO.AsinInfo asinInfo = dto.getAsins().get(0);
                performanceDO.setAsin(asinInfo.getAsin());
                performanceDO.setAmazonUrl(asinInfo.getAmazonUrl());
                performanceDO.setSid(asinInfo.getSid());
            }
            
            // 从parent_asins获取父ASIN
            String parentAsin = null;
            if (CollUtil.isNotEmpty(dto.getParentAsins())) {
                ProductPerformanceDTO.ParentAsinInfo parentAsinInfo = dto.getParentAsins().get(0);
                parentAsin = parentAsinInfo.getParentAsin();
                performanceDO.setParentAsin(parentAsin);
            }
            
            // 从price_list获取第一个价格信息
            if (CollUtil.isNotEmpty(dto.getPriceList())) {
                ProductPerformanceDTO.PriceInfo priceInfo = dto.getPriceList().get(0);
                performanceDO.setMsku(priceInfo.getSellerSku());
                performanceDO.setSku(priceInfo.getLocalSku());
                
                // 设置SPU信息
                String spu = null;
                if (StrUtil.isNotBlank(priceInfo.getLocalSku()) && !finalSkuSpuMap.isEmpty()) {
                    Map<String, String> spuInfo = finalSkuSpuMap.get(priceInfo.getLocalSku());
                    if (spuInfo != null && spuInfo.containsKey("spu")) {
                        spu = spuInfo.get("spu");
                        log.debug("设置SPU信息: SKU={}, SPU={}", priceInfo.getLocalSku(), spu);
                    }
                }
                
                // 如果SPU为空，使用父ASIN作为SPU
                if (StrUtil.isBlank(spu) && StrUtil.isNotBlank(parentAsin)) {
                    spu = parentAsin;
                    log.debug("SPU为空，使用父ASIN作为SPU: {}", parentAsin);
                }
                
                performanceDO.setSpu(spu);
                
                performanceDO.setMid(priceInfo.getMid());
                performanceDO.setSellerName(priceInfo.getSellerName());
                performanceDO.setCountry(priceInfo.getCountry());
                performanceDO.setPrice(priceInfo.getPrice());
                performanceDO.setStatus(priceInfo.getStatus());
                performanceDO.setLocalName(priceInfo.getLocalName());
            }
            
            // 如果price_list为空但有父ASIN，也要设置SPU
            if (CollUtil.isEmpty(dto.getPriceList()) && StrUtil.isNotBlank(parentAsin)) {
                performanceDO.setSpu(parentAsin);
                log.debug("无价格信息，使用父ASIN作为SPU: {}", parentAsin);
            }
            
            performanceDO.setItemName(dto.getItemName());
            performanceDO.setSmallImageUrl(dto.getSmallImageUrl());
            performanceDO.setCurrencyCode(dto.getCurrencyCode());
            performanceDO.setCurrencyIcon(dto.getCurrencyIcon());
            
            // 销售数据
            performanceDO.setVolume(dto.getVolume() != null ? dto.getVolume() : 0);
            performanceDO.setOrderItems(dto.getOrderItems() != null ? dto.getOrderItems() : 0);
            performanceDO.setAmount(dto.getAmount() != null ? dto.getAmount() : BigDecimal.ZERO);
            performanceDO.setNetAmount(dto.getNetAmount() != null ? dto.getNetAmount() : BigDecimal.ZERO);
            performanceDO.setAvgCustomPrice(dto.getAvgCustomPrice());
            performanceDO.setAvgVolume(dto.getAvgVolume());
            
            // 环比数据
            performanceDO.setVolumeChain(dto.getVolumeChain() != null ? dto.getVolumeChain() : 0);
            performanceDO.setVolumeChainRatio(dto.getVolumeChainRatio() != null ? dto.getVolumeChainRatio() : BigDecimal.ZERO);
            performanceDO.setAmountChain(dto.getAmountChain() != null ? dto.getAmountChain() : BigDecimal.ZERO);
            performanceDO.setAmountChainRatio(dto.getAmountChainRatio() != null ? dto.getAmountChainRatio() : BigDecimal.ZERO);
            performanceDO.setOrderItemsChain(dto.getOrderItemsChain() != null ? dto.getOrderItemsChain() : 0);
            performanceDO.setOrderChainRatio(dto.getOrderChainRatio() != null ? dto.getOrderChainRatio() : BigDecimal.ZERO);
            
            // 促销数据
            performanceDO.setPromotionVolume(dto.getPromotionVolume() != null ? dto.getPromotionVolume() : 0);
            performanceDO.setPromotionAmount(dto.getPromotionAmount() != null ? dto.getPromotionAmount() : BigDecimal.ZERO);
            performanceDO.setPromotionOrderItems(dto.getPromotionOrderItems() != null ? dto.getPromotionOrderItems() : 0);
            performanceDO.setPromotionDiscount(dto.getPromotionDiscount() != null ? dto.getPromotionDiscount() : BigDecimal.ZERO);
            
            // 毛利数据
            performanceDO.setGrossProfit(dto.getGrossProfit() != null ? dto.getGrossProfit() : BigDecimal.ZERO);
            performanceDO.setPredictGrossProfit(dto.getPredictGrossProfit() != null ? dto.getPredictGrossProfit() : BigDecimal.ZERO);
            performanceDO.setGrossMargin(dto.getGrossMargin() != null ? dto.getGrossMargin() : BigDecimal.ZERO);
            performanceDO.setPredictGrossMargin(dto.getPredictGrossMargin() != null ? dto.getPredictGrossMargin() : BigDecimal.ZERO);
            performanceDO.setRoi(dto.getRoi() != null ? dto.getRoi() : BigDecimal.ZERO);
            
            // 评价数据
            performanceDO.setReviewsCount(dto.getReviewsCount() != null ? dto.getReviewsCount() : 0);
            performanceDO.setAvgStar(dto.getAvgStar() != null ? dto.getAvgStar() : BigDecimal.ZERO);
            performanceDO.setPrevStar(dto.getPrevStar() != null ? dto.getPrevStar() : BigDecimal.ZERO);
            performanceDO.setCommentRate(dto.getCommentRate());
            
            // 退货数据
            performanceDO.setReturnCount(dto.getReturnCount() != null ? dto.getReturnCount() : 0);
            performanceDO.setReturnRate(dto.getReturnRate() != null ? dto.getReturnRate() : BigDecimal.ZERO);
            performanceDO.setReturnGoodsCount(dto.getReturnGoodsCount() != null ? dto.getReturnGoodsCount() : 0);
            performanceDO.setReturnGoodsRate(dto.getReturnGoodsRate() != null ? dto.getReturnGoodsRate() : BigDecimal.ZERO);
            performanceDO.setReturnAmount(dto.getReturnAmount() != null ? dto.getReturnAmount() : BigDecimal.ZERO);
            
            // 库存数据
            performanceDO.setAfnFulfillableQuantity(dto.getAfnFulfillableQuantity() != null ? dto.getAfnFulfillableQuantity() : 0);
            performanceDO.setAfnInboundReceivingQuantity(dto.getAfnInboundReceivingQuantity() != null ? dto.getAfnInboundReceivingQuantity() : 0);
            performanceDO.setAfnInboundShippedQuantity(dto.getAfnInboundShippedQuantity() != null ? dto.getAfnInboundShippedQuantity() : 0);
            performanceDO.setAfnInboundWorkingQuantity(dto.getAfnInboundWorkingQuantity() != null ? dto.getAfnInboundWorkingQuantity() : 0);
            performanceDO.setAfnUnsellableQuantity(dto.getAfnUnsellableQuantity() != null ? dto.getAfnUnsellableQuantity() : 0);
            performanceDO.setReservedFcProcessing(dto.getReservedFcProcessing() != null ? dto.getReservedFcProcessing() : 0);
            performanceDO.setReservedFcTransfers(dto.getReservedFcTransfers() != null ? dto.getReservedFcTransfers() : 0);
            performanceDO.setFbmQuantity(dto.getFbmQuantity() != null ? dto.getFbmQuantity() : 0);
            performanceDO.setReservedCustomerorders(dto.getReservedCustomerorders() != null ? dto.getReservedCustomerorders() : 0);
            performanceDO.setStockUpNum(dto.getStockUpNum() != null ? dto.getStockUpNum() : 0);
            performanceDO.setAvailableDays(dto.getAvailableDays() != null ? dto.getAvailableDays() : 0);
            performanceDO.setFbmAvailableDays(dto.getFbmAvailableDays() != null ? dto.getFbmAvailableDays() : 0);
            performanceDO.setMonthStockSalesRatio(dto.getMonthStockSalesRatio());
            performanceDO.setInventorySalesRatio(dto.getInventorySalesRatio() != null ? dto.getInventorySalesRatio() : BigDecimal.ZERO);
            
            // 流量数据
            performanceDO.setClicks(dto.getClicks() != null ? dto.getClicks() : 0);
            performanceDO.setSessions(dto.getSessions() != null ? dto.getSessions() : 0);
            performanceDO.setSessionsMobile(dto.getSessionsMobile() != null ? dto.getSessionsMobile() : 0);
            performanceDO.setSessionsTotal(dto.getSessionsTotal() != null ? dto.getSessionsTotal() : 0);
            performanceDO.setPageViews(dto.getPageViews() != null ? dto.getPageViews() : 0);
            performanceDO.setPageViewsMobile(dto.getPageViewsMobile() != null ? dto.getPageViewsMobile() : 0);
            performanceDO.setPageViewsTotal(dto.getPageViewsTotal() != null ? dto.getPageViewsTotal() : 0);
            performanceDO.setBuyBoxPercentage(dto.getBuyBoxPercentage());
            
            // 转化率数据
            performanceDO.setCvr(dto.getCvr() != null ? dto.getCvr() : BigDecimal.ZERO);
            performanceDO.setCtr(dto.getCtr() != null ? dto.getCtr() : BigDecimal.ZERO);
            performanceDO.setVolumeCvr(dto.getVolumeCvr() != null ? dto.getVolumeCvr() : BigDecimal.ZERO);
            performanceDO.setAdCvr(dto.getAdCvr() != null ? dto.getAdCvr() : BigDecimal.ZERO);
            
            // 广告数据
            performanceDO.setImpressions(dto.getImpressions() != null ? dto.getImpressions() : 0);
            performanceDO.setAdClicks(dto.getClicks() != null ? dto.getClicks() : 0); // 注意：API中clicks字段可能是广告点击
            performanceDO.setAdOrderQuantity(dto.getAdOrderQuantity() != null ? dto.getAdOrderQuantity() : 0);
            performanceDO.setAdSalesAmount(dto.getAdSalesAmount() != null ? dto.getAdSalesAmount() : BigDecimal.ZERO);
            performanceDO.setSpend(dto.getSpend() != null ? dto.getSpend() : BigDecimal.ZERO);
            performanceDO.setCpc(dto.getCpc());
            performanceDO.setCpm(dto.getCpm());
            performanceDO.setCpo(dto.getCpo());
            performanceDO.setAcos(dto.getAcos() != null ? dto.getAcos() : BigDecimal.ZERO);
            performanceDO.setAcoas(dto.getAcoas() != null ? dto.getAcoas() : BigDecimal.ZERO);
            performanceDO.setRoas(dto.getRoas() != null ? dto.getRoas() : BigDecimal.ZERO);
            performanceDO.setAsoas(dto.getAsoas() != null ? dto.getAsoas() : BigDecimal.ZERO);
            performanceDO.setAdvRate(dto.getAdvRate() != null ? dto.getAdvRate() : BigDecimal.ZERO);
            
            // 计算TACOS = 广告花费 / 净销售额 (取绝对值)
            if (dto.getNetAmount() != null && dto.getNetAmount().compareTo(BigDecimal.ZERO) > 0 && dto.getSpend() != null) {
                BigDecimal tacos = dto.getSpend().divide(dto.getNetAmount(), 4, BigDecimal.ROUND_HALF_UP).abs();
                performanceDO.setTacos(tacos);
            } else {
                performanceDO.setTacos(BigDecimal.ZERO);
            }
            
            // 广告细分数据
            performanceDO.setAdsSpCost(dto.getAdsSpCost() != null ? dto.getAdsSpCost() : BigDecimal.ZERO);
            performanceDO.setAdsSpSales(dto.getAdsSpSales() != null ? dto.getAdsSpSales() : BigDecimal.ZERO);
            performanceDO.setAdsSdCost(dto.getAdsSdCost() != null ? dto.getAdsSdCost() : BigDecimal.ZERO);
            performanceDO.setAdsSdSales(dto.getAdsSdSales() != null ? dto.getAdsSdSales() : BigDecimal.ZERO);
            performanceDO.setSharedAdsSbCost(dto.getSharedAdsSbCost() != null ? dto.getSharedAdsSbCost() : BigDecimal.ZERO);
            performanceDO.setSharedAdsSbSales(dto.getSharedAdsSbSales() != null ? dto.getSharedAdsSbSales() : BigDecimal.ZERO);
            performanceDO.setSharedAdsSbvCost(dto.getSharedAdsSbvCost() != null ? dto.getSharedAdsSbvCost() : BigDecimal.ZERO);
            performanceDO.setSharedAdsSbvSales(dto.getSharedAdsSbvSales() != null ? dto.getSharedAdsSbvSales() : BigDecimal.ZERO);
            performanceDO.setSharedCostOfAdvertising(dto.getSharedCostOfAdvertising() != null ? dto.getSharedCostOfAdvertising() : BigDecimal.ZERO);
            performanceDO.setAdDirectSalesAmount(dto.getAdDirectSalesAmount() != null ? dto.getAdDirectSalesAmount() : BigDecimal.ZERO);
            performanceDO.setAdDirectOrderQuantity(dto.getAdDirectOrderQuantity() != null ? dto.getAdDirectOrderQuantity() : 0);
            
            // 排名数据
            performanceDO.setCateRank(dto.getCateRank() != null ? dto.getCateRank() : 0);
            performanceDO.setPrevCateRank(dto.getPrevCateRank() != null ? dto.getPrevCateRank() : 0);
            performanceDO.setRankCategory(dto.getRankCategory());
            
            // 转换小类排名
            if (CollUtil.isNotEmpty(dto.getSmallCateRank())) {
                List<ProductPerformanceDO.SmallCateRank> smallCateRankList = dto.getSmallCateRank().stream()
                    .map(rank -> ProductPerformanceDO.SmallCateRank.builder()
                        .category(rank.getCategory())
                        .rank(rank.getRank())
                        .prevRank(rank.getPrevRank())
                        .build())
                    .collect(Collectors.toList());
                // 保存完整的小类排名列表到详细字段
                performanceDO.setSmallCateRankDetail(smallCateRankList);
                
                // 取第一个小类的排名作为主要的小类排名（与大类排名保持一致的存储方式）
                if (!smallCateRankList.isEmpty()) {
                    ProductPerformanceDO.SmallCateRank firstSmallRank = smallCateRankList.get(0);
                    performanceDO.setSmallCateRank(firstSmallRank.getRank());
                    performanceDO.setPrevSmallCateRank(firstSmallRank.getPrevRank());
                    performanceDO.setSmallRankCategory(firstSmallRank.getCategory());
                }
            }
            
            // 其他字段
            performanceDO.setCategories(dto.getCategories());
            performanceDO.setBrands(dto.getBrands());
            performanceDO.setPrincipalNames(dto.getPrincipalNames());
            performanceDO.setDeveloperNames(dto.getDeveloperNames());
            performanceDO.setSuppliers(dto.getSuppliers());
            performanceDO.setAttributes(dto.getAttributes());
            
            // 转换标签信息
            if (CollUtil.isNotEmpty(dto.getTagSet())) {
                List<ProductPerformanceDO.TagSet> tagSetList = dto.getTagSet().stream()
                    .map(tag -> ProductPerformanceDO.TagSet.builder()
                        .globalTagId(tag.getGlobalTagId())
                        .tagName(tag.getTagName())
                        .color(tag.getColor())
                        .build())
                    .collect(Collectors.toList());
                performanceDO.setTagSet(tagSetList);
            }
            
            // 转换价格列表
            if (CollUtil.isNotEmpty(dto.getPriceList())) {
                List<ProductPerformanceDO.PriceInfo> priceInfoList = dto.getPriceList().stream()
                    .map(price -> ProductPerformanceDO.PriceInfo.builder()
                        .localName(price.getLocalName())
                        .localSku(price.getLocalSku())
                        .sellerSku(price.getSellerSku())
                        .price(price.getPrice())
                        .country(price.getCountry())
                        .sellerName(price.getSellerName())
                        .mid(price.getMid())
                        .sid(price.getSid())
                        .volume(price.getVolume())
                        .smallImageUrl(price.getSmallImageUrl())
                        .status(price.getStatus())
                        .build())
                    .collect(Collectors.toList());
                performanceDO.setPriceList(priceInfoList);
            }
            
            // SKU维度特有字段
            performanceDO.setCgPrice(dto.getCgPrice());
            performanceDO.setWhsValue(dto.getWhsValue());
            performanceDO.setLocalQuantity(dto.getLocalQuantity());
            performanceDO.setOverseaQuantity(dto.getOverseaQuantity());
            performanceDO.setAvgLandedPrice(dto.getAvgLandedPrice());
            performanceDO.setModel(dto.getModel());
            
            // 业务字段
            performanceDO.setSummaryField("asin"); // 默认按ASIN汇总
            performanceDO.setStartDate(startDate);
            performanceDO.setEndDate(endDate);
            performanceDO.setSyncTime(LocalDateTime.now());
            
            // 总流量 = sessions_total
            performanceDO.setTotalTraffic(dto.getSessionsTotal() != null ? dto.getSessionsTotal() : 0);
            
            // 总销售额 = amount
            performanceDO.setTotalSalesAmount(dto.getAmount() != null ? dto.getAmount() : BigDecimal.ZERO);
            
            // 匹配并填充库龄数据
            if (!fbaAgeMap.isEmpty() && performanceDO.getAsin() != null && performanceDO.getSid() != null) {
                String key = performanceDO.getAsin() + "_" + performanceDO.getSid();
                FbaAgeListDTO ageData = fbaAgeMap.get(key);
                
                if (ageData != null) {
                    // 填充库龄字段
                    performanceDO.setInventoryAge0To90(ageData.getInvAge0To90Days());
                    performanceDO.setInventoryAge91To180(ageData.getInvAge91To180Days());
                    performanceDO.setInvAge181To270Days(ageData.getInvAge181To270Days());
                    performanceDO.setInvAge271To365Days(ageData.getInvAge271To365Days());
                    performanceDO.setInvAge365PlusDays(ageData.getInvAge365PlusDays());
                    
                    log.debug("匹配到库龄数据: ASIN={}, SID={}, 0-90天库龄={}", 
                        performanceDO.getAsin(), performanceDO.getSid(), ageData.getInvAge0To90Days());
                }
            }
            
            // 再次确保必填字段有值
            if (performanceDO.getTenantId() == null) {
                performanceDO.setTenantId(1L);
            }
            if (performanceDO.getCreator() == null) {
                performanceDO.setCreator("1");
            }
            if (performanceDO.getUpdater() == null) {
                performanceDO.setUpdater("1");
            }
            
            // 手动设置deleted字段为false（MyBatis Plus会自动转换为0）
            try {
                // 由于BaseDO的deleted是Boolean类型，我们需要设置为false
                java.lang.reflect.Field deletedField = BaseDO.class.getDeclaredField("deleted");
                deletedField.setAccessible(true);
                deletedField.set(performanceDO, Boolean.FALSE);
            } catch (Exception e) {
                log.warn("设置deleted字段失败", e);
            }
            
            return performanceDO;
        }).collect(Collectors.toList());
    }
}