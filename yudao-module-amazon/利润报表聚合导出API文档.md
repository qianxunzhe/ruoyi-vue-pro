# 利润报表聚合数据导出 API 文档

## 接口概述
该接口用于导出利润报表的聚合数据，支持按不同维度（ASIN、SPU、SKU、店铺、日期）导出Excel文件。

## 接口信息

### 基本信息
- **接口地址**: `/amazon/profit-report/export-aggregate-excel`
- **请求方式**: GET
- **权限要求**: `amazon:profit-report:export`
- **返回格式**: Excel文件下载

### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 示例值 |
|--------|------|------|------|--------|
| groupBy | String | 否 | 聚合维度，可选值：ASIN、SPU、SKU、SHOP/SID、DATE<br>默认值：SKU | ASIN |
| startDate | String | 否 | 开始日期，格式：YYYY-MM-DD | 2024-01-01 |
| endDate | String | 否 | 结束日期，格式：YYYY-MM-DD | 2024-01-31 |
| sid | String | 否 | 店铺ID | 101 |
| storeName | String | 否 | 店铺名称 | US Store 1 |
| countryCode | String | 否 | 国家代码 | US |
| asin | String | 否 | ASIN | B001234567 |
| parentAsin | String | 否 | 父ASIN | B001234500 |
| spu | String | 否 | SPU | SPU001 |
| localSku | String | 否 | SKU | SKU001 |
| localName | String | 否 | 产品名称 | Product A |
| pageNo | Integer | 否 | 页码（导出时会忽略分页，导出全部数据） | 1 |
| pageSize | Integer | 否 | 每页大小（导出时会忽略分页，导出全部数据） | 10 |

### 响应说明

接口直接返回Excel文件流，浏览器会自动下载文件。

#### 文件命名规则
- ASIN维度：`利润报表-ASIN聚合.xls`
- SPU维度：`利润报表-SPU聚合.xls`
- SKU维度：`利润报表-SKU聚合.xls`
- 店铺维度：`利润报表-店铺聚合.xls`
- 日期维度：`利润报表-日期聚合.xls`

### 各维度导出字段说明

#### ASIN维度导出字段
| Excel列名 | 字段说明 | 数据类型 |
|-----------|---------|----------|
| ASIN | ASIN编码 | String |
| 父ASIN | 父ASIN编码 | String |
| 产品名称 | 产品名称 | String |
| SPU | SPU编码 | String |
| SKU数量 | 关联的SKU数量 | Integer |
| 店铺数量 | 销售店铺数量 | Integer |
| 总销售数量 | 总销售数量 | Integer |
| FBA销售数量 | FBA销售数量 | Integer |
| FBM销售数量 | FBM销售数量 | Integer |
| 退款数量 | 退款数量 | Integer |
| 销售收入 | 总销售金额 | BigDecimal |
| FBA销售金额 | FBA销售金额 | BigDecimal |
| FBM销售金额 | FBM销售金额 | BigDecimal |
| 退款金额 | 退款金额 | BigDecimal |
| 退货退款 | 退货退款总额 | BigDecimal |
| 销售成本 | 产品采购成本 | BigDecimal |
| 头程运费 | 头程运输费用 | BigDecimal |
| 其他业务成本 | 其他业务成本 | BigDecimal |
| 其他业务收入 | 其他业务收入 | BigDecimal |
| 推广费 | 推广费用 | BigDecimal |
| 站外费 | 站外费用 | BigDecimal |
| 其他费用 | 其他费用 | BigDecimal |
| 广告费 | 广告花费 | BigDecimal |
| 广告销售额 | 广告带来的销售额 | BigDecimal |
| 平台费 | 平台费用 | BigDecimal |
| 配送费 | FBA配送费 | BigDecimal |
| 仓储费 | 仓储费用 | BigDecimal |
| 总成本 | 总成本 | BigDecimal |
| 毛利润 | 毛利润 | BigDecimal |
| 毛利率(%) | 毛利率百分比 | BigDecimal |
| ACOS(%) | 广告销售成本比 | BigDecimal |

