# 产品表现API接口文档

## 基础信息
- **模块路径**: `/amazon/product-performance`
- **权限前缀**: `amazon:product-performance`

## 接口列表

### 1. 获取产品表现分页数据
**接口地址**: `GET /amazon/product-performance/page`  
**权限**: `amazon:product-performance:query`  
**描述**: 获取亚马逊产品表现的分页数据，支持多维度筛选和汇总

#### 请求参数 (ProductPerformancePageReqVO)
| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| pageNo | Integer | 否 | 页码，默认1 | 1 |
| pageSize | Integer | 否 | 每页条数，默认10 | 20 |
| asin | String | 否 | ASIN编码 | B085M7NH7K |
| parentAsin | String | 否 | 父ASIN编码 | B085M7NH7K |
| msku | String | 否 | MSKU（卖家SKU） | DEE-216 |
| sku | String | 否 | SKU（本地SKU） | Yajie003 |
| sid | Long | 否 | 店铺ID | 109 |
| sids | List\<Long\> | 否 | 店铺ID列表（批量查询） | [109, 110] |
| mid | Integer | 否 | 站点ID | 1 |
| summaryField | String | 否 | 汇总维度 | asin |
| startDate | LocalDate | 否 | 开始日期（YYYY-MM-DD） | 2024-08-01 |
| endDate | LocalDate | 否 | 结束日期（YYYY-MM-DD） | 2024-08-07 |
| itemName | String | 否 | 产品标题（模糊搜索） | Car Floor Mats |

