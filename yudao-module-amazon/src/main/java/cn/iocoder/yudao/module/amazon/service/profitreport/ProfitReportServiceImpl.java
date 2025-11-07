package cn.iocoder.yudao.module.amazon.service.profitreport;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo.*;
import cn.iocoder.yudao.module.amazon.dal.dataobject.profitreport.ProfitReportDO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.ProfitReportDTO;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.amazon.dal.mysql.profitreport.ProfitReportMapper;
import cn.iocoder.yudao.module.amazon.service.shops.ShopsService;
import cn.iocoder.yudao.module.amazon.dal.dataobject.shops.ShopsDO;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.amazon.enums.ErrorCodeConstants.*;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;

/**
 * 亚马逊利润报表数据 Service 实现类
 *
 * @author Demons
 */
@Service
@Validated
@Slf4j
public class ProfitReportServiceImpl implements ProfitReportService {

    @Resource
    private ProfitReportMapper profitReportMapper;
    
    @Resource
    private LingXingApiService lingXingApiService;
    
    @Resource
    private ShopsService shopsService;

    @Override
    public Long createProfitReport(ProfitReportSaveReqVO createReqVO) {
        // 插入
        ProfitReportDO profitReport = BeanUtils.toBean(createReqVO, ProfitReportDO.class);
        profitReportMapper.insert(profitReport);

        // 返回
        return profitReport.getId();
    }

    @Override
    public void updateProfitReport(ProfitReportSaveReqVO updateReqVO) {
        // 校验存在
        validateProfitReportExists(updateReqVO.getId());
        // 更新
        ProfitReportDO updateObj = BeanUtils.toBean(updateReqVO, ProfitReportDO.class);
        profitReportMapper.updateById(updateObj);
    }

    @Override
    public void deleteProfitReport(Long id) {
        // 校验存在
        validateProfitReportExists(id);
        // 删除
        profitReportMapper.deleteById(id);
    }

    @Override
        public void deleteProfitReportListByIds(List<Long> ids) {
        // 删除
        profitReportMapper.deleteByIds(ids);
        }


    private void validateProfitReportExists(Long id) {
        if (profitReportMapper.selectById(id) == null) {
            throw exception(PROFIT_REPORT_NOT_EXISTS);
        }
    }

    @Override
    public ProfitReportDO getProfitReport(Long id) {
        return profitReportMapper.selectById(id);
    }

    @Override
    public PageResult<ProfitReportDO> getProfitReportPage(ProfitReportPageReqVO pageReqVO) {
        return profitReportMapper.selectPage(pageReqVO);
    }
    
    @Override
    public Integer syncProfitReport(ProfitReportSyncReqVO syncReqVO) {
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
        return doSyncProfitReport(syncReqVO);
    }
    
    @Transactional(rollbackFor = Exception.class)
    protected Integer doSyncProfitReport(ProfitReportSyncReqVO syncReqVO) {
        log.info("开始同步利润报表数据: {} 至 {}", syncReqVO.getStartDate(), syncReqVO.getEndDate());
        
        int totalSynced = 0;
        
        try {
            // 获取要同步的店铺列表
            List<Long> sidsToSync = syncReqVO.getSids();
            if (CollUtil.isEmpty(sidsToSync)) {
                // 如果没有指定店铺，则获取所有活跃店铺
                List<ShopsDO> activeShops = shopsService.getAllShopsActive();
                if (CollUtil.isNotEmpty(activeShops)) {
                    sidsToSync = activeShops.stream()
                        .map(shop -> shop.getSid().longValue())
                        .collect(Collectors.toList());
                    log.info("未指定店铺，将同步所有活跃店铺，共 {} 个", sidsToSync.size());
                }
            }
            
            if (CollUtil.isEmpty(sidsToSync)) {
                log.warn("没有可同步的店铺");
                return 0;
            }
            
            // 按店铺ID依次同步
            for (Long sid : sidsToSync) {
                log.info("========== 开始同步店铺 {} 的数据 ==========", sid);
                int shopSynced = 0;
                LocalDate currentDate = syncReqVO.getStartDate();
                LocalDate endDate = syncReqVO.getEndDate();
                
                // 构造单个店铺的SID列表
                List<Long> singleSidList = Collections.singletonList(sid);
                
                // 按天循环同步数据
                while (!currentDate.isAfter(endDate)) {
                    log.info("店铺 {} - 正在同步日期: {}", sid, currentDate);
                    
                    // 1. 调用领星API获取当天数据（只传单个店铺ID）
                    List<ProfitReportDTO> apiData = lingXingApiService.getAllProfitReportData(
                        currentDate.toString(),
                        currentDate.toString(),  // 同一天
                        singleSidList,
                        syncReqVO.getAsins()
                    );
                    
                    if (CollUtil.isNotEmpty(apiData)) {
                        // 2. 如果是全量同步，先删除当天的旧数据
                        if ("FULL".equals(syncReqVO.getSyncType())) {
                            deleteOldDataByDate(currentDate, singleSidList);
                        }
                        
                        // 3. 转换并保存数据
                        List<ProfitReportDO> entities = convertToEntities(apiData, currentDate);
                        
                        // 4. 处理唯一键冲突（sync_date + asin）
                        // 如果是增量同步，跳过已存在的记录
                        if ("INCREMENTAL".equals(syncReqVO.getSyncType())) {
                            entities = filterExistingRecords(entities, currentDate);
                        }
                        
                        if (CollUtil.isNotEmpty(entities)) {
                            saveInBatches(entities);
                            shopSynced += entities.size();
                            log.info("店铺 {} - 日期 {} 同步完成，同步 {} 条数据", sid, currentDate, entities.size());
                        }
                    } else {
                        log.warn("店铺 {} - 日期 {} 未获取到数据", sid, currentDate);
                    }
                    
                    // 移到下一天
                    currentDate = currentDate.plusDays(1);
                }
                
                totalSynced += shopSynced;
                log.info("========== 店铺 {} 同步完成，同步 {} 条数据 ==========", sid, shopSynced);
            }
            
            log.info("同步利润报表完成，共同步 {} 条数据", totalSynced);
            return totalSynced;
            
        } catch (Exception e) {
            log.error("同步利润报表失败", e);
            throw new RuntimeException("同步利润报表失败: " + e.getMessage(), e);
        }
    }
    
    private void deleteOldDataByDate(LocalDate syncDate, List<Long> sids) {
        // 转换 Long 类型的 sids 为 String 类型
        List<String> sidStrings = null;
        if (CollUtil.isNotEmpty(sids)) {
            sidStrings = sids.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        }
        
        int deletedCount = profitReportMapper.deleteByDateAndSids(syncDate, sidStrings);
        log.info("删除日期 {} 的旧数据，删除了 {} 条记录", syncDate, deletedCount);
    }
    
    private List<ProfitReportDO> filterExistingRecords(List<ProfitReportDO> entities, LocalDate syncDate) {
        if (CollUtil.isEmpty(entities)) {
            return entities;
        }
        
        // 获取所有ASIN
        List<String> asins = entities.stream()
            .map(ProfitReportDO::getAsin)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        
        // 查询已存在的记录
        List<String> existingAsins = profitReportMapper.selectExistingAsins(syncDate, asins);
        
        if (CollUtil.isEmpty(existingAsins)) {
            return entities;
        }
        
        // 过滤掉已存在的记录
        Set<String> existingSet = new HashSet<>(existingAsins);
        return entities.stream()
            .filter(entity -> !existingSet.contains(entity.getAsin()))
            .collect(Collectors.toList());
    }
    
    private List<ProfitReportDO> convertToEntities(List<ProfitReportDTO> dtoList, LocalDate syncDate) {
        // 批量获取所有SKU的SPU映射
        List<String> skuList = dtoList.stream()
            .map(ProfitReportDTO::getLocalSku)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        
        Map<String, Map<String, String>> skuSpuMap = new HashMap<>();
        if (CollUtil.isNotEmpty(skuList)) {
            skuSpuMap = lingXingApiService.getSpuBySkuBatch(skuList);
        }
        
        final Map<String, Map<String, String>> finalSkuSpuMap = skuSpuMap;
        
        return dtoList.stream().map(dto -> {
            ProfitReportDO entity = new ProfitReportDO();
            
            // 设置同步日期（按天）
            entity.setSyncDate(syncDate);
            
            // 基础字段映射
            entity.setAsin(dto.getAsin());
            entity.setParentAsin(dto.getParentAsin());
            entity.setLocalSku(dto.getLocalSku());
            
            // 通过SKU获取SPU，如果获取不到则设置为空字符串
            String spu = "";
            if (StrUtil.isNotBlank(dto.getLocalSku())) {
                Map<String, String> spuInfo = finalSkuSpuMap.get(dto.getLocalSku());
                if (spuInfo != null && spuInfo.get("spu") != null) {
                    spu = spuInfo.get("spu");
                }
            }
            entity.setSpu(spu);
            entity.setSid(dto.getSid());
            entity.setStoreName(dto.getStoreName());
            entity.setCountryCode(dto.getCountryCode());
            entity.setLocalName(dto.getLocalName());
            entity.setCurrencyCode(dto.getCurrencyCode());
            entity.setTransactionStatus(dto.getTransactionStatus());
            
            // 销售数量映射
            entity.setTotalSalesQuantity(dto.getTotalSalesQuantity());
            entity.setFbaSalesQuantity(dto.getFbaSalesQuantity());
            entity.setFbmSalesQuantity(dto.getFbmSalesQuantity());
            entity.setRefundsQuantity(dto.getRefundsQuantity());
            entity.setFbaReturnsQuantity(dto.getFbaReturnsQuantity());
            
            // 销售金额映射
            entity.setTotalSalesAmount(dto.getTotalSalesAmount());
            entity.setFbaSaleAmount(dto.getFbaSaleAmount());
            entity.setFbmSaleAmount(dto.getFbmSaleAmount());
            entity.setTotalSalesRefunds(dto.getTotalSalesRefunds());
            
            // 广告数据映射
            entity.setTotalAdsSales(dto.getTotalAdsSales());
            entity.setTotalAdsCost(dto.getTotalAdsCost());
            entity.setAdsSpSales(dto.getAdsSpSales());
            entity.setAdsSpCost(dto.getAdsSpCost());
            entity.setAdsSdSales(dto.getAdsSdSales());
            entity.setAdsSdCost(dto.getAdsSdCost());
            entity.setAdsSbSales(dto.getSharedAdsSbSales());
            entity.setAdsSbCost(dto.getAdsSbCost());
            
            // 费用映射
            entity.setPlatformFee(dto.getPlatformFee());
            entity.setTotalFbaDeliveryFee(dto.getTotalFbaDeliveryFee());
            entity.setTotalStorageFee(dto.getTotalStorageFee());
            entity.setPromotionFee(dto.getPromotionFee());
            
            // 成本映射
            entity.setCgPriceTotal(dto.getCgPriceTotal());
            entity.setCgTransportCostsTotal(dto.getCgTransportCostsTotal());
            entity.setCgOtherCostsTotal(dto.getCgOtherCostsTotal());
            entity.setTotalCost(dto.getTotalCost());
            
            // 利润映射
            entity.setGrossProfit(dto.getGrossProfit());
            entity.setGrossRate(dto.getGrossRate());
            
            // ==================== 新增字段映射 ====================
            
            // 1. 收入相关字段映射
            entity.setShippingCredits(dto.getShippingCredits());
            entity.setPromotionalRebates(dto.getPromotionalRebates());
            entity.setFbaInventoryCredit(dto.getFbaInventoryCredit());
            entity.setCashOnDelivery(dto.getCashOnDelivery());
            entity.setOtherInAmount(dto.getOtherInAmount());
            entity.setGiftWrapCredits(dto.getGiftWrapCredits());         // 包装收入
            entity.setGuaranteeClaims(dto.getGuaranteeClaims());         // 买家交易保障索赔额
            entity.setCostOfPointsGranted(dto.getCostOfPointsGranted()); // 积分抵减收入
            
            // 2. 退款相关字段映射
            entity.setFbaSalesRefunds(dto.getFbaSalesRefunds());
            entity.setFbmSalesRefunds(dto.getFbmSalesRefunds());
            entity.setTotalFeeRefunds(dto.getTotalFeeRefunds());
            entity.setSellingFeeRefunds(dto.getSellingFeeRefunds());
            entity.setFbaTransactionFeeRefunds(dto.getFbaTransactionFeeRefunds());
            entity.setOtherTransactionFeeRefunds(dto.getOtherTransactionFeeRefunds());
            entity.setShippingCreditRefunds(dto.getShippingCreditRefunds());
            
            // 3. 费用相关字段映射（细分项）
            entity.setFbaDeliveryFee(dto.getFbaDeliveryFee());
            entity.setMcFbaDeliveryFee(dto.getMcFbaDeliveryFee());
            entity.setOtherTransactionFees(dto.getOtherTransactionFees());
            entity.setAdjustments(dto.getAdjustments());
            entity.setTotalPlatformOtherFee(dto.getTotalPlatformOtherFee());
            
            // 4. 成本相关字段映射（单价）
            entity.setCgUnitPrice(dto.getCgUnitPrice());
            entity.setCgTransportUnitCosts(dto.getCgTransportUnitCosts());
            
            // 5. 库存相关字段映射
            entity.setFbaInventoryCreditQuantity(dto.getFbaInventoryCreditQuantity());
            
            // 6. 站外推广费相关字段映射
            entity.setCustomOrderFee(dto.getCustomOrderFee());
            entity.setCustomOrderFeePrincipal(dto.getCustomOrderFeePrincipal());
            entity.setCustomOrderFeeCommission(dto.getCustomOrderFeeCommission());
            
            // 7. 额外的业务字段映射
            entity.setRefundsRate(dto.getRefundsRate());
            entity.setFbaReturnsQuantityRate(dto.getFbaReturnsQuantityRate());
            
            // 8. 其他费用计算所需字段映射
            entity.setSharedFbaInboundTransportationProgramFee(dto.getSharedFbaInboundTransportationProgramFee());
            entity.setSharedFbaIntegerernationalInboundFee(dto.getSharedFbaIntegerernationalInboundFee());
            
            // 其他费用明细 - 确保始终设置值，即使是空数组
            if (dto.getOtherFeeStr() != null && !dto.getOtherFeeStr().isEmpty()) {
                String jsonStr = JSONUtil.toJsonStr(dto.getOtherFeeStr());
                entity.setOtherFeeStr(jsonStr);
                log.info("ASIN: {}, otherFeeStr原始值: {}, 转换后: {}", 
                    dto.getAsin(), dto.getOtherFeeStr(), jsonStr);
            } else {
                // 如果为null或空，设置为空数组
                entity.setOtherFeeStr("[]");
                log.info("ASIN: {}, otherFeeStr为空，设置为[]", dto.getAsin());
            }
            
            // 设置同步时间
            entity.setSyncTime(LocalDateTime.now());
            

            
            return entity;
        }).collect(Collectors.toList());
    }
    
