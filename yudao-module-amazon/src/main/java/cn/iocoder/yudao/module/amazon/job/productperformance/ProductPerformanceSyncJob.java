package cn.iocoder.yudao.module.amazon.job.productperformance;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.amazon.controller.admin.productperformance.vo.ProductPerformanceSyncReqVO;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import cn.iocoder.yudao.module.amazon.service.productperformance.ProductPerformanceService;
import cn.iocoder.yudao.module.amazon.service.shops.ShopsService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 产品表现数据同步 Job
 * 
 * 参数格式：ASIN1,ASIN2|SID1,SID2|开始日期|结束日期
 * 示例：B085M7NH7K,B085M7NH7L|109,110|2024-08-01|2024-08-07
 *
 * @author 芋道源码
 */
@Slf4j
@Component
public class ProductPerformanceSyncJob implements JobHandler {

    @Resource
    private ProductPerformanceService productPerformanceService;

    @Resource
    private LingXingApiService lingXingApiService;

    @Resource
    private ShopsService shopsService;

    @Override
    public String execute(String param) throws Exception {
        log.info("开始执行产品表现同步任务，参数: {}", param);
        
        // 使用租户ID为1执行同步任务
        return TenantUtils.execute(1L, () -> {
            try {
                // 解析参数
                ProductPerformanceSyncReqVO syncReqVO = parseParam(param);
                
                // 如果没有指定店铺，则获取所有syncFlag为1的店铺
                if (CollUtil.isEmpty(syncReqVO.getSids())) {
                    List<Integer> shopSids = shopsService.getSyncEnabledShopSids();
                    if (CollUtil.isEmpty(shopSids)) {
                        return "未找到需要同步的店铺（syncFlag=1）";
                    }
                    // 将Integer列表转换为Long列表
                    List<Long> sids = new ArrayList<>();
                    for (Integer sid : shopSids) {
                        sids.add(sid.longValue());
                    }
                    syncReqVO.setSids(sids);
                }
                syncReqVO.setStartDate(DateUtil.formatDate(DateUtil.yesterday()));
                syncReqVO.setEndDate(DateUtil.formatDate(DateUtil.yesterday()));
                
                // 执行同步
                return productPerformanceService.syncProductPerformance(syncReqVO);
            } catch (Exception e) {
                log.error("产品表现同步任务执行失败", e);
                throw new RuntimeException("同步产品表现数据失败: " + e.getMessage(), e);
            }
        });
    }

    /**
     * 解析任务参数
     */
    private ProductPerformanceSyncReqVO parseParam(String param) {
        ProductPerformanceSyncReqVO reqVO = new ProductPerformanceSyncReqVO();
        
        if (StrUtil.isBlank(param)) {
            // 默认参数：同步昨天的数据
            LocalDate yesterday = LocalDate.now().minusDays(1);
            String dateStr = yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE);
            reqVO.setStartDate(dateStr);
            reqVO.setEndDate(dateStr);
            return reqVO;
        }
        
        String[] parts = param.split("\\|");
        
        // 解析ASIN列表
        if (parts.length > 0 && !parts[0].isEmpty()) {
            reqVO.setAsins(Arrays.asList(parts[0].split(",")));
        }
        
        // 解析店铺ID列表
        if (parts.length > 1 && !parts[1].isEmpty()) {
            List<Long> sids = new ArrayList<>();
            for (String sid : parts[1].split(",")) {
                sids.add(Long.parseLong(sid.trim()));
            }
            reqVO.setSids(sids);
        }
        
        // 解析日期
        if (parts.length > 2) {
            reqVO.setStartDate(parts[2]);
        } else {
            // 默认昨天
            LocalDate yesterday = LocalDate.now().minusDays(1);
            reqVO.setStartDate(yesterday.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        
        if (parts.length > 3) {
            reqVO.setEndDate(parts[3]);
        } else {
            // 默认与开始日期相同
            reqVO.setEndDate(reqVO.getStartDate());
        }
        
        // 设置默认值
        reqVO.setSummaryField("asin");
        reqVO.setIsRecentlyEnum(true);
        
        return reqVO;
    }
}