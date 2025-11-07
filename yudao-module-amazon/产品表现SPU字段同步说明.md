# 产品表现SPU字段同步实现说明

## 功能概述
在同步产品表现数据时，自动通过SKU获取对应的SPU信息并保存到数据库。

## 实现方案

### 1. 数据库修改
- 在 `amazon_product_performance` 表中添加了 `spu` 字段
- 字段类型：`varchar(100)`
- 字段位置：在 `sku` 字段之后

**SQL脚本**：`src/main/resources/sql/add_spu_field.sql`
```sql
ALTER TABLE `amazon_product_performance` 
ADD COLUMN `spu` varchar(100) DEFAULT NULL COMMENT 'SPU（产品编码）' AFTER `sku`;
```

### 2. 实体类修改
在 `ProductPerformanceDO.java` 中添加了SPU字段：
```java
/**
 * SPU（产品编码）
 */
private String spu;
```

### 3. 同步逻辑修改
在 `ProductPerformanceServiceImpl.java` 的 `convertDtoToDo` 方法中：

#### 3.1 批量获取SKU-SPU映射
```java
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
```

#### 3.2 设置SPU信息
在设置SKU后立即设置对应的SPU：
```java
// 设置SPU信息
if (StrUtil.isNotBlank(priceInfo.getLocalSku()) && !finalSkuSpuMap.isEmpty()) {
    Map<String, String> spuInfo = finalSkuSpuMap.get(priceInfo.getLocalSku());
    if (spuInfo != null && spuInfo.containsKey("spu")) {
        performanceDO.setSpu(spuInfo.get("spu"));
        log.debug("设置SPU信息: SKU={}, SPU={}", priceInfo.getLocalSku(), spuInfo.get("spu"));
    }
}
```

## 性能优化
1. **批量查询**：先收集所有SKU，再一次性批量查询对应的SPU，避免在循环中逐个查询
2. **缓存机制**：利用 `LingXingApiService` 内部的缓存机制，减少API调用次数
3. **容错处理**：SPU查询失败不影响主流程，确保产品表现数据正常同步

## 使用步骤

### 1. 执行数据库脚本
```bash
mysql -u username -p database_name < src/main/resources/sql/add_spu_field.sql
```

### 2. 确保SKU-SPU缓存已初始化

#### 方式一：自动初始化（推荐）
系统启动后会延迟10秒自动初始化缓存。

#### 方式二：手动初始化
调用API接口手动初始化：
```bash
POST /amazon/product-performance/init-sku-spu-cache
```

#### 方式三：代码中调用
```java
@Resource
private LingXingApiService lingXingApiService;

// 刷新SKU-SPU映射缓存
lingXingApiService.refreshSkuSpuCache();
```

### 3. 同步产品表现数据
调用同步接口时，系统会自动：
1. 获取产品表现数据
2. 提取所有SKU
3. 批量查询SKU对应的SPU
4. 将SPU信息保存到数据库

## 测试验证

### 测试步骤
1. 确保数据库已添加SPU字段
2. 确保SKU-SPU映射缓存已初始化
3. 调用产品表现同步接口
4. 检查数据库中的SPU字段是否正确填充

### 验证SQL
```sql
-- 查看包含SPU的产品表现数据
SELECT asin, sku, spu, item_name, seller_name 
FROM amazon_product_performance 
WHERE spu IS NOT NULL 
LIMIT 10;

-- 统计SPU填充情况
SELECT 
    COUNT(*) as total_count,
    COUNT(spu) as spu_count,
    ROUND(COUNT(spu) * 100.0 / COUNT(*), 2) as spu_fill_rate
FROM amazon_product_performance
WHERE sku IS NOT NULL;
```

## 注意事项
1. **数据完整性**：如果SKU在领星系统中没有对应的SPU，该字段将保持为空
2. **性能影响**：批量查询SPU会增加少量同步时间，但通过缓存可以有效降低影响
3. **错误处理**：SPU查询失败不会导致同步失败，仅记录警告日志
4. **缓存初始化**：系统启动后会延迟10秒自动初始化SKU-SPU缓存，也可以手动调用接口初始化

## 常见问题解决

### Q: 为什么产品表现没有获取到SPU？
**A:** 可能是SKU-SPU缓存未初始化，解决方案：
1. 等待系统启动后10秒自动初始化
2. 调用 `/amazon/product-performance/init-sku-spu-cache` 接口手动初始化
3. 确认领星API的产品列表接口是否正常返回数据

### Q: 如何验证缓存是否初始化成功？
**A:** 查看日志中是否有以下信息：
- "开始异步初始化SKU-SPU缓存"
- "SKU-SPU映射缓存刷新完成，缓存数量: xxx"

## 相关文档
- [SKU-SPU映射功能使用文档](./docs/SKU-SPU映射功能使用文档.md)
- [产品表现API接口文档](./产品表现API接口文档.md)