#### SPU维度导出字段
| Excel列名 | 字段说明 | 数据类型 |
|-----------|---------|----------|
| SPU | SPU编码 | String |
| 父ASIN | 父ASIN编码 | String |
| 产品名称 | 产品系列名称 | String |
| SKU数量 | SKU数量 | Integer |
| ASIN数量 | ASIN数量 | Integer |
| 总销售数量 | 总销售数量 | Integer |
| 销售收入 | 总销售金额 | BigDecimal |
| 退款数量 | 退款数量 | Integer |
| 退款金额 | 退款金额 | BigDecimal |
| 退货退款 | 退货退款总额 | BigDecimal |
| 销售成本 | 产品采购成本 | BigDecimal |
| 头程运费 | 头程运输费用 | BigDecimal |
| 其他业务成本 | 其他业务成本 | BigDecimal |
| 其他业务收入 | 其他业务收入 | BigDecimal |
| 推广费 | 推广费用 | BigDecimal |
| 站外费 | 站外费用 | BigDecimal |
| 其他费用 | 其他费用 | BigDecimal |
| 广告费 | 广告花费 | BigDecimal |
| 广告销售额 | 广告带来的销售额 | BigDecimal |
| 平台费 | 平台费用 | BigDecimal |
| 配送费 | 配送费用 | BigDecimal |
| 仓储费 | 仓储费用 | BigDecimal |
| 总费用 | 总费用 | BigDecimal |
| 总成本 | 总成本 | BigDecimal |
| 毛利润 | 毛利润 | BigDecimal |
| 毛利率(%) | 毛利率百分比 | BigDecimal |
| ACOS(%) | 广告销售成本比 | BigDecimal |
| ROAS | 广告投资回报率 | BigDecimal |

#### SKU维度导出字段
| Excel列名 | 字段说明 | 数据类型 |
|-----------|---------|----------|
| SKU | SKU编码 | String |
| 产品名称 | 产品名称 | String |
| 店铺数量 | 销售店铺数量 | Integer |
| 总销售数量 | 总销售数量 | Integer |
| FBA销售数量 | FBA销售数量 | Integer |
| FBM销售数量 | FBM销售数量 | Integer |
| 退款数量 | 退款数量 | Integer |
| 销售收入 | 总销售金额 | BigDecimal |
| FBA销售金额 | FBA销售金额 | BigDecimal |
| FBM销售金额 | FBM销售金额 | BigDecimal |
| 退货退款 | 退货退款总额 | BigDecimal |
| 其他业务收入 | 其他业务收入 | BigDecimal |
| 其他业务成本 | 其他业务成本 | BigDecimal |
| 推广费 | 推广费用 | BigDecimal |
| 站外费 | 站外费用 | BigDecimal |
| 其他费用 | 其他费用 | BigDecimal |
| 广告费 | 广告花费 | BigDecimal |
| 广告销售额 | 广告带来的销售额 | BigDecimal |
| 平台费 | 平台费用 | BigDecimal |
| 配送费 | FBA配送费 | BigDecimal |
| 仓储费 | 仓储费用 | BigDecimal |
| 销售成本 | 产品采购成本 | BigDecimal |
| 头程运费 | 头程运输费用 | BigDecimal |
| 总成本 | 总成本 | BigDecimal |
| 毛利润 | 毛利润 | BigDecimal |
| 毛利率(%) | 毛利率百分比 | BigDecimal |
| ACOS(%) | 广告销售成本比 | BigDecimal |
| 统计天数 | 统计天数 | Long |

#### 店铺维度导出字段
| Excel列名 | 字段说明 | 数据类型 |
|-----------|---------|----------|
| 店铺ID | 店铺ID | String |
| 店铺名称 | 店铺名称 | String |
| 国家代码 | 国家代码 | String |
| SKU数量 | SKU数量 | Long |
| ASIN数量 | ASIN数量 | Long |
| 总销售数量 | 总销售数量 | Integer |
| 销售收入 | 总销售金额 | BigDecimal |
| FBA销售金额 | FBA销售金额 | BigDecimal |
| FBM销售金额 | FBM销售金额 | BigDecimal |
| 退款金额 | 退款金额 | BigDecimal |
| 退货退款 | 退货退款总额 | BigDecimal |
| 销售成本 | 产品采购成本 | BigDecimal |
| 头程运费 | 头程运输费用 | BigDecimal |
| 其他业务成本 | 其他业务成本 | BigDecimal |
| 其他业务收入 | 其他业务收入 | BigDecimal |
| 推广费 | 推广费用 | BigDecimal |
| 站外费 | 站外费用 | BigDecimal |
| 其他费用 | 其他费用 | BigDecimal |
| 广告费 | 广告花费 | BigDecimal |
| 广告销售额 | 广告带来的销售额 | BigDecimal |
| 平台费 | 平台费用 | BigDecimal |
| 配送费 | FBA配送费 | BigDecimal |
| 仓储费 | 仓储费用 | BigDecimal |
| 总成本 | 总成本 | BigDecimal |
| 毛利润 | 毛利润 | BigDecimal |
| 毛利率(%) | 毛利率百分比 | BigDecimal |
| 退款率(%) | 退款率百分比 | BigDecimal |

