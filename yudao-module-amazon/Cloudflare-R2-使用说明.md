# Cloudflare R2 文件上传工具使用说明

## 1. 功能介绍

`CloudflareR2Utils` 是一个独立的工具类，用于上传文件到 Cloudflare R2 存储服务并返回访问 URL。

### 主要特性
- ✅ 支持单文件上传（Excel、文本、图片等）
- ✅ 支持多文件打包 ZIP 上传
- ✅ 自动生成唯一文件名，防止覆盖
- ✅ 支持自定义目录结构
- ✅ 文件存在性检查
- ✅ 文件删除功能
- ✅ 完全独立，不影响现有业务逻辑

## 2. 配置步骤

### 2.1 获取 Cloudflare R2 凭证

1. 登录 [Cloudflare Dashboard](https://dash.cloudflare.com/)
2. 进入 R2 对象存储
3. 创建存储桶（Bucket）
4. 创建 API 令牌：
   - 点击"管理 R2 API 令牌"
   - 创建新的 API 令牌
   - 权限选择：对象读写（Object Read & Write）
   - 保存 Access Key ID 和 Secret Access Key

### 2.2 配置应用程序

编辑 `application-r2.yml` 文件：

```yaml
cloudflare:
  r2:
    enabled: true  # 启用 R2 功能
    endpoint: https://<account-id>.r2.cloudflarestorage.com
    access-key: your-access-key-id
    secret-key: your-secret-access-key
    bucket: your-bucket-name
    domain:   # 可选：自定义域名
```

在主配置文件 `application.yml` 中引入 R2 配置：

```yaml
spring:
  profiles:
    include: r2  # 引入 R2 配置
```

### 2.3 配置自定义域名（可选）

如果需要使用自定义域名访问文件：

1. 在 Cloudflare R2 中配置自定义域名
2. 在配置文件中设置 `domain` 参数
3. 返回的 URL 将使用自定义域名

## 3. 使用示例

### 3.1 注入工具类

```java
@RestController
@RequestMapping("/api/export")
public class ExportController {
    
    @Resource
    private CloudflareR2Utils r2Utils;
    
    // ... 其他代码
}
```

### 3.2 上传 Excel 文件

```java
@GetMapping("/excel")
public CommonResult<String> exportExcel() {
    // 生成 Excel 数据（使用 EasyExcel 或其他工具）
    byte[] excelData = generateExcelData();
    
    // 上传到 R2
    String fileName = "report_" + System.currentTimeMillis();
    String url = r2Utils.uploadExcel(excelData, fileName);
    
    // 返回下载链接
    return success(url);
}
```

### 3.3 上传多个文件为 ZIP

```java
@GetMapping("/batch")
public CommonResult<String> exportBatch() {
    Map<String, byte[]> fileMap = new HashMap<>();
    
    // 添加多个文件
    fileMap.put("report1.xlsx", generateReport1());
    fileMap.put("report2.xlsx", generateReport2());
    fileMap.put("summary.txt", generateSummary());
    
    // 上传 ZIP
    String zipName = "batch_export_" + DateUtil.format(new Date(), "yyyyMMddHHmmss");
    String url = r2Utils.uploadZip(fileMap, zipName);
    
    return success(url);
}
```

### 3.4 上传自定义文件

```java
@PostMapping("/upload")
public CommonResult<String> uploadFile(@RequestParam("file") MultipartFile file) {
    // 获取文件内容
    byte[] content = file.getBytes();
    String fileName = file.getOriginalFilename();
    
    // 上传文件
    String url = r2Utils.uploadFile(content, fileName, "uploads");
    
    return success(url);
}
```

### 3.5 检查和删除文件

```java
// 检查文件是否存在
boolean exists = r2Utils.existsFile(fileUrl);

// 删除文件
boolean deleted = r2Utils.deleteFile(fileUrl);
```

## 4. 集成到现有业务

### 示例：改造利润报表导出

```java
@RestController
@RequestMapping("/profit-report")
public class ProfitReportController {
    
    @Resource
    private ProfitReportService reportService;
    
    @Resource
    private CloudflareR2Utils r2Utils;
    
    @GetMapping("/export")
    public CommonResult<Map<String, String>> exportReport(@Valid QueryVO query) {
        // 检查 R2 是否启用
        if (!r2Utils.isEnabled()) {
            // 降级到原有导出方式
            return exportLocal(query);
        }
        
        // 生成报表数据
        List<ProfitReportVO> data = reportService.queryData(query);
        byte[] excelBytes = ExcelUtils.write(data);
        
        // 上传到 R2
        String fileName = "profit_report_" + DateUtil.format(new Date(), "yyyyMMdd");
        String url = r2Utils.uploadExcel(excelBytes, fileName);
        
        // 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        result.put("fileName", fileName + ".xlsx");
        result.put("message", "导出成功，文件已上传到云端");
        
        return success(result);
    }
}
```

## 5. 测试

运行测试类验证功能：

```bash
# 运行单元测试
mvn test -Dtest=CloudflareR2UtilsTest

# 运行特定测试方法
mvn test -Dtest=CloudflareR2UtilsTest#testUploadExcelFile
```

## 6. 注意事项

### 6.1 安全性
- **不要**将 Access Key 和 Secret Key 提交到代码仓库
- 建议使用环境变量或配置中心管理敏感信息
- 设置合适的 bucket 访问权限

### 6.2 性能优化
- 大文件建议分片上传（需要扩展工具类）
- 考虑使用异步上传避免阻塞请求
- 配置合理的连接池参数

### 6.3 错误处理
- 工具类已包含基本的错误处理和日志记录
- 建议在业务层添加重试机制
- 提供降级方案（R2 不可用时的备选方案）

### 6.4 成本控制
- R2 存储费用：$0.015/GB/月
- 无出口流量费用
- Class A 操作（上传）：$4.50/百万次请求
- Class B 操作（下载）：$0.36/百万次请求

## 7. 扩展功能

如需更多功能，可以扩展工具类：

- 分片上传大文件
- 生成临时签名 URL
- 批量删除文件
- 文件列表查询
- 文件元数据管理
- 上传进度回调

## 8. 故障排除

### 问题：上传失败，提示认证错误
- 检查 Access Key 和 Secret Key 是否正确
- 确认 API 令牌权限是否足够

### 问题：无法访问上传的文件
- 检查 bucket 权限设置
- 验证自定义域名配置是否正确

### 问题：上传速度慢
- 考虑文件大小和网络状况
- 可以尝试就近的 R2 区域

## 9. 相关链接

- [Cloudflare R2 文档](https://developers.cloudflare.com/r2/)
- [S3 兼容 API 参考](https://developers.cloudflare.com/r2/api/s3/api/)
- [R2 定价](https://www.cloudflare.com/zh-cn/developer-platform/r2/pricing/)