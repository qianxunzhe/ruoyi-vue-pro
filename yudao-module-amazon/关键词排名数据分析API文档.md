# 关键词排名数据分析API文档

## 概述

关键词排名数据分析功能提供类似Excel的矩阵视图，支持单关键词和多关键词的排名数据分析，包括位置竞争分析和ASIN覆盖度分析。

## 接口信息

- **接口路径**: `GET /amazon/keyword-ranking/analysis`
- **权限要求**: `amazon:keyword-ranking:query`
- **数据权限**: 启用租户和用户隔离

## 请求参数

### 基础参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| startDate | LocalDate | 否 | 7天前 | 开始日期，格式：yyyy-MM-dd |
| endDate | LocalDate | 否 | 今天 | 结束日期，格式：yyyy-MM-dd |
| city | String | 是 | - | 搜索城市/地区 |
| rankType | Short | 是 | - | 排名类型 |
| keywords | List<String> | 否 | - | 关键词列表，最多5个 |
| taskId | Long | 否 | - | 任务ID筛选 |
| maxPosition | Integer | 否 | 50 | 最大位置数，不超过100 |

### 高级参数

| 参数名 | 类型 | 可选值 | 默认值 | 说明 |
|--------|------|--------|--------|------|
| aggregationType | String | latest/first/all | latest | 数据聚合方式 |
| displayMode | String | single/comparison | single | 显示模式 |
| comparisonType | String | position/asin/coverage | position | 对比类型 |

### 聚合方式说明

- **latest**: 取每天最后一次爬取的数据（推荐用于一天多次任务）
- **first**: 取每天第一次爬取的数据
- **all**: 显示所有数据（可能包含同一天的多次数据）

## 响应数据结构

