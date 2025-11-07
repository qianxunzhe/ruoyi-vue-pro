package cn.iocoder.yudao.module.amazon.controller.admin.test;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.module.amazon.controller.admin.test.vo.AmazonListingVO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonListingDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.AmazonReviewQueryDTO;
import cn.iocoder.yudao.module.amazon.dal.dataobject.lingxing.LingXingApiResponseDTO;
import cn.iocoder.yudao.module.amazon.enums.FeishuSceneEnum;
import cn.iocoder.yudao.module.amazon.service.feishu.FeishuNotificationService;
import cn.iocoder.yudao.module.amazon.service.lingXingAPI.LingXingApiService;
import cn.iocoder.yudao.module.amazon.util.CloudflareR2Utils;
import cn.iocoder.yudao.module.amazon.utils.LingXingTokenManager;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
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
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
@Tag(name = "管理后台 - R2文件上传测试")
@RestController
@RequestMapping("/amazon/test")
public class TestController {

    @Resource
    private CloudflareR2Utils r2Utils;

    @Resource
    private FeishuNotificationService feishuNotificationService;
    
    @PermitAll
    @GetMapping("/upload-test-excel")
    @Operation(summary = "测试Excel文件上传", description = "生成模拟Excel文件并上传到R2，返回带过期时间的公网URL")
    public CommonResult<Map<String, Object>> testExcelUpload() {
        try {
            // 生成模拟Excel数据
            ExcelWriter writer = ExcelUtil.getWriter(true);
            
            // 添加表头
            writer.addHeaderAlias("id", "ID");
            writer.addHeaderAlias("name", "商品名称");
            writer.addHeaderAlias("sku", "SKU");
            writer.addHeaderAlias("price", "价格");
            writer.addHeaderAlias("stock", "库存");
            writer.addHeaderAlias("createTime", "创建时间");
            
            // 生成模拟数据
            List<Map<String, Object>> rows = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", i);
                row.put("name", "测试商品" + i);
                row.put("sku", "SKU-" + IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase());
                row.put("price", Math.round(Math.random() * 1000 * 100) / 100.0);
                row.put("stock", (int)(Math.random() * 1000));
                row.put("createTime", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                rows.add(row);
            }
            
            // 写入数据
            writer.write(rows, true);
            
            // 转换为字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            writer.flush(out);
            byte[] excelBytes = out.toByteArray();
            writer.close();
            
            // 生成文件名
            String fileName = "test_data_" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
            
            // 上传到R2并生成12小时有效的预签名URL
            int durationHours = 12;
            String url = r2Utils.uploadExcelWithPresignedUrl(excelBytes, fileName, durationHours);
            
            // 计算过期时间（12小时后）
            LocalDateTime expireTime = LocalDateTime.now().plusHours(durationHours);
            long expireTimestamp = expireTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("fileName", fileName + ".xlsx");
            result.put("fileSize", excelBytes.length);
            result.put("expireTime", DateUtil.format(Date.from(expireTime.toInstant(ZoneOffset.of("+8"))), "yyyy-MM-dd HH:mm:ss"));
            result.put("expireTimestamp", expireTimestamp);
            result.put("remainingHours", 12);
            result.put("message", "Excel文件上传成功");
            result.put("urlType", "custom_domain");  // 如果使用自定义域名
            result.put("note", "使用自定义域名访问，需在Cloudflare R2中配置访问策略");
            
            log.info("Excel文件上传成功: {}", url);
            String startDate = DateUtil.format(new Date(), "yyyy-MM-dd");
            String endDate = DateUtil.format(new Date(), "yyyy-MM-dd");
            int syncCount = 100;
            // 6. 发送飞书成功通知
            String successMessage = String.format(
                    "✅ 测试数据，请忽略：利润报表同步成功\n\n" +
                            "📅 日期范围：%s 至 %s\n" +
                            "📈 同步记录数：%d\n" +
                            "📁 文件数量：%d 个店铺\n" +
                            "📎 下载地址：%s\n" +
                            "⏰ 完成时间：%s",

                    startDate, endDate, syncCount, 1,
                    url, DateUtil.now()
            );

            feishuNotificationService.sendTextMessage(successMessage, FeishuSceneEnum.PROFIT_REPORT);




            return success(result);
            
        } catch (Exception e) {
            log.error("Excel文件上传失败", e);
            return success(null);
        }
    }

