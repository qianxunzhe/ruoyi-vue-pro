package cn.iocoder.yudao.module.amazon.job.profitreport;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo.ProfitReportQueryVO;
import cn.iocoder.yudao.module.amazon.controller.admin.profitreport.vo.ProfitReportSyncReqVO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.shops.ShopsDO;
import cn.iocoder.yudao.module.amazon.enums.FeishuSceneEnum;
import cn.iocoder.yudao.module.amazon.service.feishu.FeishuNotificationService;
import cn.iocoder.yudao.module.amazon.service.profitreport.ProfitReportService;
import cn.iocoder.yudao.module.amazon.service.shops.ShopsService;
import cn.iocoder.yudao.module.amazon.util.CloudflareR2Utils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 每周利润报表同步定时任务
 * 
 * 功能：
 * 1. 每周一凌晨自动同步上周（周一至周日）的利润报表数据
 * 2. 自动获取所有活跃店铺进行同步
 * 3. 按SPU维度导出数据（按店铺分文件，每个SPU一个Sheet）
 * 4. 将多个Excel文件打包成ZIP并上传到Cloudflare R2
 * 5. 通过飞书机器人发送下载链接到群组
 * 
 * CRON表达式：0 0 2 ? * MON（每周一凌晨2点）
 *
 * @author 芋道源码
 */
@Slf4j
@Component
public class ProfitReportWeeklySyncJob implements JobHandler {

    @Resource
    private ProfitReportService profitReportService;

    @Resource
    private CloudflareR2Utils cloudflareR2Utils;

    @Resource
    private FeishuNotificationService feishuNotificationService;

    @Resource
    private ShopsService shopsService;