### 主要响应结构

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "summary": { ... },      // 汇总信息
    "matrix": { ... },       // 矩阵数据
    "statistics": { ... },   // 统计信息
    "comparison": { ... }    // 对比分析（仅对比模式）
  }
}
```

### 汇总信息 (summary)

```json
{
  "dateRange": ["2024-07-15", "2024-07-16", "2024-07-17"],
  "totalDays": 3,
  "totalPositions": 50,
  "keywords": ["iPhone case", "phone cover"],
  "city": "New York",
  "rankType": 1
}
```

### 矩阵数据 (matrix)

```json
{
  "headers": ["位置", "7月15日", "7月16日", "7月17日"],
  "keywordMatrices": {
    "iPhone case": {
      "keyword": "iPhone case",
      "rows": [
        {
          "position": "P1-01",
          "positionCode": "1-1",
          "pageNumber": 1,
          "positionInPage": 1,
          "data": ["ASIN1", "ASIN1", "ASIN2"]
        }
      ]
    }
  }
}
```

### 统计信息 (statistics)

```json
{
  "asinFrequency": {
    "ASIN1": 25,
    "ASIN2": 18
  },
  "positionStability": {
    "P1-01": 0.85,
    "P1-02": 0.71
  },
  "keywordCoverage": {
    "iPhone case": {
      "keyword": "iPhone case",
      "coveredPositions": 25,
      "totalPositions": 50,
      "coverageRate": 0.5
    }
  }
}
```

### 对比分析 (comparison)

```json
{
  "positionCompetitions": [
    {
      "position": "P1-01",
      "dates": ["2024-07-15", "2024-07-16"],
      "competitions": [
        {
          "date": "2024-07-15",
          "sameAsins": ["ASIN1"],
          "differentAsins": {
            "iPhone case": "ASIN2",
            "phone cover": "ASIN3"
          }
        }
      ]
    }
  ],
  "asinCoverages": {
    "ASIN1": {
      "asin": "ASIN1",
      "keywords": ["iPhone case", "phone cover"],
      "positions": ["P1-01", "P1-03"],
      "coverageRate": 0.67,
      "averagePosition": 2.3,
      "stabilityScore": 0.85
    }
  }
}
```

## 使用场景

### 1. 单关键词分析

**用途**: 分析单个关键词在不同位置和时间的排名变化

**请求示例**:
```json
{
  "city": "New York",
  "rankType": 1,
  "keywords": ["iPhone case"],
  "startDate": "2024-07-15",
  "endDate": "2024-07-21",
  "displayMode": "single"
}
```

**前端展示**: 单个矩阵表格，类似Excel

### 2. 多关键词对比分析

**用途**: 对比多个关键词的排名竞争情况

**请求示例**:
```json
{
  "city": "New York",
  "rankType": 1,
  "keywords": ["iPhone case", "phone cover", "mobile case"],
  "startDate": "2024-07-15",
  "endDate": "2024-07-21",
  "displayMode": "comparison",
  "comparisonType": "position"
}
```

**前端展示**: Tab切换或并排显示多个矩阵

### 3. ASIN覆盖度分析

**用途**: 分析ASIN在不同关键词中的覆盖情况

**请求示例**:
```json
{
  "city": "New York",
  "rankType": 1,
  "keywords": ["iPhone case", "phone cover"],
  "displayMode": "comparison",
  "comparisonType": "coverage"
}
```

**前端展示**: ASIN覆盖度统计图表

## 性能优化

### 1. 数据库优化

建议添加以下索引：
```sql
CREATE INDEX idx_ranking_analysis ON amazon_keyword_ranking 
(tenant_id, user_id, city, rank_type, keyword, crawl_date, crawl_time);
```

### 2. 缓存策略

- Redis缓存热点查询结果，TTL: 1小时
- 缓存Key格式: `ranking:analysis:{tenantId}:{userId}:{city}:{rankType}:{keyword}:{startDate}:{endDate}`

### 3. 查询限制

- 最大查询时间范围: 30天
- 最大关键词数量: 5个
- 最大位置数: 100个

## 错误处理

### 常见错误码

| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 参数校验失败 | 检查必填参数和参数格式 |
| 403 | 权限不足 | 确认用户具有查询权限 |
| 500 | 数据查询异常 | 检查数据库连接和SQL语句 |

### 数据为空处理

- 无数据时返回空矩阵结构
- 提供友好的提示信息
- 建议调整查询条件

## 前端集成建议

### 1. 表格展示

```javascript
// 矩阵表格渲染
const renderMatrix = (keywordMatrix) => {
  return (
    <Table>
      <thead>
        <tr>
          {headers.map(header => <th key={header}>{header}</th>)}
        </tr>
      </thead>
      <tbody>
        {keywordMatrix.rows.map(row => (
          <tr key={row.position}>
            <td>{row.position}</td>
            {row.data.map((asin, index) => (
              <td key={index}>{asin || '-'}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </Table>
  );
};
```

### 2. Tab切换

```javascript
// 多关键词Tab切换
const KeywordTabs = ({ keywordMatrices }) => {
  const [activeTab, setActiveTab] = useState(Object.keys(keywordMatrices)[0]);
  
  return (
    <div>
      <div className="tabs">
        {Object.keys(keywordMatrices).map(keyword => (
          <button 
            key={keyword}
            className={activeTab === keyword ? 'active' : ''}
            onClick={() => setActiveTab(keyword)}
          >
            {keyword}
          </button>
        ))}
      </div>
      <div className="tab-content">
        {renderMatrix(keywordMatrices[activeTab])}
      </div>
    </div>
  );
};
```

### 3. 统计图表

```javascript
// ASIN频次图表
const AsinFrequencyChart = ({ asinFrequency }) => {
  const data = Object.entries(asinFrequency).map(([asin, count]) => ({
    name: asin,
    value: count
  }));
  
  return <BarChart data={data} />;
};
```

## 扩展功能

### 1. 导出功能

可以扩展现有的Excel导出功能，支持矩阵数据导出：

```java
@GetMapping("/analysis/export")
public void exportAnalysisExcel(KeywordRankingAnalysisReqVO reqVO, 
                               HttpServletResponse response) {
    // 实现矩阵数据的Excel导出
}
```

### 2. 实时更新

可以结合WebSocket实现数据的实时更新：

```java
@EventListener
public void onRankingDataUpdate(RankingUpdateEvent event) {
    // 推送更新到前端
}
```

### 3. 趋势分析

可以添加趋势分析功能：

```java
public TrendAnalysisVO getTrendAnalysis(TrendAnalysisReqVO reqVO) {
    // 计算排名趋势、上升下降等
}
```

## 总结

该API提供了完整的关键词排名数据分析功能，支持：

1. **方案一**: 分层展示 - 多关键词独立矩阵
2. **方案四**: 智能对比分析 - 关键词竞争和ASIN覆盖度分析

通过灵活的参数配置，可以满足不同的业务分析需求，为用户提供直观的数据洞察。