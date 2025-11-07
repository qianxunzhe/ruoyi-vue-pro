# SKU-SPU映射功能使用文档

## 功能概述

本功能通过调用领星API的产品列表接口，获取并缓存SKU与SPU的对应关系，方便系统其他模块通过SKU快速获取对应的SPU信息。

## API接口说明

### 领星产品列表接口
- **接口路径**: `/erp/sc/routing/data/local_inventory/productList`
- **请求方式**: POST
- **接口说明**: 查询本地产品列表，对应系统【产品】>【产品管理】数据

## 功能实现

### 1. 相关类文件

#### DTO类
- `ProductListQueryDTO.java` - 产品列表查询参数
- `ProductListDTO.java` - 产品数据传输对象（包含SKU、SPU、产品名称）
- `ProductListResponseDTO.java` - 产品列表响应封装

#### 服务接口和实现
- `LingXingApiService.java` - 领星API服务接口
- `LingXingApiServiceImpl.java` - 领星API服务实现类

### 2. 核心功能方法

```java
// 查询产品列表
ProductListResponseDTO getProductList(ProductListQueryDTO queryParams);

// 获取所有产品列表（自动分页）
List<ProductListDTO> getAllProductList();

// 通过SKU查询SPU信息
Map<String, String> getSpuBySku(String sku);

// 批量查询SKU对应的SPU信息
Map<String, Map<String, String>> getSpuBySkuBatch(List<String> skuList);

// 刷新SKU-SPU映射缓存
void refreshSkuSpuCache();
```

## 使用示例

### 1. 注入服务

```java
@Resource
private LingXingApiService lingXingApiService;
```

### 2. 初始化缓存

建议在系统启动时或定时任务中刷新缓存：

```java
// 刷新所有SKU-SPU映射缓存
lingXingApiService.refreshSkuSpuCache();
```

### 3. 查询单个SKU的SPU信息

```java
// 查询单个SKU
String sku = "TEST-SKU-001";
Map<String, String> spuInfo = lingXingApiService.getSpuBySku(sku);

if (spuInfo != null) {
    String spu = spuInfo.get("spu");
    String productName = spuInfo.get("productName");
    
    System.out.println("SKU: " + sku);
    System.out.println("SPU: " + spu);
    System.out.println("产品名称: " + productName);
}
```

### 4. 批量查询多个SKU的SPU信息

```java
// 批量查询
List<String> skuList = Arrays.asList("SKU001", "SKU002", "SKU003");
Map<String, Map<String, String>> spuMap = lingXingApiService.getSpuBySkuBatch(skuList);

for (Map.Entry<String, Map<String, String>> entry : spuMap.entrySet()) {
    String sku = entry.getKey();
    Map<String, String> spuInfo = entry.getValue();
    
    System.out.println("SKU: " + sku);
    System.out.println("  SPU: " + spuInfo.get("spu"));
    System.out.println("  产品名称: " + spuInfo.get("productName"));
}
```

### 5. 带条件查询产品列表

```java
// 创建查询参数
ProductListQueryDTO queryParams = new ProductListQueryDTO();
queryParams.setOffset(0);
queryParams.setLength(100);
queryParams.setSkuList(Arrays.asList("SKU001", "SKU002"));

// 查询产品列表
ProductListResponseDTO response = lingXingApiService.getProductList(queryParams);

if (response != null && response.getCode() == 0) {
    List<ProductListDTO> products = response.getData();
    for (ProductListDTO product : products) {
        System.out.println("SKU: " + product.getSku());
        System.out.println("SPU: " + product.getSpu());
        System.out.println("产品名称: " + product.getProductName());
    }
}
```

## 缓存机制说明

### 缓存策略
1. **内存缓存**: 使用HashMap存储SKU-SPU映射关系
2. **缓存更新**: 
   - 首次查询时自动更新缓存
   - 可手动调用`refreshSkuSpuCache()`刷新全量缓存
3. **线程安全**: 使用ReentrantLock保证并发访问安全

### 缓存流程
1. 查询时优先从内存缓存获取
2. 缓存未命中时调用领星API获取
3. 获取到数据后自动更新缓存

## 性能优化建议

### 1. 定期刷新缓存
建议通过定时任务定期刷新缓存，保证数据的时效性：

```java
@Component
public class SkuSpuCacheRefreshJob {
    
    @Resource
    private LingXingApiService lingXingApiService;
    
    // 每天凌晨2点刷新缓存
    @Scheduled(cron = "0 0 2 * * ?")
    public void refreshCache() {
        lingXingApiService.refreshSkuSpuCache();
    }
}
```

### 2. 批量查询优化
当需要查询多个SKU时，使用批量查询方法可以减少API调用次数：

```java
// 推荐：批量查询
Map<String, Map<String, String>> result = lingXingApiService.getSpuBySkuBatch(skuList);

// 不推荐：循环单个查询
for (String sku : skuList) {
    Map<String, String> spuInfo = lingXingApiService.getSpuBySku(sku);
}
```

### 3. 异步加载
对于不需要立即获取结果的场景，可以异步加载：

```java
@Async
public CompletableFuture<Map<String, String>> getSpuBySkuAsync(String sku) {
    return CompletableFuture.completedFuture(lingXingApiService.getSpuBySku(sku));
}
```

## 注意事项

1. **API限流**: 领星API有请求频率限制，批量查询时会自动控制请求频率
2. **数据一致性**: 缓存数据可能与实时数据存在延迟，对实时性要求高的场景需要实时查询
3. **内存占用**: 产品数量较大时，缓存会占用一定内存，需要根据实际情况调整
4. **错误处理**: API调用失败时会返回null，使用时需要进行空值判断

## 常见问题

### Q1: 缓存多久更新一次？
A: 缓存不会自动过期，需要手动调用`refreshSkuSpuCache()`或通过定时任务更新。

### Q2: 如何处理SKU不存在的情况？
A: 当SKU不存在时，`getSpuBySku()`方法会返回null，使用时需要进行空值判断。

### Q3: 批量查询有数量限制吗？
A: 领星API单次查询建议不超过1000条，系统会自动分页处理超过限制的请求。

### Q4: 缓存数据存储在哪里？
A: 缓存数据存储在内存中（HashMap），应用重启后需要重新加载。

## 扩展功能建议

1. **持久化缓存**: 可以考虑将缓存数据持久化到Redis，提高系统重启后的恢复速度
2. **缓存预热**: 系统启动时自动加载热点SKU的SPU信息
3. **缓存监控**: 添加缓存命中率、更新频率等监控指标
4. **数据同步**: 与产品信息变更事件联动，实时更新缓存

## 联系方式

如有问题或建议，请联系开发团队。