#### 响应参数 (PageResult\<ProductPerformanceRespVO\>)
```json
{
  "total": 100,
  "list": [
    {
      // 基础信息
      "id": 27099,
      "createTime": "2024-08-01 10:00:00",
      "updateTime": "2024-08-01 15:00:00",
      
      // 产品信息
      "asin": "B085M7NH7K",
      "parentAsin": "B085M7NH7K",
      "msku": "DEE-216",
      "sku": "Yajie003",
      "itemName": "Car Floor Mats",
      "smallImageUrl": "https://...",
      "amazonUrl": "https://www.amazon.com/dp/B085M7NH7K",
      "price": 29.99,
      "status": 1,  // -1未同步 1active 0inactive 2incomplete
      
      // 店铺信息
      "sid": 109,
      "mid": 1,
      "sellerName": "MyStore",
      "country": "US",
      "currencyCode": "USD",
      "currencyIcon": "$",
      
      // 销售数据
      "volume": 150,  // 销量
      "orderItems": 120,  // 订单量
      "amount": 4498.50,  // 销售额
      "netAmount": 4048.65,  // 净销售额
      "avgCustomPrice": 29.99,  // 销售均价
      "avgVolume": 5.0,  // 平均销量
      
      // 环比数据
      "volumeChain": 20,  // 环比销量
      "volumeChainRatio": 15.38,  // 销量环比%
      "amountChain": 598.80,  // 环比销售额
      "amountChainRatio": 15.38,  // 销售额环比%
      "orderItemsChain": 16,  // 环比订单量
      "orderChainRatio": 15.38,  // 订单量环比%
      
      // 促销数据
      "promotionVolume": 30,  // 促销销量
      "promotionAmount": 749.70,  // 促销销售额
      "promotionOrderItems": 25,  // 促销订单量
      "promotionDiscount": 5.00,  // 促销折扣
      
      // 毛利数据
      "grossProfit": 1500.00,  // 结算毛利润
      "predictGrossProfit": 1600.00,  // 订单毛利润
      "grossMargin": 33.33,  // 结算毛利率%
      "predictGrossMargin": 35.00,  // 订单毛利率%
      "roi": 2.5,  // ROI
      
      // 评价数据
      "reviewsCount": 256,  // 评论数
      "avgStar": 4.5,  // 评分
      "prevStar": 4.4,  // 前一个评分
      "commentRate": 2.13,  // 留评率%
      
      // 退货数据
      "returnCount": 5,  // 退款量
      "returnRate": 3.33,  // 退款率%
      "returnGoodsCount": 3,  // 退货量
      "returnGoodsRate": 2.00,  // 退货率%
      "returnAmount": 149.95,  // 退款金额
      
      // 库存数据
      "afnFulfillableQuantity": 500,  // FBA可售
      "afnInboundReceivingQuantity": 100,  // FBA入库中
      "afnInboundShippedQuantity": 200,  // FBA在途
      "afnInboundWorkingQuantity": 50,  // FBA计划入库
      "afnUnsellableQuantity": 10,  // FBA不可售
      "reservedFcProcessing": 5,  // 调仓中
      "reservedFcTransfers": 10,  // 待调仓
      "fbmQuantity": 100,  // FBM可售
      "reservedCustomerorders": 20,  // 待发货
      "stockUpNum": 300,  // 实际在途
      "availableDays": 100,  // 可售预估天数
      "fbmAvailableDays": 20,  // FBM可售天数
      "monthStockSalesRatio": 3.33,  // 月库销比
      "inventorySalesRatio": 3.33,  // 存销比
      
      // 流量数据
      "clicks": 1500,  // 点击量
      "sessions": 800,  // Sessions-Browser
      "sessionsMobile": 700,  // Sessions-Mobile
      "sessionsTotal": 1500,  // Sessions-Total
      "pageViews": 1600,  // PV-Browser
      "pageViewsMobile": 1400,  // PV-Mobile
      "pageViewsTotal": 3000,  // PV-Total
      "buyBoxPercentage": 95.00,  // Buybox占比%
      
      // 转化率数据
      "cvr": 8.00,  // 转化率%
      "ctr": 10.00,  // 点击率%
      "volumeCvr": 10.00,  // 销量CVR%
      "adCvr": 5.00,  // 广告CVR%
      
      // 广告数据
      "impressions": 15000,  // 曝光量
      "adClicks": 750,  // 广告点击量
      "adOrderQuantity": 38,  // 广告订单量
      "adSalesAmount": 1139.62,  // 广告销售额
      "spend": 150.00,  // 广告花费
      "cpc": 0.20,  // CPC
      "cpm": 10.00,  // CPM
      "cpo": 3.95,  // CPO
      "acos": 13.16,  // ACOS%
      "acoas": 0.13,  // ACoAS
      "roas": 7.60,  // ROAS
      "asoas": 0.10,  // ASoAS
      "advRate": 31.67,  // 广告订单量占比%
      "tacos": 3.33,  // TACOS%
      
      // 广告细分数据
      "adsSpCost": 100.00,  // SP广告费
      "adsSpSales": 800.00,  // SP广告销售额
      "adsSdCost": 30.00,  // SD广告费
      "adsSdSales": 239.62,  // SD广告销售额
      "sharedAdsSbCost": 15.00,  // SB广告费
      "sharedAdsSbSales": 80.00,  // SB广告销售额
      "sharedAdsSbvCost": 5.00,  // SBV广告费
      "sharedAdsSbvSales": 20.00,  // SBV广告销售额
      "sharedCostOfAdvertising": 0.00,  // 差异分摊
      "adDirectSalesAmount": 1000.00,  // 直接成交销售额
      "adDirectOrderQuantity": 35,  // 直接成交订单量
      
      // 排名数据
      "cateRank": 1500,  // 大类排名
      "prevCateRank": 1600,  // 上一次大类排名
      "rankCategory": "Automotive",  // 大类排名分类
      "smallCateRank": [  // 小类排名
        {
          "category": "Car Floor Mats",
          "rank": 50,
          "prevRank": 55
        }
      ],
      
      // 分类和标签信息
      "categories": ["Automotive", "Car Accessories"],  // 分类
      "brands": ["Brand A"],  // 品牌
      "principalNames": ["John Doe"],  // 负责人
      "developerNames": ["Jane Smith"],  // 开发人
      "suppliers": ["Supplier A"],  // 供应商
      "attributes": ["Waterproof", "All Weather"],  // 属性
      "tagSet": [  // 标签
        {
          "globalTagId": "tag001",
          "tagName": "热销品",
          "color": "#FF0000"
        }
      ],
      "priceList": [  // 价格列表
        {
          "localName": "汽车脚垫",
          "localSku": "Yajie003",
          "sellerSku": "DEE-216",
          "price": 29.99,
          "country": "US",
          "sellerName": "MyStore",
          "mid": 1,
          "sid": 109,
          "volume": 150,
          "smallImageUrl": "https://...",
          "status": 1
        }
      ],
      
      // SKU维度特有字段
      "localName": "汽车脚垫",  // 品名
      "cgPrice": 8.50,  // 采购成本
      "whsValue": 4250.00,  // 可用货值
      "localQuantity": 300,  // 本地可用
      "overseaQuantity": 200,  // 海外仓可用
      "avgLandedPrice": 12.00,  // 平均售价
      "model": ["Model A", "Model B"],  // 型号
      
      // 业务字段
      "summaryField": "asin",  // 汇总维度
      "startDate": "2024-08-01",  // 数据开始日期
      "endDate": "2024-08-07",  // 数据结束日期
      "syncTime": "2024-08-08 02:00:00",  // 同步时间
      
      // 运营分析扩展字段
      "primePrice": 27.99,  // Prime价格
      "couponPrice": 24.99,  // Coupon价格
      "couponDiscount": 16.67,  // Coupon折扣率%
      "targetOrders": 200,  // 目标订单
      "promotionActivity": "Summer Sale",  // 促销活动
      "keywordCount": 50,  // 关键词数量
      "p1Keywords": 10,  // P1关键词数量 (数据库字段: p1_keywords)
      "p2Keywords": 20,  // P2关键词数量 (数据库字段: p2_keywords)
      "p3Keywords": 20,  // P3关键词数量 (数据库字段: p3_keywords)
      "totalTraffic": 2000,  // 总流量
      "totalSalesAmount": 5998.00,  // 总销售额
      "inventoryAge0To90": 400,  // 0-90天库龄 (数据库字段: inventory_age_0_90)
      "inventoryAge91To180": 80,  // 91-180天库龄 (数据库字段: inventory_age_91_180)
      "inventoryAge180Plus": 20,  // 180+天库龄 (数据库字段: inventory_age_180_plus)
      "expectedRedundantProducts": 10,  // 预计冗余商品
      "storageFee30Days": 50.00,  // 30天仓储费 (数据库字段: storage_fee_30_days)
      "storageFee180To365Quantity": 20,  // 180-365天收费数量 (数据库字段: storage_fee_180_365_quantity)
      "storageFee180To365Amount": 100.00,  // 180-365天收费金额 (数据库字段: storage_fee_180_365_amount)
      "breakEvenTacos": 15.00,  // 盈利平衡TACOS%
      "operationalAnalysis": "产品表现良好，建议增加库存"  // 运营分析
    }
  ]
}
```

