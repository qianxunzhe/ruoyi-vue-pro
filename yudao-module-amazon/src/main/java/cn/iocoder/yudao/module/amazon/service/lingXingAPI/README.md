# 领星API服务 - 亚马逊店铺列表查询

## 功能说明

本服务实现了调用领星ERP API查询企业已授权的亚马逊店铺列表功能。

## 主要功能

### 1. 查询亚马逊店铺列表
- **接口**: `LingXingApiService.getAmazonStoreList()`
- **API路径**: `/erp/sc/data/seller/lists`
- **请求方式**: GET
- **返回类型**: `LingXingApiResponseDTO<List<AmazonStoreDTO>>`

### 2. 获取美国正常店铺sid列表
- **接口**: `LingXingApiService.getUSNormalStoreSids()`
- **功能**: 获取所有美国（NA区域）且状态为正常（status=1）的店铺sid列表
- **返回类型**: `List<Long>`

## 数据结构

### AmazonStoreDTO
店铺信息数据传输对象，包含以下字段：

| 字段名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| sid | Long | 店铺id，领星ERP对企业已授权店铺的唯一标识 | 1519 |
| mid | Long | 站点id | 1 |
| name | String | 店铺名 | "test账号" |
| sellerId | String | 亚马逊店铺id | "AZTOL**" |
| accountName | String | 店铺账户名称 | "account**" |
| sellerAccountId | Long | 店铺账号id | 1 |
| region | String | 站点简称，例如NA指北美 | "EU" |
| country | String | 商城所在国家名称 | "西班牙" |
| hasAdsSetting | Integer | 是否授权广告：0 否，1 是 | 0 |
| marketplaceId | String | 市场id | "ATVPDKIKX0DER" |
| status | Integer | 店铺状态：0 停止同步，1 正常，2 授权异常，3 欠费停服 | 1 |

### 店铺状态说明
- **0**: 停止同步
- **1**: 正常 
- **2**: 授权异常
- **3**: 欠费停服

### 区域说明
- **NA**: 北美
- **EU**: 欧洲

## 配置说明

### application.yml 配置示例

```yaml
amazon:
  lingxing:
    enabled: true                                    # 是否启用领星API
    api-domain: https://openapi.lingxing.com         # API域名
    app-id: your_app_id                              # 应用ID
    app-key: your_app_key                            # 应用Key（通常与app-id相同）
    app-secret: your_app_secret                      # 应用密钥
    timeout-seconds: 30                              # 请求超时时间（秒）
```

### 必需配置项
- `app-id`: 领星开放平台提供的应用ID
- `app-key`: 应用Key（通常与app-id相同，或由平台分配）
- `app-secret`: 领星开放平台提供的应用密钥

### 配置说明
1. **app-id**: 从领星开放平台获取的应用标识符
2. **app-key**: 在某些情况下与app-id相同，请参考领星开放平台文档或咨询客服
3. **app-secret**: 用于签名验证的密钥，请妥善保管

## 常见问题及解决方案

### 1. App Secret 错误
**错误信息**: `app secret not correct`

**解决方案**:
- 检查配置文件中的`app-secret`是否正确
- 确认在领星开放平台中查看的密钥是否正确
- 注意密钥中是否包含特殊字符，确保完整复制

### 2. 缺少参数错误
**错误信息**: `missing query param(access_token,sign,timestamp,app_key)`

**解决方案**:
- 确保配置了所有必需参数：`app-id`, `app-key`, `app-secret`
- 检查app-key配置是否正确

### 3. 访问令牌获取失败
**现象**: access_token为null

**解决方案**:
1. 首先解决App Secret错误问题
2. 确保网络连接正常
3. 检查API域名配置是否正确

### 4. 配置检查步骤

1. **检查配置**: 
   ```bash
   GET /admin-api/amazon/lingxing/test/config
   ```

2. **测试连接**: 
   ```bash
   GET /admin-api/amazon/lingxing/test/ping
   ```

3. **获取Token**: 
   ```bash
   POST /admin-api/amazon/lingxing/token
   ```

## 使用示例

### 1. 查询所有店铺列表

```java
@Resource
private LingXingApiService lingXingApiService;

public void queryAllStores() {
    LingXingApiResponseDTO<List<AmazonStoreDTO>> result = lingXingApiService.getAmazonStoreList();
    
    if (result.isSuccess()) {
        List<AmazonStoreDTO> stores = result.getData();
        log.info("获取到{}个店铺", stores.size());
        
        for (AmazonStoreDTO store : stores) {
            log.info("店铺信息: sid={}, name={}, region={}, status={}", 
                store.getSid(), store.getName(), store.getRegion(), store.getStatus());
        }
    } else {
        log.error("获取店铺列表失败: {}", result.getMessage());
    }
}
```

### 2. 获取美国正常店铺sid列表

```java
public void getUSStores() {
    List<Long> usSids = lingXingApiService.getUSNormalStoreSids();
    
    if (!usSids.isEmpty()) {
        log.info("获取到{}个美国正常店铺sid: {}", usSids.size(), usSids);
    } else {
        log.warn("没有找到美国正常状态的店铺");
    }
}
```

## API响应示例

```json
{
    "code": 0,
    "msg": "success",
    "error_details": [],
    "request_id": "B34C8F27-F3CA-EE25-836C-32FABBD8B8CD",
    "response_time": "2021-11-10 17:28:33",
    "data": [
        {
            "sid": 1,
            "mid": 1,
            "name": "店铺1",
            "seller_id": "AZTOL********",
            "account_name": "account**",
            "seller_account_id": 2,
            "region": "EU",
            "country": "西班牙",
            "has_ads_setting": 0,
            "marketplace_id": "ATVPDKIKX0DER",
            "status": 1
        }
    ]
}
```

## 测试

### 单元测试

```bash
# 运行领星API服务测试
mvn test -Dtest=LingXingApiServiceTest
```

### 手动测试

可以通过访问测试接口进行手动测试：

- **配置检查**: `GET /admin-api/amazon/lingxing/test/config`
- **连接测试**: `GET /admin-api/amazon/lingxing/test/ping`
- **获取Token**: `POST /admin-api/amazon/lingxing/token`

## 注意事项

1. **令牌管理**: 服务会自动管理访问令牌的获取和刷新，有效期30分钟
2. **错误处理**: 所有API调用都包含完整的错误处理和日志记录
3. **签名验证**: 使用领星官方SDK进行请求签名
4. **配置安全**: 请妥善保管App Secret，避免泄露
5. **参数完整性**: 确保app-id、app-key、app-secret三个参数都正确配置

## 依赖

项目需要以下依赖：

```xml
<!-- 领星SDK -->
<dependency>
    <groupId>com.asinking.openapi</groupId>
    <artifactId>asinking-openapi-sdk</artifactId>
    <version>latest</version>
</dependency>
```

## 联系支持

如果遇到配置问题，建议：
1. 检查领星开放平台文档
2. 联系领星技术支持确认app-key的正确获取方式
3. 确认账号权限和API访问权限 