#### 日期维度导出字段
| Excel列名 | 字段说明 | 数据类型 |
|-----------|---------|----------|
| 日期 | 统计日期 | LocalDate |
| ASIN数量 | ASIN数量 | Long |
| SKU数量 | SKU数量 | Long |
| 店铺数量 | 店铺数量 | Long |
| 总销售数量 | 总销售数量 | Integer |
| FBA销售数量 | FBA销售数量 | Integer |
| FBM销售数量 | FBM销售数量 | Integer |
| 退款数量 | 退款数量 | Integer |
| 销售收入 | 总销售金额 | BigDecimal |
| FBA销售金额 | FBA销售金额 | BigDecimal |
| FBM销售金额 | FBM销售金额 | BigDecimal |
| 退款金额 | 退款金额 | BigDecimal |
| 退货退款 | 退货退款总额 | BigDecimal |
| 销售成本 | 产品采购成本 | BigDecimal |
| 头程运费 | 头程运输费用 | BigDecimal |
| 其他业务成本 | 其他业务成本 | BigDecimal |
| 其他业务收入 | 其他业务收入 | BigDecimal |
| 推广费 | 推广费用 | BigDecimal |
| 站外费 | 站外费用 | BigDecimal |
| 其他费用 | 其他费用 | BigDecimal |
| 广告费 | 广告花费 | BigDecimal |
| 广告销售额 | 广告带来的销售额 | BigDecimal |
| 平台费 | 平台费用 | BigDecimal |
| 配送费 | FBA配送费 | BigDecimal |
| 仓储费 | 仓储费用 | BigDecimal |
| 总成本 | 总成本 | BigDecimal |
| 毛利润 | 毛利润 | BigDecimal |
| 毛利率(%) | 毛利率百分比 | BigDecimal |
| ACOS(%) | 广告销售成本比 | BigDecimal |

## 前端调用示例

### JavaScript/Axios 示例
```javascript
// 导出ASIN维度数据
async function exportProfitReportByAsin() {
  try {
    const response = await axios({
      url: '/amazon/profit-report/export-aggregate-excel',
      method: 'GET',
      params: {
        groupBy: 'ASIN',
        startDate: '2024-01-01',
        endDate: '2024-01-31',
        sid: '101'  // 可选：筛选特定店铺
      },
      responseType: 'blob'  // 重要：设置响应类型为blob
    });
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', '利润报表-ASIN聚合.xls');
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('导出失败:', error);
  }
}

// 导出SPU维度数据
async function exportProfitReportBySpu() {
  const params = {
    groupBy: 'SPU',
    startDate: '2024-01-01',
    endDate: '2024-01-31'
  };
  
  // 构建查询字符串
  const queryString = new URLSearchParams(params).toString();
  
  // 直接通过window.open下载
  window.open(`/amazon/profit-report/export-aggregate-excel?${queryString}`);
}
```

### Vue 3 示例
```vue
<template>
  <el-button @click="handleExport" :loading="exporting">
    导出Excel
  </el-button>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

const exporting = ref(false);

// 查询条件
const queryParams = ref({
  groupBy: 'ASIN',  // 聚合维度
  startDate: '2024-01-01',
  endDate: '2024-01-31',
  sid: null,
  storeName: null,
  asin: null
});

const handleExport = async () => {
  exporting.value = true;
  try {
    const response = await fetch('/amazon/profit-report/export-aggregate-excel?' + 
      new URLSearchParams(queryParams.value), {
      method: 'GET',
      headers: {
        'Authorization': 'Bearer ' + getToken()  // 添加token
      }
    });
    
    if (!response.ok) {
      throw new Error('导出失败');
    }
    
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `利润报表-${queryParams.value.groupBy}聚合.xls`;
    a.click();
    window.URL.revokeObjectURL(url);
    
    ElMessage.success('导出成功');
  } catch (error) {
    ElMessage.error('导出失败: ' + error.message);
  } finally {
    exporting.value = false;
  }
};
</script>
```

### 注意事项

1. **权限验证**：调用接口需要有 `amazon:profit-report:export` 权限
2. **大数据量**：导出大量数据时可能需要较长时间，建议添加loading提示
3. **日期范围**：建议限制日期范围，避免一次导出过多数据
4. **错误处理**：需要处理网络错误、权限错误等异常情况
5. **文件格式**：导出的文件格式为 `.xls`，兼容Excel 2003及以上版本

## 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 401 | 未授权 | 检查登录状态和token |
| 403 | 无权限 | 检查用户是否有导出权限 |
| 400 | 参数错误 | 检查groupBy参数是否正确 |
| 500 | 服务器错误 | 联系管理员 |