    @Override
    public String execute(String param) throws Exception {
        log.info("开始执行每周利润报表同步任务");
        
        // 使用租户ID 1 执行整个任务
        return TenantUtils.execute(1L, () -> {
            LocalDate startDate = null;
            LocalDate endDate = null;
            List<Long> sids = new ArrayList<>();
            
            try {
                // 1. 计算上周的日期范围（周一至周日）
                LocalDate now = LocalDate.now();
                startDate = now.minusWeeks(1).with(DayOfWeek.MONDAY);
                endDate = startDate.plusDays(6);
                
                // 2. 获取所有活跃店铺
                List<ShopsDO> activeShops = shopsService.getAllShopsActive();
                if (CollUtil.isEmpty(activeShops)) {
                    String errorMsg = "未找到任何活跃店铺";
                    log.error(errorMsg);
                    feishuNotificationService.notifyTaskFailed("每周利润报表同步", errorMsg, null);
                    return errorMsg;
                }
                
                // 将店铺sid从Integer转换为Long
                for (ShopsDO shop : activeShops) {
                    if (shop.getSid() != null) {
                        sids.add(shop.getSid().longValue());
                    }
                }
                
                if (CollUtil.isEmpty(sids)) {
                    String errorMsg = "活跃店铺中没有有效的sid";
                    log.error(errorMsg);
//                    feishuNotificationService.notifyTaskFailed("每周利润报表同步", errorMsg, null);
                    return errorMsg;
                }
                
                log.info("准备同步利润报表 - 日期范围: {} 至 {}, 店铺数量: {}", 
                    startDate, endDate, sids.size());
                
                // 3. 同步利润报表数据
                ProfitReportSyncReqVO syncReqVO = new ProfitReportSyncReqVO();
                syncReqVO.setStartDate(startDate);
                syncReqVO.setEndDate(endDate);
                syncReqVO.setSids(sids);
                
                log.info("开始同步利润报表数据...");
                Integer syncCount = profitReportService.syncProfitReport(syncReqVO);
                log.info("同步利润报表数据完成，同步记录数: {}", syncCount);
                
                // 如果没有同步到数据，直接返回
                if (syncCount == null || syncCount == 0) {
                    String msg = String.format("未同步到任何数据（%s 至 %s）", startDate, endDate);
                    log.warn(msg);
//                    feishuNotificationService.sendTextMessage(
//                        String.format("⚠️ 利润报表同步提醒\n日期范围：%s 至 %s\n结果：%s",
//                            startDate, endDate, msg),
//                        FeishuSceneEnum.PROFIT_REPORT
//                    );
                    return msg;
                }
            
//            // 3. 准备导出参数
//            ProfitReportQueryVO queryVO = new ProfitReportQueryVO();
//            queryVO.setStartDate(LocalDate.parse(startDate.toString()));
//            queryVO.setEndDate(LocalDate.parse(endDate.toString()));
//            queryVO.setSids(sids);
//            queryVO.setGroupBy("SPU");
//
//            log.info("开始导出SPU维度数据（按店铺分文件）...");
//
//            // 4. 导出SPU维度的Excel（按店铺分文件）
//            Map<String, byte[]> excelMap = profitReportService.exportSpuSheetsByShop(queryVO);
//
//            if (excelMap.isEmpty()) {
//                String msg = "导出数据为空，没有可导出的数据";
//                log.warn(msg);
//                feishuNotificationService.notifyTaskFailed("每周利润报表同步", msg, null);
//                return msg;
//            }
//
//            log.info("成功生成{}个Excel文件，准备上传到R2...", excelMap.size());
//
//            // 5. 上传到Cloudflare R2
//            String zipName = String.format("利润报表_%s至%s_%s.zip",
//                startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
//                endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
//                DateUtil.format(new Date(), "HHmmss"));
//
//            String fileUrl;
//
//            // 检查R2是否已启用
//            if (!cloudflareR2Utils.isEnabled()) {
//                log.warn("Cloudflare R2未启用，跳过文件上传");
//                // R2未启用时，只发送同步成功通知
//                String message = String.format(
//                    "📊 利润报表同步完成\n" +
//                    "📅 日期范围：%s 至 %s\n" +
//                    "📈 同步记录数：%d\n" +
//                    "📁 生成文件数：%d 个店铺\n" +
//                    "⚠️ 提示：R2未配置，文件未上传\n" +
//                    "⏰ 完成时间：%s",
//                    startDate, endDate, syncCount, excelMap.size(), DateUtil.now()
//                );
//
//                feishuNotificationService.sendTextMessage(message, FeishuSceneEnum.PROFIT_REPORT);
//                return String.format("同步成功，记录数: %d，文件数: %d（R2未启用，未上传）",
//                    syncCount, excelMap.size());
//            }
//
//            try {
//                fileUrl = cloudflareR2Utils.uploadZip(excelMap, zipName);
//                log.info("文件上传成功，URL: {}", fileUrl);
//            } catch (Exception e) {
//                log.error("文件上传失败", e);
//                // 上传失败但同步成功，发送部分成功通知
//                String message = String.format(
//                    "⚠️ 利润报表同步部分完成\n" +
//                    "📅 日期范围：%s 至 %s\n" +
//                    "📈 同步记录数：%d\n" +
//                    "📁 生成文件数：%d 个店铺\n" +
//                    "❌ 文件上传失败：%s\n" +
//                    "⏰ 时间：%s",
//                    startDate, endDate, syncCount, excelMap.size(),
//                    e.getMessage(), DateUtil.now()
//                );
//
//                feishuNotificationService.sendTextMessage(message, FeishuSceneEnum.PROFIT_REPORT);
//                throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
//            }
//
//            // 6. 发送飞书成功通知
//            String successMessage = String.format(
//                "✅ 利润报表同步成功\n\n" +
//                "📅 日期范围：%s 至 %s\n" +
//                "📈 同步记录数：%d\n" +
//                "📁 文件数量：%d 个店铺\n" +
//                "📎 下载地址：%s\n" +
//                "⏰ 完成时间：%s",
//                startDate, endDate, syncCount, excelMap.size(),
//                fileUrl, DateUtil.now()
//            );
//
//            feishuNotificationService.sendTextMessage(successMessage, FeishuSceneEnum.PROFIT_REPORT);
//
//            // 返回执行结果
//                return String.format("执行成功 - 同步记录数: %d, 文件数: %d, 下载地址: %s",
//                    syncCount, excelMap.size(), fileUrl);
                return "success";
            } catch (Exception e) {
                log.error("每周利润报表同步任务执行失败", e);
                
                // 发送失败通知
//                String errorMessage = String.format(
//                    "❌ 利润报表同步失败\n\n" +
//                    "📅 尝试日期：%s 至 %s\n" +
//                    "❌ 错误信息：%s\n" +
//                    "📝 错误详情：\n%s\n" +
//                    "⏰ 失败时间：%s",
//                    startDate != null ? startDate : "未知",
//                    endDate != null ? endDate : "未知",
//                    e.getMessage(),
//                    ExceptionUtils.getStackTrace(e).substring(0, Math.min(500, ExceptionUtils.getStackTrace(e).length())),
//                    DateUtil.now()
//                );
//
//                try {
//                    feishuNotificationService.notifyTaskFailed(
//                        "每周利润报表同步",
//                        e.getMessage(),
//                        ExceptionUtils.getStackTrace(e)
//                    );
//                } catch (Exception notifyError) {
//                    log.error("发送飞书通知失败", notifyError);
//                }
                
                throw new RuntimeException(e);
            }
        });
    }
}