    private void saveInBatches(List<ProfitReportDO> entities) {
        if (CollUtil.isEmpty(entities)) {
            return;
        }
        
        int totalCount = entities.size();
        log.info("准备批量保存 {} 条利润报表数据", totalCount);
        
        // 使用批量保存或更新的优化策略
        int insertCount = 0;
        int updateCount = 0;
        int batchSize = 500;
        
        // 按批次处理，避免一次性查询太多数据
        for (int i = 0; i < entities.size(); i += batchSize) {
            int end = Math.min(i + batchSize, entities.size());
            List<ProfitReportDO> batch = entities.subList(i, end);
            
            try {
                // 构建查询条件：根据 sync_date + asin + sid 查询已存在的记录
                List<ProfitReportDO> existingRecords = queryExistingRecords(batch);
                Map<String, ProfitReportDO> existingMap = buildUniqueKeyMap(existingRecords);
                
                // 分离需要插入和更新的记录
                List<ProfitReportDO> toInsert = new ArrayList<>();
                List<ProfitReportDO> toUpdate = new ArrayList<>();
                
                for (ProfitReportDO entity : batch) {
                    String uniqueKey = buildUniqueKey(entity);
                    ProfitReportDO existing = existingMap.get(uniqueKey);
                    
                    if (existing != null) {
                        // 更新记录：保留原有ID，更新其他字段
                        entity.setId(existing.getId());
                        entity.setCreateTime(existing.getCreateTime());
                        entity.setCreator(existing.getCreator());
                        toUpdate.add(entity);
                    } else {
                        // 新增记录
                        toInsert.add(entity);
                    }
                }
                
                // 执行批量插入
                if (!toInsert.isEmpty()) {
                    Boolean insertResult = profitReportMapper.insertBatch(toInsert);
                    if (Boolean.TRUE.equals(insertResult)) {
                        insertCount += toInsert.size();
                        log.debug("批量插入成功: {} 条", toInsert.size());
                    }
                }
                
                // 执行批量更新
                if (!toUpdate.isEmpty()) {
                    Boolean updateResult = profitReportMapper.updateBatch(toUpdate);
                    if (Boolean.TRUE.equals(updateResult)) {
                        updateCount += toUpdate.size();
                        log.debug("批量更新成功: {} 条", toUpdate.size());
                    }
                }
                
            } catch (Exception e) {
                log.error("批量保存失败，批次: {}-{}，错误: {}", i, end, e.getMessage(), e);
                // 可以选择继续处理下一批，或者抛出异常中断
                throw new RuntimeException("批量保存利润报表数据失败", e);
            }
        }
        
        log.info("批量保存完成，总计: {} 条，新增: {} 条，更新: {} 条", 
            totalCount, insertCount, updateCount);
    }
    
    /**
     * 查询已存在的记录
     */
    private List<ProfitReportDO> queryExistingRecords(List<ProfitReportDO> entities) {
        if (CollUtil.isEmpty(entities)) {
            return Collections.emptyList();
        }
        
        // 构建查询条件
        LambdaQueryWrapperX<ProfitReportDO> wrapper = new LambdaQueryWrapperX<>();
        
        // 使用 OR 条件查询所有可能存在的记录
        wrapper.and(w -> {
            for (ProfitReportDO entity : entities) {
                w.or(orWrapper -> orWrapper
                    .eq(ProfitReportDO::getSyncDate, entity.getSyncDate())
                    .eq(ProfitReportDO::getAsin, entity.getAsin())
                    .eq(ProfitReportDO::getSid, entity.getSid())
                );
            }
        });
        
        return profitReportMapper.selectList(wrapper);
    }
    
    /**
     * 构建唯一键映射
     */
    private Map<String, ProfitReportDO> buildUniqueKeyMap(List<ProfitReportDO> records) {
        Map<String, ProfitReportDO> map = new HashMap<>();
        for (ProfitReportDO record : records) {
            map.put(buildUniqueKey(record), record);
        }
        return map;
    }
    
    /**
     * 构建唯一键
     */
    private String buildUniqueKey(ProfitReportDO entity) {
        return entity.getSyncDate() + "_" + entity.getAsin() + "_" + entity.getSid();
    }
    
    @Override
    public PageResult<ProfitReportSpuAggVO> aggregateBySpu(ProfitReportQueryVO queryVO) {
        // 1. 查询原始数据
        List<ProfitReportDO> rawData = queryRawData(queryVO);
        
        // 2. 按SPU分组（优先使用spu字段，其次parent_asin）
        Map<String, List<ProfitReportDO>> groupedData = rawData.stream()
            .collect(Collectors.groupingBy(item -> {
                if (StrUtil.isNotBlank(item.getSpu())) {
                    return item.getSpu();
                }
                if (StrUtil.isNotBlank(item.getParentAsin())) {
                    return item.getParentAsin();
                }
                return item.getAsin();
            }));
        
        // 3. 计算聚合数据
        List<ProfitReportSpuAggVO> aggregatedList = groupedData.entrySet().stream()
            .map(entry -> buildSpuAggVO(entry.getKey(), entry.getValue()))
            .sorted((a, b) -> compareByBigDecimal(b.getTotalSalesAmount(), a.getTotalSalesAmount()))
            .collect(Collectors.toList());
        
        return paginateInMemory(aggregatedList, queryVO);
    }
    
    @Override
    public PageResult<ProfitReportSpuSidAggVO> aggregateBySpuAndSid(ProfitReportQueryVO queryVO) {
        // 1. 查询原始数据
        List<ProfitReportDO> rawData = queryRawData(queryVO);
        
        // 2. 按SPU+SID复合键分组（同时过滤掉SPU或SID为空的数据）
        Map<String, List<ProfitReportDO>> groupedData = rawData.stream()
            .filter(item -> StrUtil.isNotBlank(item.getSpu()) && StrUtil.isNotBlank(item.getSid()))
            .collect(Collectors.groupingBy(item -> {
                // 使用SPU_SID作为分组键
                return item.getSpu() + "_" + item.getSid();
            }));
        
        // 3. 计算聚合数据
        List<ProfitReportSpuSidAggVO> aggregatedList = groupedData.entrySet().stream()
            .map(entry -> buildSpuSidAggVO(entry.getKey(), entry.getValue()))
            .sorted((a, b) -> {
                // 先按SPU排序，再按销售额排序
                int spuCompare = a.getSpu().compareTo(b.getSpu());
                if (spuCompare != 0) {
                    return spuCompare;
                }
                return compareByBigDecimal(b.getTotalSalesAmount(), a.getTotalSalesAmount());
            })
            .collect(Collectors.toList());
        
        return paginateInMemory(aggregatedList, queryVO);
    }
    
    @Override
    public PageResult<ProfitReportShopAggVO> aggregateBySid(ProfitReportQueryVO queryVO) {
        // 1. 查询原始数据
        List<ProfitReportDO> rawData = queryRawData(queryVO);
        
        // 2. 按店铺ID分组
        Map<String, List<ProfitReportDO>> groupedData = rawData.stream()
            .filter(item -> StrUtil.isNotBlank(item.getSid()))
            .collect(Collectors.groupingBy(ProfitReportDO::getSid));
        
        // 3. 计算聚合数据
        List<ProfitReportShopAggVO> aggregatedList = groupedData.entrySet().stream()
            .map(entry -> buildShopAggVO(entry.getKey(), entry.getValue()))
            .sorted((a, b) -> compareByBigDecimal(b.getTotalSalesAmount(), a.getTotalSalesAmount()))
            .collect(Collectors.toList());
        
        return paginateInMemory(aggregatedList, queryVO);
    }
    
    private List<ProfitReportDO> queryRawData(ProfitReportQueryVO queryVO) {
        // 转换 Long 类型的 sids 为 String 类型
        List<String> sidStrings = null;
        if (CollUtil.isNotEmpty(queryVO.getSids())) {
            sidStrings = queryVO.getSids().stream()
                .map(String::valueOf)
                .collect(Collectors.toList());
        }
        
        return profitReportMapper.selectListForAggregation(
            queryVO.getStartDate(),
            queryVO.getEndDate(),
            sidStrings,
            queryVO.getAsins(),
            queryVO.getSkus()
        );
    }
    
    private ProfitReportSkuAggVO buildSkuAggVO(String sku, List<ProfitReportDO> skuData) {
        ProfitReportSkuAggVO vo = new ProfitReportSkuAggVO();
        vo.setLocalSku(sku);
        vo.setLocalName(skuData.get(0).getLocalName());
        
        // 聚合销售数量
        vo.setTotalSalesQuantity(sumInteger(skuData, ProfitReportDO::getTotalSalesQuantity));
        vo.setFbaSalesQuantity(sumInteger(skuData, ProfitReportDO::getFbaSalesQuantity));
        vo.setFbmSalesQuantity(sumInteger(skuData, ProfitReportDO::getFbmSalesQuantity));
        vo.setRefundsQuantity(sumInteger(skuData, ProfitReportDO::getRefundsQuantity));
        
        // 聚合金额
        vo.setTotalSalesAmount(sumBigDecimal(skuData, ProfitReportDO::getTotalSalesAmount));
        vo.setFbaSaleAmount(sumBigDecimal(skuData, ProfitReportDO::getFbaSaleAmount));
        vo.setFbmSaleAmount(sumBigDecimal(skuData, ProfitReportDO::getFbmSaleAmount));
        
        // 计算退货退款 = 销售退款额 + 费用退款额
        BigDecimal salesRefunds = sumBigDecimal(skuData, ProfitReportDO::getTotalSalesRefunds);
        BigDecimal feeRefunds = sumBigDecimal(skuData, ProfitReportDO::getTotalFeeRefunds);
        vo.setTotalSalesRefunds(salesRefunds);  // 单独设置收入退款项
        vo.setTotalFeeRefunds(feeRefunds);      // 单独设置费用退款项
        vo.setTotalRefunds(safeAdd(salesRefunds, feeRefunds));
        
        // 成本相关
        vo.setCgPriceTotal(sumBigDecimal(skuData, ProfitReportDO::getCgPriceTotal));
        vo.setCgTransportCostsTotal(sumBigDecimal(skuData, ProfitReportDO::getCgTransportCostsTotal));
        vo.setCgOtherCostsTotal(sumBigDecimal(skuData, ProfitReportDO::getCgOtherCostsTotal));
        
        // 净销售额明细字段聚合
        BigDecimal shippingCredits = sumBigDecimal(skuData, ProfitReportDO::getShippingCredits);
        BigDecimal promotionalRebates = sumBigDecimal(skuData, ProfitReportDO::getPromotionalRebates);
        BigDecimal fbaInventoryCredit = sumBigDecimal(skuData, ProfitReportDO::getFbaInventoryCredit);
        BigDecimal cashOnDelivery = sumBigDecimal(skuData, ProfitReportDO::getCashOnDelivery);
        BigDecimal otherInAmount = sumBigDecimal(skuData, ProfitReportDO::getOtherInAmount);
        
        // 单独设置每个字段
        vo.setShippingCredits(shippingCredits);
        vo.setPromotionalRebates(promotionalRebates);
        vo.setFbaInventoryCredit(fbaInventoryCredit);
        vo.setCashOnDelivery(cashOnDelivery);
        vo.setOtherInAmount(otherInAmount);
        
        // 其他业务收入 = 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入
        vo.setOtherRevenue(safeAdd(shippingCredits, promotionalRebates, fbaInventoryCredit, cashOnDelivery, otherInAmount));
        
        // 费用相关
        vo.setTotalAdsCost(sumBigDecimal(skuData, ProfitReportDO::getTotalAdsCost));
        vo.setTotalAdsSales(sumBigDecimal(skuData, ProfitReportDO::getTotalAdsSales));
        vo.setPlatformFee(sumBigDecimal(skuData, ProfitReportDO::getPlatformFee));
        vo.setTotalFbaDeliveryFee(sumBigDecimal(skuData, ProfitReportDO::getTotalFbaDeliveryFee));
        vo.setTotalStorageFee(sumBigDecimal(skuData, ProfitReportDO::getTotalStorageFee));
        vo.setPromotionFee(sumBigDecimal(skuData, ProfitReportDO::getPromotionFee));
        
        // 站外费和其他费用需要特殊处理
        vo.setOffsiteFee(calculateOffsiteFee(skuData));
        vo.setOtherFee(calculateOtherFee(skuData));
        
        vo.setTotalCost(sumBigDecimal(skuData, ProfitReportDO::getTotalCost));
        vo.setGrossProfit(sumBigDecimal(skuData, ProfitReportDO::getGrossProfit));
        
        // 计算率指标
        calculateRates(vo);
        
        return vo;
    }
    