### 2. 获取单个产品表现详情
**接口地址**: `GET /amazon/product-performance/get`  
**权限**: `amazon:product-performance:query`  
**描述**: 根据ID获取单个产品表现的详细信息

#### 请求参数
| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Long | 是 | 产品表现记录ID | 1024 |

#### 响应参数
同上面的 ProductPerformanceRespVO 对象结构

### 3. 同步产品表现数据
**接口地址**: `POST /amazon/product-performance/sync`  
**权限**: `amazon:product-performance:sync`  
**描述**: 同步指定ASIN列表的产品表现数据

#### 请求参数 (ProductPerformanceSyncReqVO)
```json
{
  "asins": ["B085M7NH7K", "B085M7NH7L"],  // ASIN列表，为空时查询店铺所有产品
  "sids": [109, 110],  // 店铺ID列表（必填）
  "startDate": "2024-08-01",  // 开始日期（必填）
  "endDate": "2024-08-07",  // 结束日期（必填）
  "summaryField": "asin",  // 汇总维度，默认"asin"
  "mid": 1,  // 站点ID
  "currencyCode": "CNY",  // 货币类型
  "isRecentlyEnum": true  // 是否仅查询活跃商品，默认true
}
```

#### 响应参数
```json
{
  "code": 0,
  "data": "同步任务已提交，正在处理中...",
  "msg": "success"
}
```

### 4. 同步店铺所有产品表现数据
**接口地址**: `POST /amazon/product-performance/sync-all`  
**权限**: `amazon:product-performance:sync`  
**描述**: 同步店铺的所有产品表现数据（不指定ASIN）

#### 请求参数
同 ProductPerformanceSyncReqVO，但会忽略 asins 参数

#### 响应参数
同上

## 数据说明

### 汇总维度 (summaryField)
- `asin`: 按ASIN汇总
- `parent_asin`: 按父ASIN汇总
- `msku`: 按MSKU汇总  
- `sku`: 按SKU汇总

### 商品状态 (status)
- `-1`: 未同步
- `0`: inactive（非活跃）
- `1`: active（活跃）
- `2`: incomplete（不完整）

### 站点ID (mid) 对应关系
- 1: 美国站
- 2: 加拿大站
- 3: 墨西哥站
- 4: 英国站
- 5: 德国站
- 6: 法国站
- 7: 意大利站
- 8: 西班牙站
- 9: 日本站
- 10: 澳大利亚站
- 其他站点请查询系统配置

## 注意事项
1. 分页查询时，建议指定日期范围以提高查询效率
2. 同步数据接口为异步处理，提交后需要等待一段时间才能查询到最新数据
3. 批量查询店铺时使用 `sids` 参数，单个店铺查询使用 `sid` 参数
4. 所有百分比字段的值都是实际百分比数值，如 `50.00` 表示 50%
5. 金额字段均为指定货币的金额值
6. 日期格式统一使用 `YYYY-MM-DD`
7. 时间格式统一使用 `YYYY-MM-DD HH:mm:ss`
8. 部分字段的数据库字段名与Java字段名有差异（已在响应示例中标注），主要涉及包含数字的字段：
   - Java字段名采用驼峰命名（如 `inventoryAge0To90`）
   - 数据库字段名在数字前后都有下划线（如 `inventory_age_0_90`）

## 权限说明
- 查询权限: `amazon:product-performance:query`
- 同步权限: `amazon:product-performance:sync`