    @PermitAll
    @GetMapping("/upload-test-zip")
    @Operation(summary = "测试ZIP文件上传", description = "生成多个模拟文件打包成ZIP并上传到R2，返回带过期时间的公网URL")
    public CommonResult<Map<String, Object>> testZipUpload() {
        try {
            Map<String, byte[]> fileMap = new HashMap<>();
            
            // 生成Excel文件1 - 销售数据
            ExcelWriter writer1 = ExcelUtil.getWriter(true);
            List<Map<String, Object>> salesData = new ArrayList<>();
            for (int i = 1; i <= 50; i++) {
                Map<String, Object> row = new HashMap<>();
                row.put("日期", DateUtil.format(DateUtil.offsetDay(new Date(), -i), "yyyy-MM-dd"));
                row.put("销售额", Math.round(Math.random() * 10000 * 100) / 100.0);
                row.put("订单数", (int)(Math.random() * 100));
                row.put("客单价", Math.round(Math.random() * 500 * 100) / 100.0);
                salesData.add(row);
            }
            writer1.write(salesData, true);
            ByteArrayOutputStream out1 = new ByteArrayOutputStream();
            writer1.flush(out1);
            fileMap.put("sales_data.xlsx", out1.toByteArray());
            writer1.close();
            
            // 生成Excel文件2 - 库存数据
            ExcelWriter writer2 = ExcelUtil.getWriter(true);
            List<Map<String, Object>> inventoryData = new ArrayList<>();
            for (int i = 1; i <= 30; i++) {
                Map<String, Object> row = new HashMap<>();
                row.put("SKU", "SKU-" + IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase());
                row.put("仓库", "仓库" + (i % 3 + 1));
                row.put("库存量", (int)(Math.random() * 1000));
                row.put("安全库存", (int)(Math.random() * 100));
                row.put("更新时间", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                inventoryData.add(row);
            }
            writer2.write(inventoryData, true);
            ByteArrayOutputStream out2 = new ByteArrayOutputStream();
            writer2.flush(out2);
            fileMap.put("inventory_data.xlsx", out2.toByteArray());
            writer2.close();
            
            // 生成CSV文件 - 产品列表
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("产品ID,产品名称,分类,价格,状态\n");
            for (int i = 1; i <= 20; i++) {
                csvContent.append(i).append(",")
                         .append("产品").append(i).append(",")
                         .append("分类").append(i % 5 + 1).append(",")
                         .append(Math.round(Math.random() * 1000 * 100) / 100.0).append(",")
                         .append(i % 2 == 0 ? "在售" : "下架").append("\n");
            }
            fileMap.put("product_list.csv", csvContent.toString().getBytes("UTF-8"));
            
            // 生成JSON文件 - 配置信息
            Map<String, Object> config = new HashMap<>();
            config.put("version", "1.0.0");
            config.put("timestamp", System.currentTimeMillis());
            config.put("environment", "test");
            Map<String, Object> settings = new HashMap<>();
            settings.put("autoSync", true);
            settings.put("syncInterval", 3600);
            settings.put("maxRetries", 3);
            config.put("settings", settings);
            String jsonContent = JsonUtils.toJsonString(config);
            fileMap.put("config.json", jsonContent.getBytes("UTF-8"));
            
            // 生成TXT文件 - 说明文档
            String readme = "测试数据包说明\n" +
                           "================\n" +
                           "生成时间: " + DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + "\n" +
                           "包含文件:\n" +
                           "1. sales_data.xlsx - 销售数据\n" +
                           "2. inventory_data.xlsx - 库存数据\n" +
                           "3. product_list.csv - 产品列表\n" +
                           "4. config.json - 配置信息\n" +
                           "5. README.txt - 本说明文件\n\n" +
                           "注意：此为测试数据，仅供演示使用。";
            fileMap.put("README.txt", readme.getBytes("UTF-8"));
            
            // 生成ZIP文件名
            String zipName = "test_package_" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
            
            // 上传ZIP文件并生成12小时有效的预签名URL
            int durationHours = 12;
            String url = r2Utils.uploadZipWithPresignedUrl(fileMap, zipName, durationHours);
            
            // 计算过期时间（12小时后）
            LocalDateTime expireTime = LocalDateTime.now().plusHours(durationHours);
            long expireTimestamp = expireTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
            
            // 计算总文件大小
            long totalSize = fileMap.values().stream().mapToLong(bytes -> bytes.length).sum();
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("url", url);
            result.put("fileName", zipName + ".zip");
            result.put("fileCount", fileMap.size());
            result.put("fileList", fileMap.keySet());
            result.put("totalSize", totalSize);
            result.put("expireTime", DateUtil.format(Date.from(expireTime.toInstant(ZoneOffset.of("+8"))), "yyyy-MM-dd HH:mm:ss"));
            result.put("expireTimestamp", expireTimestamp);
            result.put("remainingHours", 12);
            result.put("message", "ZIP文件上传成功，包含" + fileMap.size() + "个文件");
            result.put("urlType", "custom_domain");  // 如果使用自定义域名
            result.put("note", "使用自定义域名访问，需在Cloudflare R2中配置访问策略");
            
            log.info("ZIP文件上传成功: {}", url);
            return success(result);
            
        } catch (Exception e) {
            log.error("ZIP文件上传失败", e);
            return success(null);
        }
    }
}