    private ProfitReportSpuAggVO buildSpuAggVO(String spu, List<ProfitReportDO> spuData) {
        ProfitReportSpuAggVO vo = new ProfitReportSpuAggVO();
        vo.setSpu(spu);
        vo.setParentAsin(spuData.get(0).getParentAsin());
        
        // 收集SKU列表
        vo.setSkuList(spuData.stream()
            .map(ProfitReportDO::getLocalSku)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList()));
        vo.setSkuCount(vo.getSkuList().size());
        
        // 聚合销售数据
        vo.setTotalSalesQuantity(sumInteger(spuData, ProfitReportDO::getTotalSalesQuantity));
        vo.setTotalSalesAmount(sumBigDecimal(spuData, ProfitReportDO::getTotalSalesAmount));
        vo.setRefundsQuantity(sumInteger(spuData, ProfitReportDO::getRefundsQuantity));
        // 计算退货退款 = 销售退款额 + 费用退款额
        BigDecimal salesRefunds = sumBigDecimal(spuData, ProfitReportDO::getTotalSalesRefunds);
        BigDecimal feeRefunds = sumBigDecimal(spuData, ProfitReportDO::getTotalFeeRefunds);
        vo.setTotalSalesRefunds(salesRefunds);  // 设置收入退款项
        vo.setTotalFeeRefunds(feeRefunds);      // 设置费用退款项
        vo.setTotalRefunds(safeAdd(salesRefunds, feeRefunds));
        
        // 成本相关
        vo.setCgPriceTotal(sumBigDecimal(spuData, ProfitReportDO::getCgPriceTotal));
        vo.setCgTransportCostsTotal(sumBigDecimal(spuData, ProfitReportDO::getCgTransportCostsTotal));
        vo.setCgOtherCostsTotal(sumBigDecimal(spuData, ProfitReportDO::getCgOtherCostsTotal));
        
        // 净销售额明细字段聚合
        BigDecimal shippingCredits = sumBigDecimal(spuData, ProfitReportDO::getShippingCredits);
        BigDecimal promotionalRebates = sumBigDecimal(spuData, ProfitReportDO::getPromotionalRebates);
        BigDecimal fbaInventoryCredit = sumBigDecimal(spuData, ProfitReportDO::getFbaInventoryCredit);
        BigDecimal cashOnDelivery = sumBigDecimal(spuData, ProfitReportDO::getCashOnDelivery);
        BigDecimal otherInAmount = sumBigDecimal(spuData, ProfitReportDO::getOtherInAmount);
        BigDecimal giftWrapCredits = sumBigDecimal(spuData, ProfitReportDO::getGiftWrapCredits);
        BigDecimal guaranteeClaims = sumBigDecimal(spuData, ProfitReportDO::getGuaranteeClaims);
        BigDecimal costOfPointsGranted = sumBigDecimal(spuData, ProfitReportDO::getCostOfPointsGranted);
        
        // 单独设置每个字段
        vo.setShippingCredits(shippingCredits);
        vo.setPromotionalRebates(promotionalRebates);
        vo.setFbaInventoryCredit(fbaInventoryCredit);
        vo.setCashOnDelivery(cashOnDelivery);
        vo.setOtherInAmount(otherInAmount);
        vo.setGiftWrapCredits(giftWrapCredits);
        vo.setGuaranteeClaims(guaranteeClaims);
        vo.setCostOfPointsGranted(costOfPointsGranted);
        
        // 其他业务收入 = 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入 + 包装收入 + 买家交易保障索赔额 + 积分抵减收入
        vo.setOtherRevenue(safeAdd(shippingCredits, promotionalRebates, fbaInventoryCredit, cashOnDelivery, otherInAmount, giftWrapCredits, guaranteeClaims, costOfPointsGranted));
        
        // 费用相关
        vo.setTotalAdsCost(sumBigDecimal(spuData, ProfitReportDO::getTotalAdsCost));
        vo.setTotalAdsSales(sumBigDecimal(spuData, ProfitReportDO::getTotalAdsSales));
        vo.setPlatformFee(sumBigDecimal(spuData, ProfitReportDO::getPlatformFee));
        vo.setTotalFbaDeliveryFee(sumBigDecimal(spuData, ProfitReportDO::getTotalFbaDeliveryFee));
        vo.setTotalStorageFee(sumBigDecimal(spuData, ProfitReportDO::getTotalStorageFee));
        vo.setPromotionFee(sumBigDecimal(spuData, ProfitReportDO::getPromotionFee));
        
        // 站外费和其他费用需要特殊处理
        vo.setOffsiteFee(calculateOffsiteFee(spuData));
        vo.setOtherFee(calculateOtherFee(spuData));
        
        vo.setTotalCost(sumBigDecimal(spuData, ProfitReportDO::getTotalCost));
        vo.setGrossProfit(sumBigDecimal(spuData, ProfitReportDO::getGrossProfit));
        
        // 计算率指标
        // 修正：根据公式，净销售额 = 总销售额 + 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入 + 包装收入 + 买家交易保障索赔额 + 积分抵减收入 + 收入退款额 + 费用退款额
        // 总销售额 = FBA销售额 + FBM销售额，所以可以直接使用总销售额
        BigDecimal netSalesAmount = safeAdd(
            vo.getTotalSalesAmount(),     // 总销售额（已包含FBA+FBM）
            vo.getShippingCredits(),       // 买家运费
            vo.getPromotionalRebates(),    // 促销折扣
            vo.getFbaInventoryCredit(),    // FBA库存赔偿
            vo.getCashOnDelivery(),        // COD
            vo.getOtherInAmount(),         // 其他收入
            vo.getGiftWrapCredits(),       // 包装收入
            vo.getGuaranteeClaims(),       // 买家交易保障索赔额
            vo.getCostOfPointsGranted(),   // 积分抵减收入
            vo.getTotalSalesRefunds(),     // 收入退款额
            vo.getTotalFeeRefunds()        // 费用退款额
        );

        log.info("净销售额: {}", netSalesAmount);
        
        // 计算毛利率 = 毛利润 / 净销售额
        if (netSalesAmount != null && netSalesAmount.compareTo(BigDecimal.ZERO) > 0) {
            vo.setGrossRate(vo.getGrossProfit()
                .divide(netSalesAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
        
        if (vo.getTotalAdsSales() != null && vo.getTotalAdsSales().compareTo(BigDecimal.ZERO) > 0) {
            vo.setAcos(vo.getTotalAdsCost()
                .divide(vo.getTotalAdsSales(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
        
        return vo;
    }
    
    private ProfitReportSpuSidAggVO buildSpuSidAggVO(String spuSidKey, List<ProfitReportDO> data) {
        // 从复合键中解析SPU和SID
        String[] parts = spuSidKey.split("_");
        String spu = parts[0];
        String sid = parts.length > 1 ? parts[1] : "";
        
        ProfitReportSpuSidAggVO vo = new ProfitReportSpuSidAggVO();
        vo.setSpuSidKey(spuSidKey);
        vo.setSpu(spu);
        vo.setSid(sid);
        
        // 从数据中获取店铺和产品信息
        ProfitReportDO firstRecord = data.get(0);
        vo.setParentAsin(firstRecord.getParentAsin());
        vo.setStoreName(firstRecord.getStoreName());
        vo.setCountryCode(firstRecord.getCountryCode());
        
        // 收集SKU列表和ASIN列表
        vo.setSkuList(data.stream()
            .map(ProfitReportDO::getLocalSku)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList()));
        vo.setSkuCount(vo.getSkuList().size());
        
        vo.setAsinList(data.stream()
            .map(ProfitReportDO::getAsin)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList()));
        vo.setAsinCount(vo.getAsinList().size());
        
        // 统计天数
        long daysCount = data.stream()
            .map(ProfitReportDO::getSyncDate)
            .filter(Objects::nonNull)
            .distinct()
            .count();
        vo.setDaysCount(daysCount);
        
        // 聚合销售数据
        vo.setTotalSalesQuantity(sumInteger(data, ProfitReportDO::getTotalSalesQuantity));
        vo.setTotalSalesAmount(sumBigDecimal(data, ProfitReportDO::getTotalSalesAmount));
        vo.setRefundsQuantity(sumInteger(data, ProfitReportDO::getRefundsQuantity));
        
        // 计算退货退款
        BigDecimal salesRefunds = sumBigDecimal(data, ProfitReportDO::getTotalSalesRefunds);
        BigDecimal feeRefunds = sumBigDecimal(data, ProfitReportDO::getTotalFeeRefunds);
        vo.setTotalSalesRefunds(salesRefunds);
        vo.setTotalFeeRefunds(feeRefunds);
        vo.setTotalRefunds(safeAdd(salesRefunds, feeRefunds));
        
        // 成本相关
        vo.setCgPriceTotal(sumBigDecimal(data, ProfitReportDO::getCgPriceTotal));
        vo.setCgTransportCostsTotal(sumBigDecimal(data, ProfitReportDO::getCgTransportCostsTotal));
        vo.setCgOtherCostsTotal(sumBigDecimal(data, ProfitReportDO::getCgOtherCostsTotal));
        
        // 净销售额明细字段聚合
        BigDecimal shippingCredits = sumBigDecimal(data, ProfitReportDO::getShippingCredits);
        BigDecimal promotionalRebates = sumBigDecimal(data, ProfitReportDO::getPromotionalRebates);
        BigDecimal fbaInventoryCredit = sumBigDecimal(data, ProfitReportDO::getFbaInventoryCredit);
        BigDecimal cashOnDelivery = sumBigDecimal(data, ProfitReportDO::getCashOnDelivery);
        BigDecimal otherInAmount = sumBigDecimal(data, ProfitReportDO::getOtherInAmount);
        
        vo.setShippingCredits(shippingCredits);
        vo.setPromotionalRebates(promotionalRebates);
        vo.setFbaInventoryCredit(fbaInventoryCredit);
        vo.setCashOnDelivery(cashOnDelivery);
        vo.setOtherInAmount(otherInAmount);
        
        // 其他业务收入
        vo.setOtherRevenue(safeAdd(shippingCredits, promotionalRebates, fbaInventoryCredit, cashOnDelivery, otherInAmount));
        
        // 费用相关
        vo.setTotalAdsCost(sumBigDecimal(data, ProfitReportDO::getTotalAdsCost));
        vo.setTotalAdsSales(sumBigDecimal(data, ProfitReportDO::getTotalAdsSales));
        vo.setPlatformFee(sumBigDecimal(data, ProfitReportDO::getPlatformFee));
        vo.setTotalFbaDeliveryFee(sumBigDecimal(data, ProfitReportDO::getTotalFbaDeliveryFee));
        vo.setTotalStorageFee(sumBigDecimal(data, ProfitReportDO::getTotalStorageFee));
        vo.setPromotionFee(sumBigDecimal(data, ProfitReportDO::getPromotionFee));
        
        // 站外费和其他费用
        vo.setOffsiteFee(calculateOffsiteFee(data));
        vo.setOtherFee(calculateOtherFee(data));
        
        // 总费用
        vo.setTotalFees(safeAdd(
            vo.getPlatformFee(),
            vo.getTotalFbaDeliveryFee(),
            vo.getTotalStorageFee(),
            vo.getTotalAdsCost(),
            vo.getPromotionFee(),
            vo.getOffsiteFee(),
            vo.getOtherFee()
        ));
        
        vo.setTotalCost(sumBigDecimal(data, ProfitReportDO::getTotalCost));
        vo.setGrossProfit(sumBigDecimal(data, ProfitReportDO::getGrossProfit));
        
        // 计算率指标和平均值
        if (vo.getTotalSalesAmount() != null && vo.getTotalSalesAmount().compareTo(BigDecimal.ZERO) > 0) {
            vo.setGrossRate(vo.getGrossProfit()
                .divide(vo.getTotalSalesAmount(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
            
            // 计算店铺平均毛利率（可以与整体毛利率相同，或根据需求调整）
            vo.setAvgGrossRate(vo.getGrossRate());
        }
        
        if (vo.getTotalAdsSales() != null && vo.getTotalAdsSales().compareTo(BigDecimal.ZERO) > 0) {
            vo.setAcos(vo.getTotalAdsCost()
                .divide(vo.getTotalAdsSales(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
            vo.setRoas(vo.getTotalAdsSales()
                .divide(vo.getTotalAdsCost(), 2, RoundingMode.HALF_UP));
        }
        
        // 计算日均值
        if (daysCount > 0) {
            BigDecimal days = new BigDecimal(daysCount);
            if (vo.getTotalSalesQuantity() != null) {
                vo.setAvgDailySalesQuantity(new BigDecimal(vo.getTotalSalesQuantity())
                    .divide(days, 2, RoundingMode.HALF_UP));
            }
            if (vo.getTotalSalesAmount() != null) {
                vo.setAvgDailySalesAmount(vo.getTotalSalesAmount()
                    .divide(days, 2, RoundingMode.HALF_UP));
            }
        }
        
        return vo;
    }
    
    private ProfitReportShopAggVO buildShopAggVO(String sid, List<ProfitReportDO> shopData) {
        ProfitReportShopAggVO vo = new ProfitReportShopAggVO();
        vo.setSid(sid);
        vo.setStoreName(shopData.get(0).getStoreName());
        vo.setCountryCode(shopData.get(0).getCountryCode());
        
        // 统计SKU和ASIN数量
        vo.setSkuCount(shopData.stream()
            .map(ProfitReportDO::getLocalSku)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .count());
        
        vo.setAsinCount(shopData.stream()
            .map(ProfitReportDO::getAsin)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .count());
        
        // 聚合销售数据
        vo.setTotalSalesQuantity(sumInteger(shopData, ProfitReportDO::getTotalSalesQuantity));
        vo.setTotalSalesAmount(sumBigDecimal(shopData, ProfitReportDO::getTotalSalesAmount));
        vo.setFbaSaleAmount(sumBigDecimal(shopData, ProfitReportDO::getFbaSaleAmount));
        vo.setFbmSaleAmount(sumBigDecimal(shopData, ProfitReportDO::getFbmSaleAmount));
        // 计算退货退款 = 销售退款额 + 费用退款额
        BigDecimal salesRefunds = sumBigDecimal(shopData, ProfitReportDO::getTotalSalesRefunds);
        BigDecimal feeRefunds = sumBigDecimal(shopData, ProfitReportDO::getTotalFeeRefunds);
        vo.setTotalSalesRefunds(salesRefunds);  // 设置收入退款项
        vo.setTotalFeeRefunds(feeRefunds);      // 设置费用退款项
        vo.setTotalRefunds(safeAdd(salesRefunds, feeRefunds));
        
        // 成本相关
        vo.setCgPriceTotal(sumBigDecimal(shopData, ProfitReportDO::getCgPriceTotal));
        vo.setCgTransportCostsTotal(sumBigDecimal(shopData, ProfitReportDO::getCgTransportCostsTotal));
        vo.setCgOtherCostsTotal(sumBigDecimal(shopData, ProfitReportDO::getCgOtherCostsTotal));
        
        // 净销售额明细字段聚合
        BigDecimal shippingCredits = sumBigDecimal(shopData, ProfitReportDO::getShippingCredits);
        BigDecimal promotionalRebates = sumBigDecimal(shopData, ProfitReportDO::getPromotionalRebates);
        BigDecimal fbaInventoryCredit = sumBigDecimal(shopData, ProfitReportDO::getFbaInventoryCredit);
        BigDecimal cashOnDelivery = sumBigDecimal(shopData, ProfitReportDO::getCashOnDelivery);
        BigDecimal otherInAmount = sumBigDecimal(shopData, ProfitReportDO::getOtherInAmount);
        
        // 单独设置每个字段
        vo.setShippingCredits(shippingCredits);
        vo.setPromotionalRebates(promotionalRebates);
        vo.setFbaInventoryCredit(fbaInventoryCredit);
        vo.setCashOnDelivery(cashOnDelivery);
        vo.setOtherInAmount(otherInAmount);
        
        // 其他业务收入 = 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入
        vo.setOtherRevenue(safeAdd(shippingCredits, promotionalRebates, fbaInventoryCredit, cashOnDelivery, otherInAmount));
        
        // 费用相关
        vo.setTotalAdsCost(sumBigDecimal(shopData, ProfitReportDO::getTotalAdsCost));
        vo.setTotalAdsSales(sumBigDecimal(shopData, ProfitReportDO::getTotalAdsSales));
        vo.setPlatformFee(sumBigDecimal(shopData, ProfitReportDO::getPlatformFee));
        vo.setTotalFbaDeliveryFee(sumBigDecimal(shopData, ProfitReportDO::getTotalFbaDeliveryFee));
        vo.setTotalStorageFee(sumBigDecimal(shopData, ProfitReportDO::getTotalStorageFee));
        vo.setPromotionFee(sumBigDecimal(shopData, ProfitReportDO::getPromotionFee));
        
        // 站外费和其他费用需要特殊处理
        vo.setOffsiteFee(calculateOffsiteFee(shopData));
        vo.setOtherFee(calculateOtherFee(shopData));
        
        vo.setTotalCost(sumBigDecimal(shopData, ProfitReportDO::getTotalCost));
        vo.setGrossProfit(sumBigDecimal(shopData, ProfitReportDO::getGrossProfit));
        
        // 计算毛利率
        // 修正：根据公式，净销售额 = 总销售额 + 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入 + 收入退款额 + 费用退款额
        BigDecimal netSalesAmount = safeAdd(
            vo.getTotalSalesAmount(),  // 总销售额（已包含FBA+FBM）
            vo.getShippingCredits(),    // 买家运费
            vo.getPromotionalRebates(), // 促销折扣
            vo.getFbaInventoryCredit(),  // FBA库存赔偿
            vo.getCashOnDelivery(),      // COD
            vo.getOtherInAmount(),       // 其他收入
            vo.getTotalSalesRefunds(),   // 收入退款额
            vo.getTotalFeeRefunds()      // 费用退款额
        );
        
        // 计算毛利率 = 毛利润 / 净销售额
        if (netSalesAmount != null && netSalesAmount.compareTo(BigDecimal.ZERO) > 0) {
            vo.setGrossRate(vo.getGrossProfit()
                .divide(netSalesAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
        
        return vo;
    }
    
    private void calculateRates(ProfitReportSkuAggVO vo) {
        // 修正：根据公式，净销售额 = 总销售额 + 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入 + 收入退款额 + 费用退款额
        BigDecimal netSalesAmount = safeAdd(
            vo.getTotalSalesAmount(),  // 总销售额（已包含FBA+FBM）
            vo.getShippingCredits(),    // 买家运费
            vo.getPromotionalRebates(), // 促销折扣
            vo.getFbaInventoryCredit(),  // FBA库存赔偿
            vo.getCashOnDelivery(),      // COD
            vo.getOtherInAmount(),       // 其他收入
            vo.getTotalSalesRefunds(),   // 收入退款额
            vo.getTotalFeeRefunds()      // 费用退款额
        );
        
        // 计算毛利率 = 毛利润 / 净销售额
        if (netSalesAmount != null && netSalesAmount.compareTo(BigDecimal.ZERO) > 0) {
            vo.setGrossRate(vo.getGrossProfit()
                .divide(netSalesAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
        
        // 计算ACOS
        if (vo.getTotalAdsSales() != null && vo.getTotalAdsSales().compareTo(BigDecimal.ZERO) > 0) {
            vo.setAcos(vo.getTotalAdsCost()
                .divide(vo.getTotalAdsSales(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
    }
    
    private Integer sumInteger(List<ProfitReportDO> list, Function<ProfitReportDO, Integer> mapper) {
        return list.stream()
            .map(mapper)
            .filter(Objects::nonNull)
            .reduce(0, Integer::sum);
    }
    
    private BigDecimal sumBigDecimal(List<ProfitReportDO> list, Function<ProfitReportDO, BigDecimal> mapper) {
        return list.stream()
            .map(mapper)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private int compareByBigDecimal(BigDecimal a, BigDecimal b) {
        if (a == null && b == null) return 0;
        if (a == null) return -1;
        if (b == null) return 1;
        return a.compareTo(b);
    }
    
    private <T> PageResult<T> paginateInMemory(List<T> list, ProfitReportQueryVO queryVO) {
        int total = list.size();
        int pageNo = queryVO.getPageNo();
        int pageSize = queryVO.getPageSize();
        
        // 如果pageSize为PAGE_SIZE_NONE，返回所有数据
        if (pageSize == PageParam.PAGE_SIZE_NONE) {
            return new PageResult<>(list, (long) total);
        }
        
        // 正常分页逻辑
        int offset = (pageNo - 1) * pageSize;
        int limit = Math.min(offset + pageSize, total);
        
        if (offset >= total) {
            return new PageResult<>(Collections.emptyList(), 0L);
        }
        
        List<T> pageData = list.subList(offset, limit);
        return new PageResult<>(pageData, (long) total);
    }
//
//    @Override
//    public PageResult<ProfitReportAsinAggVO> aggregateByAsin(ProfitReportQueryVO queryVO) {
//        // 1. 查询原始数据
//        List<ProfitReportDO> rawData = queryRawData(queryVO);
//
//        // 2. 按ASIN分组聚合
//        Map<String, List<ProfitReportDO>> groupedData = rawData.stream()
//            .filter(item -> StrUtil.isNotBlank(item.getAsin()))
//            .collect(Collectors.groupingBy(ProfitReportDO::getAsin));
//
//        // 3. 计算聚合数据
//        List<ProfitReportAsinAggVO> aggregatedList = groupedData.entrySet().stream()
//            .map(entry -> buildAsinAggVO(entry.getKey(), entry.getValue()))
//            .sorted((a, b) -> compareByBigDecimal(b.getTotalSalesAmount(), a.getTotalSalesAmount()))
    
    @Override
    public Object aggregate(ProfitReportQueryVO queryVO) {
        String groupBy = queryVO.getGroupBy();
        if (StrUtil.isBlank(groupBy)) {
            groupBy = "SPU"; // 默认按SPU聚合
        }
        
        return switch (groupBy.toUpperCase()) {
            case "SPU" -> aggregateBySpu(queryVO);
            case "SPU_SID" -> aggregateBySpuAndSid(queryVO);
            case "SID" -> aggregateBySid(queryVO);
            default -> throw new IllegalArgumentException("不支持的聚合维度: " + groupBy + "，仅支持SPU、SPU_SID、SID");
        };
    }
    
    private ProfitReportAsinAggVO buildAsinAggVO(String asin, List<ProfitReportDO> asinData) {
        ProfitReportAsinAggVO vo = new ProfitReportAsinAggVO();
        vo.setAsin(asin);
        
        // 获取第一条数据的基本信息
        ProfitReportDO firstRecord = asinData.get(0);
        vo.setParentAsin(firstRecord.getParentAsin());
        vo.setProductName(firstRecord.getLocalName());
        vo.setSpu(firstRecord.getSpu());
        
        // 收集SKU列表和店铺数量
        vo.setSkuList(asinData.stream()
            .map(ProfitReportDO::getLocalSku)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList()));
        vo.setSkuCount(vo.getSkuList().size());
        
        // 收集店铺名称列表
        vo.setStoreNames(asinData.stream()
            .map(ProfitReportDO::getStoreName)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .collect(Collectors.toList()));
        
        vo.setStoreCount((int) asinData.stream()
            .map(ProfitReportDO::getSid)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .count());
        
        // 聚合销售数量
        vo.setTotalSalesQuantity(sumInteger(asinData, ProfitReportDO::getTotalSalesQuantity));
        vo.setFbaSalesQuantity(sumInteger(asinData, ProfitReportDO::getFbaSalesQuantity));
        vo.setFbmSalesQuantity(sumInteger(asinData, ProfitReportDO::getFbmSalesQuantity));
        vo.setRefundsQuantity(sumInteger(asinData, ProfitReportDO::getRefundsQuantity));
        
        // 聚合金额
        vo.setTotalSalesAmount(sumBigDecimal(asinData, ProfitReportDO::getTotalSalesAmount));
        vo.setFbaSaleAmount(sumBigDecimal(asinData, ProfitReportDO::getFbaSaleAmount));
        vo.setFbmSaleAmount(sumBigDecimal(asinData, ProfitReportDO::getFbmSaleAmount));
        // 计算退货退款 = 销售退款额 + 费用退款额
        BigDecimal salesRefunds = sumBigDecimal(asinData, ProfitReportDO::getTotalSalesRefunds);
        BigDecimal feeRefunds = sumBigDecimal(asinData, ProfitReportDO::getTotalFeeRefunds);
        vo.setTotalSalesRefunds(salesRefunds);  // 设置收入退款项
        vo.setTotalFeeRefunds(feeRefunds);      // 设置费用退款项
        vo.setTotalRefunds(safeAdd(salesRefunds, feeRefunds));
        
        // 成本相关
        vo.setCgPriceTotal(sumBigDecimal(asinData, ProfitReportDO::getCgPriceTotal));
        vo.setCgTransportCostsTotal(sumBigDecimal(asinData, ProfitReportDO::getCgTransportCostsTotal));
        vo.setCgOtherCostsTotal(sumBigDecimal(asinData, ProfitReportDO::getCgOtherCostsTotal));
        
        // 净销售额明细字段聚合
        BigDecimal shippingCredits = sumBigDecimal(asinData, ProfitReportDO::getShippingCredits);
        BigDecimal promotionalRebates = sumBigDecimal(asinData, ProfitReportDO::getPromotionalRebates);
        BigDecimal fbaInventoryCredit = sumBigDecimal(asinData, ProfitReportDO::getFbaInventoryCredit);
        BigDecimal cashOnDelivery = sumBigDecimal(asinData, ProfitReportDO::getCashOnDelivery);
        BigDecimal otherInAmount = sumBigDecimal(asinData, ProfitReportDO::getOtherInAmount);
        
        // 单独设置每个字段
        vo.setShippingCredits(shippingCredits);
        vo.setPromotionalRebates(promotionalRebates);
        vo.setFbaInventoryCredit(fbaInventoryCredit);
        vo.setCashOnDelivery(cashOnDelivery);
        vo.setOtherInAmount(otherInAmount);
        
        // 其他业务收入 = 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入
        vo.setOtherRevenue(safeAdd(shippingCredits, promotionalRebates, fbaInventoryCredit, cashOnDelivery, otherInAmount));
        
        // 费用相关
        vo.setTotalAdsCost(sumBigDecimal(asinData, ProfitReportDO::getTotalAdsCost));
        vo.setTotalAdsSales(sumBigDecimal(asinData, ProfitReportDO::getTotalAdsSales));
        vo.setPlatformFee(sumBigDecimal(asinData, ProfitReportDO::getPlatformFee));
        vo.setTotalFbaDeliveryFee(sumBigDecimal(asinData, ProfitReportDO::getTotalFbaDeliveryFee));
        vo.setTotalStorageFee(sumBigDecimal(asinData, ProfitReportDO::getTotalStorageFee));
        vo.setPromotionFee(sumBigDecimal(asinData, ProfitReportDO::getPromotionFee));
        
        // 站外费和其他费用需要特殊处理
        vo.setOffsiteFee(calculateOffsiteFee(asinData));
        vo.setOtherFee(calculateOtherFee(asinData));
        
        vo.setTotalCost(sumBigDecimal(asinData, ProfitReportDO::getTotalCost));
        vo.setGrossProfit(sumBigDecimal(asinData, ProfitReportDO::getGrossProfit));
        
        // 计算基本率指标
        // 修正：根据公式，净销售额 = 总销售额 + 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入 + 收入退款额 + 费用退款额
        BigDecimal netSalesAmount = safeAdd(
            vo.getTotalSalesAmount(),  // 总销售额（已包含FBA+FBM）
            vo.getShippingCredits(),    // 买家运费
            vo.getPromotionalRebates(), // 促销折扣
            vo.getFbaInventoryCredit(),  // FBA库存赔偿
            vo.getCashOnDelivery(),      // COD
            vo.getOtherInAmount(),       // 其他收入
            vo.getTotalSalesRefunds(),   // 收入退款额
            vo.getTotalFeeRefunds()      // 费用退款额
        );
        
        // 计算毛利率 = 毛利润 / 净销售额
        if (netSalesAmount != null && netSalesAmount.compareTo(BigDecimal.ZERO) > 0) {
            vo.setGrossRate(vo.getGrossProfit()
                .divide(netSalesAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
        
        if (vo.getTotalAdsSales() != null && vo.getTotalAdsSales().compareTo(BigDecimal.ZERO) > 0) {
            vo.setAcos(vo.getTotalAdsCost()
                .divide(vo.getTotalAdsSales(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
        
        return vo;
    }
    
    private ProfitReportDateAggVO buildDateAggVO(LocalDate date, List<ProfitReportDO> dateData) {
        ProfitReportDateAggVO vo = new ProfitReportDateAggVO();
        vo.setDate(date);
        
        // 统计数量
        vo.setAsinCount(dateData.stream()
            .map(ProfitReportDO::getAsin)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .count());
        
        vo.setSkuCount(dateData.stream()
            .map(ProfitReportDO::getLocalSku)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .count());
        
        vo.setStoreCount(dateData.stream()
            .map(ProfitReportDO::getSid)
            .filter(StrUtil::isNotBlank)
            .distinct()
            .count());
        
        // 聚合销售数量
        vo.setTotalSalesQuantity(sumInteger(dateData, ProfitReportDO::getTotalSalesQuantity));
        vo.setFbaSalesQuantity(sumInteger(dateData, ProfitReportDO::getFbaSalesQuantity));
        vo.setFbmSalesQuantity(sumInteger(dateData, ProfitReportDO::getFbmSalesQuantity));
        vo.setRefundsQuantity(sumInteger(dateData, ProfitReportDO::getRefundsQuantity));
        
        // 聚合金额
        vo.setTotalSalesAmount(sumBigDecimal(dateData, ProfitReportDO::getTotalSalesAmount));
        vo.setFbaSaleAmount(sumBigDecimal(dateData, ProfitReportDO::getFbaSaleAmount));
        vo.setFbmSaleAmount(sumBigDecimal(dateData, ProfitReportDO::getFbmSaleAmount));
        // 计算退货退款 = 销售退款额 + 费用退款额
        BigDecimal salesRefunds = sumBigDecimal(dateData, ProfitReportDO::getTotalSalesRefunds);
        BigDecimal feeRefunds = sumBigDecimal(dateData, ProfitReportDO::getTotalFeeRefunds);
        vo.setTotalSalesRefunds(salesRefunds);  // 设置收入退款项
        vo.setTotalFeeRefunds(feeRefunds);      // 设置费用退款项
        vo.setTotalRefunds(safeAdd(salesRefunds, feeRefunds));
        
        // 成本相关
        vo.setCgPriceTotal(sumBigDecimal(dateData, ProfitReportDO::getCgPriceTotal));
        vo.setCgTransportCostsTotal(sumBigDecimal(dateData, ProfitReportDO::getCgTransportCostsTotal));
        vo.setCgOtherCostsTotal(sumBigDecimal(dateData, ProfitReportDO::getCgOtherCostsTotal));
        
        // 净销售额明细字段聚合
        BigDecimal shippingCredits = sumBigDecimal(dateData, ProfitReportDO::getShippingCredits);
        BigDecimal promotionalRebates = sumBigDecimal(dateData, ProfitReportDO::getPromotionalRebates);
        BigDecimal fbaInventoryCredit = sumBigDecimal(dateData, ProfitReportDO::getFbaInventoryCredit);
        BigDecimal cashOnDelivery = sumBigDecimal(dateData, ProfitReportDO::getCashOnDelivery);
        BigDecimal otherInAmount = sumBigDecimal(dateData, ProfitReportDO::getOtherInAmount);
        
        // 单独设置每个字段
        vo.setShippingCredits(shippingCredits);
        vo.setPromotionalRebates(promotionalRebates);
        vo.setFbaInventoryCredit(fbaInventoryCredit);
        vo.setCashOnDelivery(cashOnDelivery);
        vo.setOtherInAmount(otherInAmount);
        
        // 其他业务收入 = 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入
        vo.setOtherRevenue(safeAdd(shippingCredits, promotionalRebates, fbaInventoryCredit, cashOnDelivery, otherInAmount));
        
        // 费用相关
        vo.setTotalAdsCost(sumBigDecimal(dateData, ProfitReportDO::getTotalAdsCost));
        vo.setTotalAdsSales(sumBigDecimal(dateData, ProfitReportDO::getTotalAdsSales));
        vo.setPlatformFee(sumBigDecimal(dateData, ProfitReportDO::getPlatformFee));
        vo.setTotalFbaDeliveryFee(sumBigDecimal(dateData, ProfitReportDO::getTotalFbaDeliveryFee));
        vo.setTotalStorageFee(sumBigDecimal(dateData, ProfitReportDO::getTotalStorageFee));
        vo.setPromotionFee(sumBigDecimal(dateData, ProfitReportDO::getPromotionFee));
        
        // 站外费和其他费用需要特殊处理
        vo.setOffsiteFee(calculateOffsiteFee(dateData));
        vo.setOtherFee(calculateOtherFee(dateData));
        
        vo.setTotalCost(sumBigDecimal(dateData, ProfitReportDO::getTotalCost));
        vo.setGrossProfit(sumBigDecimal(dateData, ProfitReportDO::getGrossProfit));
        
        // 计算基本率指标
        // 修正：根据公式，净销售额 = 总销售额 + 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入 + 收入退款额 + 费用退款额
        BigDecimal netSalesAmount = safeAdd(
            vo.getTotalSalesAmount(),  // 总销售额（已包含FBA+FBM）
            vo.getShippingCredits(),    // 买家运费
            vo.getPromotionalRebates(), // 促销折扣
            vo.getFbaInventoryCredit(),  // FBA库存赔偿
            vo.getCashOnDelivery(),      // COD
            vo.getOtherInAmount(),       // 其他收入
            vo.getTotalSalesRefunds(),   // 收入退款额
            vo.getTotalFeeRefunds()      // 费用退款额
        );
        
        // 计算毛利率 = 毛利润 / 净销售额
        if (netSalesAmount != null && netSalesAmount.compareTo(BigDecimal.ZERO) > 0) {
            vo.setGrossRate(vo.getGrossProfit()
                .divide(netSalesAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
        
        if (vo.getTotalAdsSales() != null && vo.getTotalAdsSales().compareTo(BigDecimal.ZERO) > 0) {
            vo.setAcos(vo.getTotalAdsCost()
                .divide(vo.getTotalAdsSales(), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100)));
        }
        
        return vo;
    }
    
    /**
     * 安全加法，处理null值
     */
    private BigDecimal safeAdd(BigDecimal... values) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                sum = sum.add(value);
            }
        }
        return sum;
    }
    
    /**
     * 计算站外费
     * 站外费用 = otherFeeStr中所有feeAllocation的总和
     */
    private BigDecimal calculateOffsiteFee(List<ProfitReportDO> dataList) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProfitReportDO data : dataList) {
            String otherFeeStr = data.getOtherFeeStr();
            if (StrUtil.isNotBlank(otherFeeStr)) {
                try {
                    // otherFeeStr是JSON数组格式
                    if (otherFeeStr.trim().startsWith("[")) {
                        com.alibaba.fastjson.JSONArray feeArray = com.alibaba.fastjson.JSONArray.parseArray(otherFeeStr);
                        for (int i = 0; i < feeArray.size(); i++) {
                            JSONObject feeObj = feeArray.getJSONObject(i);
                            if (feeObj != null && feeObj.containsKey("feeAllocation")) {
                                BigDecimal feeAllocation = feeObj.getBigDecimal("feeAllocation");
                                if (feeAllocation != null) {
                                    total = total.add(feeAllocation);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("解析站外费失败: {}", otherFeeStr);
                }
            }
        }
        return total;
    }
    
    /**
     * 计算其他费用
     * 其他费用 = 其他订单费用 + 入库配置费 + FBA国际物流货运费 + 调整费用 + 平台其他费
     * 注：入库配置费和FBA国际物流货运费可能存储在otherFeeStr的JSON数组中
     */
    private BigDecimal calculateOtherFee(List<ProfitReportDO> dataList) {
        BigDecimal otherTransactionFees = sumBigDecimal(dataList, ProfitReportDO::getOtherTransactionFees);
        BigDecimal adjustments = sumBigDecimal(dataList, ProfitReportDO::getAdjustments);
        BigDecimal totalPlatformOtherFee = sumBigDecimal(dataList, ProfitReportDO::getTotalPlatformOtherFee);
        
        // 从otherFeeStr中获取特定的其他费用项（如入库配置费、FBA国际物流货运费）
        BigDecimal otherFeesFromJson = BigDecimal.ZERO;
        for (ProfitReportDO data : dataList) {
            String otherFeeStr = data.getOtherFeeStr();
            if (StrUtil.isNotBlank(otherFeeStr)) {
                try {
                    // otherFeeStr可能是JSON数组格式
                    if (otherFeeStr.trim().startsWith("[")) {
                        com.alibaba.fastjson.JSONArray feeArray = com.alibaba.fastjson.JSONArray.parseArray(otherFeeStr);
                        for (int i = 0; i < feeArray.size(); i++) {
                            JSONObject feeObj = feeArray.getJSONObject(i);
                            if (feeObj != null) {
                                // 获取费用名称和金额
                                String feeName = feeObj.getString("feeName");
                                BigDecimal feeAmount = feeObj.getBigDecimal("feeAmount");
                                // 如果是入库配置费或FBA国际物流货运费等其他费用项，则累加
                                if (feeName != null && feeAmount != null && 
                                    (feeName.contains("入库") || feeName.contains("国际物流") || 
                                     feeName.contains("inbound") || feeName.contains("international"))) {
                                    otherFeesFromJson = otherFeesFromJson.add(feeAmount);
                                }
                            }
                        }
                    }
                    // 兼容对象格式（如果有的话）
                    else if (otherFeeStr.trim().startsWith("{")) {
                        JSONObject json = JSONObject.parseObject(otherFeeStr);
                        BigDecimal inboundFee = json.getBigDecimal("inboundFee");
                        BigDecimal internationalFee = json.getBigDecimal("internationalFee");
                        otherFeesFromJson = safeAdd(otherFeesFromJson, inboundFee, internationalFee);
                    }
                } catch (Exception e) {
                    log.debug("解析其他费用失败: {}", otherFeeStr);
                }
            }
        }
        
        return safeAdd(otherTransactionFees, adjustments, totalPlatformOtherFee, otherFeesFromJson);
    }
    
    @Override
    public List<ProfitReportManagementVO> convertToManagementReport(Object aggregateData, ProfitReportQueryVO queryVO, String groupBy) {
        List<ProfitReportManagementVO> result = new ArrayList<>();
        
        // 构建日期字符串 YY-MM-DD--YY-MM-DD
        String dateRange = "";
        if (queryVO != null && queryVO.getStartDate() != null && queryVO.getEndDate() != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yy-MM-dd");
            dateRange = queryVO.getStartDate().format(formatter) + "--" + queryVO.getEndDate().format(formatter);
        }
        
        // 获取项目值（根据聚合类型获取对应的值）
        String projectValue = "";
        
        // 从聚合数据中提取各项指标值
        BigDecimal salesAmount = BigDecimal.ZERO;
        BigDecimal totalRefunds = BigDecimal.ZERO;
        BigDecimal cgPriceTotal = BigDecimal.ZERO;
        BigDecimal cgTransportCostsTotal = BigDecimal.ZERO;
        BigDecimal otherRevenue = BigDecimal.ZERO;
        BigDecimal cgOtherCostsTotal = BigDecimal.ZERO;
        BigDecimal platformFee = BigDecimal.ZERO;
        BigDecimal totalFbaDeliveryFee = BigDecimal.ZERO;
        BigDecimal totalAdsCost = BigDecimal.ZERO;
        BigDecimal promotionFee = BigDecimal.ZERO;
        BigDecimal offsiteFee = BigDecimal.ZERO;
        BigDecimal totalStorageFee = BigDecimal.ZERO;
        BigDecimal otherFee = BigDecimal.ZERO;
        
        // 新增：直接累加毛利润和计算净销售额所需的字段
        BigDecimal grossProfit = BigDecimal.ZERO;
        BigDecimal shippingCredits = BigDecimal.ZERO;
        BigDecimal promotionalRebates = BigDecimal.ZERO;
        BigDecimal fbaInventoryCredit = BigDecimal.ZERO;
        BigDecimal cashOnDelivery = BigDecimal.ZERO;
        BigDecimal otherInAmount = BigDecimal.ZERO;
        BigDecimal totalSalesRefunds = BigDecimal.ZERO;
        BigDecimal totalFeeRefunds = BigDecimal.ZERO;
        
        // 根据不同类型的聚合数据提取值
        if (aggregateData instanceof PageResult) {
            PageResult<?> pageResult = (PageResult<?>) aggregateData;
            if (pageResult.getList() != null && !pageResult.getList().isEmpty()) {
                Object firstItem = pageResult.getList().get(0);
                
                // 如果有多条数据，需要汇总
                // 收集项目值（用于显示在“项目”列）
                List<String> projectValues = new ArrayList<>();
                
                for (Object item : pageResult.getList()) {
                    if (item instanceof ProfitReportAsinAggVO) {
                        ProfitReportAsinAggVO vo = (ProfitReportAsinAggVO) item;
                        // 收集ASIN
                        if (vo.getAsin() != null && !projectValues.contains(vo.getAsin())) {
                            projectValues.add(vo.getAsin());
                        }
                        salesAmount = safeAdd(salesAmount, vo.getTotalSalesAmount());
                        totalRefunds = safeAdd(totalRefunds, vo.getTotalRefunds());
                        cgPriceTotal = safeAdd(cgPriceTotal, vo.getCgPriceTotal());
                        cgTransportCostsTotal = safeAdd(cgTransportCostsTotal, vo.getCgTransportCostsTotal());
                        otherRevenue = safeAdd(otherRevenue, vo.getOtherRevenue());
                        cgOtherCostsTotal = safeAdd(cgOtherCostsTotal, vo.getCgOtherCostsTotal());
                        platformFee = safeAdd(platformFee, vo.getPlatformFee());
                        totalFbaDeliveryFee = safeAdd(totalFbaDeliveryFee, vo.getTotalFbaDeliveryFee());
                        totalAdsCost = safeAdd(totalAdsCost, vo.getTotalAdsCost());
                        promotionFee = safeAdd(promotionFee, vo.getPromotionFee());
                        offsiteFee = safeAdd(offsiteFee, vo.getOffsiteFee());
                        totalStorageFee = safeAdd(totalStorageFee, vo.getTotalStorageFee());
                        otherFee = safeAdd(otherFee, vo.getOtherFee());
                        // 累加毛利润和净销售额相关字段
                        grossProfit = safeAdd(grossProfit, vo.getGrossProfit());
                        shippingCredits = safeAdd(shippingCredits, vo.getShippingCredits());
                        promotionalRebates = safeAdd(promotionalRebates, vo.getPromotionalRebates());
                        fbaInventoryCredit = safeAdd(fbaInventoryCredit, vo.getFbaInventoryCredit());
                        cashOnDelivery = safeAdd(cashOnDelivery, vo.getCashOnDelivery());
                        otherInAmount = safeAdd(otherInAmount, vo.getOtherInAmount());
                        totalSalesRefunds = safeAdd(totalSalesRefunds, vo.getTotalSalesRefunds());
                        totalFeeRefunds = safeAdd(totalFeeRefunds, vo.getTotalFeeRefunds());
                    } else if (item instanceof ProfitReportSpuAggVO) {
                        ProfitReportSpuAggVO vo = (ProfitReportSpuAggVO) item;
                        // 收集SPU
                        if (vo.getSpu() != null && !projectValues.contains(vo.getSpu())) {
                            projectValues.add(vo.getSpu());
                        }
                        salesAmount = safeAdd(salesAmount, vo.getTotalSalesAmount());
                        totalRefunds = safeAdd(totalRefunds, vo.getTotalRefunds());
                        cgPriceTotal = safeAdd(cgPriceTotal, vo.getCgPriceTotal());
                        cgTransportCostsTotal = safeAdd(cgTransportCostsTotal, vo.getCgTransportCostsTotal());
                        otherRevenue = safeAdd(otherRevenue, vo.getOtherRevenue());
                        cgOtherCostsTotal = safeAdd(cgOtherCostsTotal, vo.getCgOtherCostsTotal());
                        platformFee = safeAdd(platformFee, vo.getPlatformFee());
                        totalFbaDeliveryFee = safeAdd(totalFbaDeliveryFee, vo.getTotalFbaDeliveryFee());
                        totalAdsCost = safeAdd(totalAdsCost, vo.getTotalAdsCost());
                        promotionFee = safeAdd(promotionFee, vo.getPromotionFee());
                        offsiteFee = safeAdd(offsiteFee, vo.getOffsiteFee());
                        totalStorageFee = safeAdd(totalStorageFee, vo.getTotalStorageFee());
                        otherFee = safeAdd(otherFee, vo.getOtherFee());
                        // 累加毛利润和净销售额相关字段
                        grossProfit = safeAdd(grossProfit, vo.getGrossProfit());
                        shippingCredits = safeAdd(shippingCredits, vo.getShippingCredits());
                        promotionalRebates = safeAdd(promotionalRebates, vo.getPromotionalRebates());
                        fbaInventoryCredit = safeAdd(fbaInventoryCredit, vo.getFbaInventoryCredit());
                        cashOnDelivery = safeAdd(cashOnDelivery, vo.getCashOnDelivery());
                        otherInAmount = safeAdd(otherInAmount, vo.getOtherInAmount());
                        totalSalesRefunds = safeAdd(totalSalesRefunds, vo.getTotalSalesRefunds());
                        totalFeeRefunds = safeAdd(totalFeeRefunds, vo.getTotalFeeRefunds());
                    } else if (item instanceof ProfitReportSkuAggVO) {
                        ProfitReportSkuAggVO vo = (ProfitReportSkuAggVO) item;
                        // 收集SKU
                        if (vo.getLocalSku() != null && !projectValues.contains(vo.getLocalSku())) {
                            projectValues.add(vo.getLocalSku());
                        }
                        salesAmount = safeAdd(salesAmount, vo.getTotalSalesAmount());
                        totalRefunds = safeAdd(totalRefunds, vo.getTotalRefunds());
                        cgPriceTotal = safeAdd(cgPriceTotal, vo.getCgPriceTotal());
                        cgTransportCostsTotal = safeAdd(cgTransportCostsTotal, vo.getCgTransportCostsTotal());
                        otherRevenue = safeAdd(otherRevenue, vo.getOtherRevenue());
                        cgOtherCostsTotal = safeAdd(cgOtherCostsTotal, vo.getCgOtherCostsTotal());
                        platformFee = safeAdd(platformFee, vo.getPlatformFee());
                        totalFbaDeliveryFee = safeAdd(totalFbaDeliveryFee, vo.getTotalFbaDeliveryFee());
                        totalAdsCost = safeAdd(totalAdsCost, vo.getTotalAdsCost());
                        promotionFee = safeAdd(promotionFee, vo.getPromotionFee());
                        offsiteFee = safeAdd(offsiteFee, vo.getOffsiteFee());
                        totalStorageFee = safeAdd(totalStorageFee, vo.getTotalStorageFee());
                        otherFee = safeAdd(otherFee, vo.getOtherFee());
                        // 累加毛利润和净销售额相关字段
                        grossProfit = safeAdd(grossProfit, vo.getGrossProfit());
                        shippingCredits = safeAdd(shippingCredits, vo.getShippingCredits());
                        promotionalRebates = safeAdd(promotionalRebates, vo.getPromotionalRebates());
                        fbaInventoryCredit = safeAdd(fbaInventoryCredit, vo.getFbaInventoryCredit());
                        cashOnDelivery = safeAdd(cashOnDelivery, vo.getCashOnDelivery());
                        otherInAmount = safeAdd(otherInAmount, vo.getOtherInAmount());
                        totalSalesRefunds = safeAdd(totalSalesRefunds, vo.getTotalSalesRefunds());
                        totalFeeRefunds = safeAdd(totalFeeRefunds, vo.getTotalFeeRefunds());
                    } else if (item instanceof ProfitReportShopAggVO) {
                        ProfitReportShopAggVO vo = (ProfitReportShopAggVO) item;
                        // 收集店铺
                        if (vo.getSid() != null && !projectValues.contains(vo.getSid())) {
                            projectValues.add(vo.getSid());
                        }
                        salesAmount = safeAdd(salesAmount, vo.getTotalSalesAmount());
                        totalRefunds = safeAdd(totalRefunds, vo.getTotalRefunds());
                        cgPriceTotal = safeAdd(cgPriceTotal, vo.getCgPriceTotal());
                        cgTransportCostsTotal = safeAdd(cgTransportCostsTotal, vo.getCgTransportCostsTotal());
                        otherRevenue = safeAdd(otherRevenue, vo.getOtherRevenue());
                        cgOtherCostsTotal = safeAdd(cgOtherCostsTotal, vo.getCgOtherCostsTotal());
                        platformFee = safeAdd(platformFee, vo.getPlatformFee());
                        totalFbaDeliveryFee = safeAdd(totalFbaDeliveryFee, vo.getTotalFbaDeliveryFee());
                        totalAdsCost = safeAdd(totalAdsCost, vo.getTotalAdsCost());
                        promotionFee = safeAdd(promotionFee, vo.getPromotionFee());
                        offsiteFee = safeAdd(offsiteFee, vo.getOffsiteFee());
                        totalStorageFee = safeAdd(totalStorageFee, vo.getTotalStorageFee());
                        otherFee = safeAdd(otherFee, vo.getOtherFee());
                        // 累加毛利润和净销售额相关字段
                        grossProfit = safeAdd(grossProfit, vo.getGrossProfit());
                        shippingCredits = safeAdd(shippingCredits, vo.getShippingCredits());
                        promotionalRebates = safeAdd(promotionalRebates, vo.getPromotionalRebates());
                        fbaInventoryCredit = safeAdd(fbaInventoryCredit, vo.getFbaInventoryCredit());
                        cashOnDelivery = safeAdd(cashOnDelivery, vo.getCashOnDelivery());
                        otherInAmount = safeAdd(otherInAmount, vo.getOtherInAmount());
                        totalSalesRefunds = safeAdd(totalSalesRefunds, vo.getTotalSalesRefunds());
                        totalFeeRefunds = safeAdd(totalFeeRefunds, vo.getTotalFeeRefunds());
                    } else if (item instanceof ProfitReportSpuSidAggVO) {
                        ProfitReportSpuSidAggVO vo = (ProfitReportSpuSidAggVO) item;
                        // 收集SPU
                        if (vo.getSpu() != null && !projectValues.contains(vo.getSpu())) {
                            projectValues.add(vo.getSpu());
                        }
                        salesAmount = safeAdd(salesAmount, vo.getTotalSalesAmount());
                        totalRefunds = safeAdd(totalRefunds, vo.getTotalRefunds());
                        cgPriceTotal = safeAdd(cgPriceTotal, vo.getCgPriceTotal());
                        cgTransportCostsTotal = safeAdd(cgTransportCostsTotal, vo.getCgTransportCostsTotal());
                        otherRevenue = safeAdd(otherRevenue, vo.getOtherRevenue());
                        cgOtherCostsTotal = safeAdd(cgOtherCostsTotal, vo.getCgOtherCostsTotal());
                        platformFee = safeAdd(platformFee, vo.getPlatformFee());
                        totalFbaDeliveryFee = safeAdd(totalFbaDeliveryFee, vo.getTotalFbaDeliveryFee());
                        totalAdsCost = safeAdd(totalAdsCost, vo.getTotalAdsCost());
                        promotionFee = safeAdd(promotionFee, vo.getPromotionFee());
                        offsiteFee = safeAdd(offsiteFee, vo.getOffsiteFee());
                        totalStorageFee = safeAdd(totalStorageFee, vo.getTotalStorageFee());
                        otherFee = safeAdd(otherFee, vo.getOtherFee());
                        // 累加毛利润和净销售额相关字段
                        grossProfit = safeAdd(grossProfit, vo.getGrossProfit());
                        shippingCredits = safeAdd(shippingCredits, vo.getShippingCredits());
                        promotionalRebates = safeAdd(promotionalRebates, vo.getPromotionalRebates());
                        fbaInventoryCredit = safeAdd(fbaInventoryCredit, vo.getFbaInventoryCredit());
                        cashOnDelivery = safeAdd(cashOnDelivery, vo.getCashOnDelivery());
                        otherInAmount = safeAdd(otherInAmount, vo.getOtherInAmount());
                        totalSalesRefunds = safeAdd(totalSalesRefunds, vo.getTotalSalesRefunds());
                        totalFeeRefunds = safeAdd(totalFeeRefunds, vo.getTotalFeeRefunds());
                    } else if (item instanceof ProfitReportDateAggVO) {
                        ProfitReportDateAggVO vo = (ProfitReportDateAggVO) item;
                        // 收集日期
                        if (vo.getDate() != null && !projectValues.contains(vo.getDate())) {
                            projectValues.add(String.valueOf(vo.getDate()));
                        }
                        salesAmount = safeAdd(salesAmount, vo.getTotalSalesAmount());
                        totalRefunds = safeAdd(totalRefunds, vo.getTotalRefunds());
                        cgPriceTotal = safeAdd(cgPriceTotal, vo.getCgPriceTotal());
                        cgTransportCostsTotal = safeAdd(cgTransportCostsTotal, vo.getCgTransportCostsTotal());
                        otherRevenue = safeAdd(otherRevenue, vo.getOtherRevenue());
                        cgOtherCostsTotal = safeAdd(cgOtherCostsTotal, vo.getCgOtherCostsTotal());
                        platformFee = safeAdd(platformFee, vo.getPlatformFee());
                        totalFbaDeliveryFee = safeAdd(totalFbaDeliveryFee, vo.getTotalFbaDeliveryFee());
                        totalAdsCost = safeAdd(totalAdsCost, vo.getTotalAdsCost());
                        promotionFee = safeAdd(promotionFee, vo.getPromotionFee());
                        offsiteFee = safeAdd(offsiteFee, vo.getOffsiteFee());
                        totalStorageFee = safeAdd(totalStorageFee, vo.getTotalStorageFee());
                        otherFee = safeAdd(otherFee, vo.getOtherFee());
                        // 累加毛利润和净销售额相关字段
                        grossProfit = safeAdd(grossProfit, vo.getGrossProfit());
                        shippingCredits = safeAdd(shippingCredits, vo.getShippingCredits());
                        promotionalRebates = safeAdd(promotionalRebates, vo.getPromotionalRebates());
                        fbaInventoryCredit = safeAdd(fbaInventoryCredit, vo.getFbaInventoryCredit());
                        cashOnDelivery = safeAdd(cashOnDelivery, vo.getCashOnDelivery());
                        otherInAmount = safeAdd(otherInAmount, vo.getOtherInAmount());
                        totalSalesRefunds = safeAdd(totalSalesRefunds, vo.getTotalSalesRefunds());
                        totalFeeRefunds = safeAdd(totalFeeRefunds, vo.getTotalFeeRefunds());
                    }
                }
                
                // 将项目值组合成字符串
                if (!projectValues.isEmpty()) {
                    projectValue = String.join(", ", projectValues);
                }
            }
        }
        
        // 毛利润已经从聚合数据中直接累加，无需重新计算
        // grossProfit 已在上面的循环中累加完成
        
        // 修正：根据公式，净销售额 = 总销售额 + 买家运费 + 促销折扣 + FBA库存赔偿 + COD + 其他收入 + 收入退款额 + 费用退款额
        BigDecimal netSalesAmount = safeAdd(
            salesAmount,           // 总销售额（已包含FBA+FBM）
            shippingCredits,       // 买家运费
            promotionalRebates,    // 促销折扣
            fbaInventoryCredit,    // FBA库存赔偿
            cashOnDelivery,        // COD
            otherInAmount,         // 其他收入
            totalSalesRefunds,     // 收入退款额
            totalFeeRefunds        // 费用退款额
        );
        
        // 计算毛利率 = (毛利 / 净销售额) * 100
        BigDecimal grossRate = BigDecimal.ZERO;
        if (netSalesAmount.compareTo(BigDecimal.ZERO) > 0) {
            grossRate = grossProfit.divide(netSalesAmount, 6, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
        }
        
        // 设置小数位数为2位
        salesAmount = setScale(salesAmount, 2);
        totalRefunds = setScale(totalRefunds, 2);
        cgPriceTotal = setScale(cgPriceTotal, 2);
        cgTransportCostsTotal = setScale(cgTransportCostsTotal, 2);
        otherRevenue = setScale(otherRevenue, 2);
        cgOtherCostsTotal = setScale(cgOtherCostsTotal, 2);
        platformFee = setScale(platformFee, 2);
        totalFbaDeliveryFee = setScale(totalFbaDeliveryFee, 2);
        totalAdsCost = setScale(totalAdsCost, 2);
        promotionFee = setScale(promotionFee, 2);
        offsiteFee = setScale(offsiteFee, 2);
        totalStorageFee = setScale(totalStorageFee, 2);
        otherFee = setScale(otherFee, 2);
        grossProfit = setScale(grossProfit, 2);
        grossRate = setScale(grossRate, 2); // 毛利率保留2位小数（已经是百分比）
        
        // 按照管理利润表格式构建数据行
        // Row 0: 日期行
        result.add(ProfitReportManagementVO.createTextRow("日期", dateRange));
        
        // Row 1: 项目行
        result.add(ProfitReportManagementVO.createTextRow("项目", projectValue));
        
        // Row 3: 一、收入：
        result.add(ProfitReportManagementVO.createSection("一、收入："));
        // Row 4: 销售收入
        result.add(ProfitReportManagementVO.createDataRow("销售收入", salesAmount));
        // Row 5: 退货退款
        result.add(ProfitReportManagementVO.createDataRow("退货退款", totalRefunds));
        // Row 6: 减：销售成本
        result.add(ProfitReportManagementVO.createDataRow("减：销售成本", cgPriceTotal));
        // Row 7: 减：头程运费
        result.add(ProfitReportManagementVO.createDataRow("减：头程运费", cgTransportCostsTotal));
        // Row 8: 减：营业税金及附加（无对应字段）
        result.add(ProfitReportManagementVO.createDataRow("减：营业税金及附加", null));
        // Row 9: 其他业务收入
        result.add(ProfitReportManagementVO.createDataRow("其他业务收入", otherRevenue));
        // Row 10: 减：其他业务成本
        result.add(ProfitReportManagementVO.createDataRow("减：其他业务成本", cgOtherCostsTotal));
        
        // Row 11: 二、毛利：
        result.add(ProfitReportManagementVO.createSection("二、毛利："));
        // Row 12: 毛利
        result.add(ProfitReportManagementVO.createDataRow("毛利", grossProfit));
        // Row 13: 毛利率（显示为百分比）
        String grossRateStr = null;
        if (grossRate != null) {
            // 毛利率已经在计算时乘以100了，直接加%即可
            grossRateStr = grossRate + "%";
        }
        result.add(ProfitReportManagementVO.createTextRow("毛利率", grossRateStr));
        
        // Row 14: 减：平台费用：
        result.add(ProfitReportManagementVO.createSection("减：平台费用："));
        // Row 15: 平台佣金
        result.add(ProfitReportManagementVO.createDataRow("平台佣金", platformFee));
        // Row 16: 配送费
        result.add(ProfitReportManagementVO.createIndentedDataRow("配送费", totalFbaDeliveryFee));
        // Row 17: 广告费
        result.add(ProfitReportManagementVO.createIndentedDataRow("广告费", totalAdsCost));
        // Row 18: 推广费
        result.add(ProfitReportManagementVO.createIndentedDataRow("推广费", promotionFee));
        // Row 19: 站外费
        result.add(ProfitReportManagementVO.createIndentedDataRow("站外费", offsiteFee));
        // Row 20: 仓储费
        result.add(ProfitReportManagementVO.createIndentedDataRow("仓储费", totalStorageFee));
        // Row 21: 其他费用
        result.add(ProfitReportManagementVO.createIndentedDataRow("其他费用", otherFee));
        
        // Row 22: 平台费用小计
        BigDecimal platformFeeTotal = safeAdd(platformFee, totalFbaDeliveryFee, totalAdsCost, 
            promotionFee, offsiteFee, totalStorageFee, otherFee);
        platformFeeTotal = setScale(platformFeeTotal, 2);
        result.add(ProfitReportManagementVO.createDataRow("平台费用小计", platformFeeTotal));
        
        // Row 23: 减：其他变动费用
        result.add(ProfitReportManagementVO.createSection("减：其他变动费用"));
        // Row 24: 存货计提准备（留空）
        result.add(ProfitReportManagementVO.createIndentedDataRow("存货计提准备", null));
        // Row 25: 奖金金额（留空）
        result.add(ProfitReportManagementVO.createIndentedDataRow("奖金金额", null));
        // Row 26: 其他销售费用（留空）
        result.add(ProfitReportManagementVO.createIndentedDataRow("其他销售费用", null));
        // Row 27: 其他变动费用小计（留空）
        result.add(ProfitReportManagementVO.createDataRow("其他变动费用小计", null));
        
        // Row 28: 三、边际利润：
        result.add(ProfitReportManagementVO.createSection("三、边际利润："));
        // Row 29: 边际利润（留空）
        result.add(ProfitReportManagementVO.createDataRow("边际利润", null));
        // Row 30: 边际利润率（留空）
        result.add(ProfitReportManagementVO.createDataRow("边际利润率", null));
        
        // Row 31: 减：固定费用
        result.add(ProfitReportManagementVO.createSection("减：固定费用"));
        // Row 32-37: 固定费用项（留空）
        result.add(ProfitReportManagementVO.createIndentedDataRow("固定薪酬", null));
        result.add(ProfitReportManagementVO.createIndentedDataRow("租金及物业管理费", null));
        result.add(ProfitReportManagementVO.createIndentedDataRow("日常办公费用", null));
        result.add(ProfitReportManagementVO.createIndentedDataRow("职工福利费", null));
        result.add(ProfitReportManagementVO.createIndentedDataRow("职工培训费", null));
        result.add(ProfitReportManagementVO.createIndentedDataRow("其他费用", null));
        // Row 38: 固定费用小计（留空）
        result.add(ProfitReportManagementVO.createDataRow("固定费用小计", null));
        
        // Row 39: 四、营业利润：
        result.add(ProfitReportManagementVO.createSection("四、营业利润："));
        // Row 40: 营业利润（留空）
        result.add(ProfitReportManagementVO.createDataRow("营业利润", null));
        // Row 41: 营业利润率（留空）
        result.add(ProfitReportManagementVO.createDataRow("营业利润率", null));
        
        // Row 42: 加：营业外收支（留空）
        result.add(ProfitReportManagementVO.createDataRow("加：营业外收支", null));
        
        // Row 43: 五、利润总额
        result.add(ProfitReportManagementVO.createSection("五、利润总额"));
        // Row 44: 利润总额（留空）
        result.add(ProfitReportManagementVO.createDataRow("利润总额", null));
        // Row 45: 利润率（留空）
        result.add(ProfitReportManagementVO.createDataRow("利润率", null));
        
        return result;
    }
    
    private BigDecimal setScale(BigDecimal value, int scale) {
        if (value == null) {
            return null;
        }
        return value.setScale(scale, RoundingMode.HALF_UP);
    }
    
    private BigDecimal safeSubtract(BigDecimal a, BigDecimal b) {
        if (a == null) a = BigDecimal.ZERO;
        if (b == null) b = BigDecimal.ZERO;
        return a.subtract(b);
    }
    
    @Override
    public Map<String, byte[]> exportSpuSheetsByShop(ProfitReportQueryVO queryVO) throws Exception {
        // 设置不分页，获取所有数据
        queryVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        
        // 1. 获取SPU+SID维度的聚合数据
        PageResult<ProfitReportSpuSidAggVO> spuSidResult = aggregateBySpuAndSid(queryVO);
        List<ProfitReportSpuSidAggVO> allData = spuSidResult.getList();
        
        if (CollUtil.isEmpty(allData)) {
            throw new IllegalArgumentException("没有找到符合条件的数据");
        }
        
        // 2. 按店铺（sid）分组数据
        Map<String, List<ProfitReportSpuSidAggVO>> shopDataMap = allData.stream()
            .filter(item -> StrUtil.isNotBlank(item.getSid()))
            .collect(Collectors.groupingBy(ProfitReportSpuSidAggVO::getSid));
        
        log.info("exportSpuSheetsByShop - 总数据条数: {}, 店铺数量: {}, 店铺SID列表: {}", 
            allData.size(), shopDataMap.size(), shopDataMap.keySet());
        
        // 3. 为每个店铺生成一个Excel文件
        Map<String, byte[]> resultMap = new LinkedHashMap<>();
        
        for (Map.Entry<String, List<ProfitReportSpuSidAggVO>> shopEntry : shopDataMap.entrySet()) {
            String sid = shopEntry.getKey();
            List<ProfitReportSpuSidAggVO> shopData = shopEntry.getValue();
            
            // 获取店铺名称
            String storeName = shopData.stream()
                .map(ProfitReportSpuSidAggVO::getStoreName)
                .filter(StrUtil::isNotBlank)
                .findFirst()
                .orElse("未知店铺");
            
            // 按SPU分组
            Map<String, List<ProfitReportSpuSidAggVO>> spuDataMap = shopData.stream()
                .filter(item -> StrUtil.isNotBlank(item.getSpu()))
                .collect(Collectors.groupingBy(
                    ProfitReportSpuSidAggVO::getSpu,
                    TreeMap::new,  // 使用TreeMap自动按字母顺序排序
                    Collectors.toList()
                ));
            
            // 如果有SPU为空的数据，放到"其他"sheet中（放在最后）
            List<ProfitReportSpuSidAggVO> noSpuData = shopData.stream()
                .filter(item -> StrUtil.isBlank(item.getSpu()))
                .collect(Collectors.toList());
            
            // 将排序后的SPU数据放入LinkedHashMap以保持顺序
            Map<String, List<ProfitReportSpuSidAggVO>> orderedSpuDataMap = new LinkedHashMap<>(spuDataMap);
            
            // 将"其他"放在最后
            if (CollUtil.isNotEmpty(noSpuData)) {
                orderedSpuDataMap.put("其他", noSpuData);
            }
            
            log.info("店铺{}的SPU已按A-Z顺序排序，共{}个SPU", sid, orderedSpuDataMap.size());
            
            // 创建多Sheet的Excel，每个SPU一个sheet，使用管理利润表格式
            Map<String, List<ProfitReportManagementVO>> sheetDataMap = new LinkedHashMap<>();
            for (Map.Entry<String, List<ProfitReportSpuSidAggVO>> spuEntry : orderedSpuDataMap.entrySet()) {
                String spu = spuEntry.getKey();
                List<ProfitReportSpuSidAggVO> spuList = spuEntry.getValue();
                
                // 处理sheet名称（Excel限制31个字符）
                String sheetName = spu;
                if (sheetName.length() > 31) {
                    sheetName = sheetName.substring(0, 28) + "...";
                }
                
                // 将SPU聚合数据转换为管理利润表格式
                // 将List包装成PageResult以适配convertToManagementReport方法
                PageResult<ProfitReportSpuSidAggVO> pageResult = new PageResult<>();
                pageResult.setList(spuList);
                pageResult.setTotal((long) spuList.size());
                
                List<ProfitReportManagementVO> managementReport = convertToManagementReport(
                    pageResult,  // 包装成PageResult
                    queryVO,  // 查询参数（包含日期范围）
                    "SPU_SID" // 聚合维度
                );
                
                sheetDataMap.put(sheetName, managementReport);
            }
            
            // 使用ExcelMultiSheetUtils生成Excel字节数组（管理利润表格式）
            byte[] excelBytes = null;
            try {
                excelBytes = cn.iocoder.yudao.framework.excel.core.util.ExcelMultiSheetUtils
                    .writeMultiSheet(sheetDataMap, ProfitReportManagementVO.class);
                
                if (excelBytes == null || excelBytes.length == 0) {
                    log.error("生成Excel失败 - 店铺SID: {}, 返回空字节数组", sid);
                    continue;
                }
            } catch (Exception e) {
                log.error("生成Excel异常 - 店铺SID: {}, 错误: {}", sid, e.getMessage(), e);
                throw new RuntimeException("生成Excel文件失败: " + e.getMessage(), e);
            }
            
            // 生成文件名：店铺名称_SID.xlsx
            String fileName = cn.iocoder.yudao.framework.excel.core.util.ZipExportUtils
                .sanitizeFileName(storeName + "_" + sid) + ".xlsx";
            
            log.info("生成Excel文件 - 店铺SID: {}, 店铺名称: {}, 文件名: {}, 文件大小: {} bytes, SPU数量: {}", 
                sid, storeName, fileName, excelBytes.length, spuDataMap.size());
            
            resultMap.put(fileName, excelBytes);
        }
        
        log.info("exportSpuSheetsByShop完成 - 生成文件总数: {}", resultMap.size());
        
        return resultMap;
    }
    
    @Override
    public Map<String, byte[]> exportPureSpuSheets(ProfitReportQueryVO queryVO) throws Exception {
        // 设置不分页，获取所有数据
        queryVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        
        // 1. 获取纯SPU维度的聚合数据（不考虑店铺）
        PageResult<ProfitReportSpuAggVO> spuResult = aggregateBySpu(queryVO);
        List<ProfitReportSpuAggVO> allData = spuResult.getList();
        
        if (CollUtil.isEmpty(allData)) {
            throw new IllegalArgumentException("没有找到符合条件的数据");
        }
        
        log.info("exportPureSpuSheets - 总SPU数量: {}", allData.size());
        
        // 对SPU数据按名称进行A-Z排序
        allData.sort((a, b) -> {
            String spuA = a.getSpu() != null ? a.getSpu() : "";
            String spuB = b.getSpu() != null ? b.getSpu() : "";
            return spuA.compareToIgnoreCase(spuB);
        });
        log.info("SPU已按A-Z顺序排序");
        
        // 2. 创建多Sheet的Excel，每个SPU一个sheet
        Map<String, List<ProfitReportManagementVO>> sheetDataMap = new LinkedHashMap<>();
        
        for (ProfitReportSpuAggVO spuData : allData) {
            String spu = spuData.getSpu();
            
            // 处理sheet名称（Excel限制31个字符）
            String sheetName = spu;
            if (sheetName.length() > 31) {
                sheetName = sheetName.substring(0, 28) + "...";
            }
            
            // 将SPU聚合数据转换为管理利润表格式
            PageResult<ProfitReportSpuAggVO> pageResult = new PageResult<>();
            pageResult.setList(Collections.singletonList(spuData));
            pageResult.setTotal(1L);
            
            List<ProfitReportManagementVO> managementVOList = convertToManagementReport(pageResult, queryVO, "SPU");
            
            sheetDataMap.put(sheetName, managementVOList);
        }
        
        // 使用ExcelMultiSheetUtils生成包含多个sheet的Excel字节数组
        byte[] excelBytes = null;
        try {
            excelBytes = cn.iocoder.yudao.framework.excel.core.util.ExcelMultiSheetUtils
                .writeMultiSheet(sheetDataMap, ProfitReportManagementVO.class);
            
            if (excelBytes == null || excelBytes.length == 0) {
                log.error("生成Excel失败 - 返回空字节数组");
                throw new RuntimeException("生成Excel文件失败：返回空数据");
            }
        } catch (Exception e) {
            log.error("生成Excel异常 - 错误: {}", e.getMessage(), e);
            throw new RuntimeException("生成Excel文件失败: " + e.getMessage(), e);
        }
        
        // 生成文件名
        String fileName = "利润报表_SPU汇总.xlsx";
        
        Map<String, byte[]> resultMap = new LinkedHashMap<>();
        resultMap.put(fileName, excelBytes);
        
        log.info("exportPureSpuSheets完成 - 生成单个Excel文件，包含 {} 个sheet", sheetDataMap.size());
        
        return resultMap;
    }
    
    @Override
    public Map<String, byte[]> exportPureSpuSheetsWithMerge(ProfitReportQueryVO queryVO, byte[] historyFileBytes) throws Exception {
        // 设置不分页，获取所有数据
        queryVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        
        log.info("exportPureSpuSheetsWithMerge - 开始合并导出，历史文件大小: {} bytes, 查询参数: startDate={}, endDate={}, sids={}", 
            historyFileBytes != null ? historyFileBytes.length : 0,
            queryVO.getStartDate(), queryVO.getEndDate(), queryVO.getSids());
        
        // 1. 获取纯SPU维度的聚合数据（不考虑店铺）
        PageResult<ProfitReportSpuAggVO> spuResult = aggregateBySpu(queryVO);
        List<ProfitReportSpuAggVO> allData = spuResult.getList();
        
        if (CollUtil.isEmpty(allData)) {
            log.error("exportPureSpuSheetsWithMerge - 未查询到任何数据");
            throw new IllegalArgumentException("没有找到符合条件的数据");
        }
        
        log.info("exportPureSpuSheetsWithMerge - 查询到的SPU数量: {}", allData.size());
        log.info("exportPureSpuSheetsWithMerge - SPU列表: {}", allData.stream()
            .map(ProfitReportSpuAggVO::getSpu)
            .collect(Collectors.toList()));
        
        // 对SPU数据按名称进行A-Z排序
        allData.sort((a, b) -> {
            String spuA = a.getSpu() != null ? a.getSpu() : "";
            String spuB = b.getSpu() != null ? b.getSpu() : "";
            return spuA.compareToIgnoreCase(spuB);
        });
        log.info("SPU已按A-Z顺序排序");
        
        // 2. 准备新数据Map，每个SPU一个sheet
        Map<String, List<ProfitReportManagementVO>> newDataMap = new LinkedHashMap<>();
        
        // 生成日期范围标识，用于新列的标题
        String dateRangeStr = "";
        if (queryVO.getStartDate() != null && queryVO.getEndDate() != null) {
            dateRangeStr = queryVO.getStartDate() + "至" + queryVO.getEndDate();
        } else if (queryVO.getStartDate() != null) {
            dateRangeStr = queryVO.getStartDate().toString();
        }
        
        for (ProfitReportSpuAggVO spuData : allData) {
            String spu = spuData.getSpu();
            
            // 处理sheet名称（Excel限制31个字符）
            String sheetName = spu;
            if (sheetName.length() > 31) {
                sheetName = sheetName.substring(0, 28) + "...";
            }
            
            // 将SPU聚合数据转换为管理利润表格式
            PageResult<ProfitReportSpuAggVO> pageResult = new PageResult<>();
            pageResult.setList(Collections.singletonList(spuData));
            pageResult.setTotal(1L);
            
            List<ProfitReportManagementVO> managementVOList = convertToManagementReport(pageResult, queryVO, "SPU");
            
            // 如果有日期范围，修改第一行的金额列为日期范围
            if (!managementVOList.isEmpty() && !dateRangeStr.isEmpty()) {
                // 在列表开头添加日期标识
                ProfitReportManagementVO dateHeader = new ProfitReportManagementVO();
                dateHeader.setItemName("日期范围");
                dateHeader.setAmountStr(dateRangeStr);
                managementVOList.add(0, dateHeader);
            }
            
            newDataMap.put(sheetName, managementVOList);
        }
        
        // 3. 使用ExcelMergeUtils合并数据
        byte[] mergedExcelBytes = null;
        try {
            mergedExcelBytes = cn.iocoder.yudao.framework.excel.core.util.ExcelMergeUtils
                .mergeExcelSheets(historyFileBytes, newDataMap, ProfitReportManagementVO.class);
            
            if (mergedExcelBytes == null || mergedExcelBytes.length == 0) {
                log.error("合并Excel失败 - 返回空字节数组");
                throw new RuntimeException("合并Excel文件失败：返回空数据");
            }
        } catch (Exception e) {
            log.error("合并Excel异常 - 错误: {}", e.getMessage(), e);
            throw new RuntimeException("合并Excel文件失败: " + e.getMessage(), e);
        }
        
        // 生成文件名
        String fileName = "利润报表_SPU汇总_合并.xlsx";
        
        Map<String, byte[]> resultMap = new LinkedHashMap<>();
        resultMap.put(fileName, mergedExcelBytes);
        
        log.info("exportPureSpuSheetsWithMerge完成 - 生成合并后的Excel文件，包含 {} 个sheet", newDataMap.size());
        
        return resultMap;
    }

}