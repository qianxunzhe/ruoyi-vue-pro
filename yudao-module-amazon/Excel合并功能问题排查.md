# Excel合并功能问题排查

## 问题修复说明

已修复的主要问题：

### 1. Excel读写格式问题
- **原问题**：使用通用Map读取Excel，导致数据结构丢失
- **修复**：使用`LinkedHashMap<Integer, String>`保持列的顺序和索引

### 2. Sheet名称匹配问题  
- **原问题**：Sheet名称超过31字符被截断后无法匹配
- **修复**：增加了截断名称的智能匹配逻辑

### 3. 数据合并逻辑
- **原问题**：新数据没有正确追加到对应行
- **修复**：改进了数据行匹配算法，按项目名称精确匹配

## 验证步骤

### 1. 查看服务器日志
启动应用后，执行合并操作时应该能看到以下日志：
```
开始合并Excel文件，历史文件大小：xxx，新数据Sheet数量：xxx
读取到历史文件中的Sheet数量：xxx
处理Sheet：xxx，历史数据行数：xxx
提取到日期范围：2025-09-08至2025-09-14
Sheet xxx 合并后的数据行数：xxx，列数：xxx
合并完成，生成的Excel文件大小：xxx
```

### 2. 使用测试脚本
```bash
# 获取token（先登录获取）
TOKEN="your_token_here"

# 运行测试脚本
./test-merge-export.sh $TOKEN
```

### 3. 手动测试（使用Postman）

#### 步骤1：导出初始数据
```
POST /admin-api/amazon/profit-report/export-aggregate-excel
Headers: Authorization: Bearer {token}
Body (form-data):
  - groupBy: SPU
  - startDate: 2025-09-01
  - endDate: 2025-09-07  
  - sid: 10730,10565,10549
  - mergeHistory: false
```
保存返回的Excel文件

#### 步骤2：合并新数据
```
POST /admin-api/amazon/profit-report/export-aggregate-excel
Headers: Authorization: Bearer {token}
Body (form-data):
  - groupBy: SPU
  - startDate: 2025-09-08
  - endDate: 2025-09-14
  - sid: 10730,10565,10549
  - mergeHistory: true
  - historyFile: [选择步骤1保存的文件]
```

### 4. 验证合并结果

打开合并后的Excel文件，检查：

1. **Sheet数量**：应该包含所有SPU的Sheet
2. **列标题**：第一行应该显示不同的日期范围
3. **数据对齐**：相同项目的数据应该在同一行

正确的格式示例：
```
| 项目     | 2025-09-01至2025-09-07 | 2025-09-08至2025-09-14 |
|----------|------------------------|------------------------|
| 销售额   | 10000.00              | 12000.00              |
| 成本     | 6000.00               | 7200.00               |
| 毛利     | 4000.00               | 4800.00               |
```

## 常见问题

### 问题1：合并后没有新列
**可能原因**：
- Sheet名称不匹配（检查是否有特殊字符或空格）
- 新数据为空
- 日期范围提取失败

**解决方法**：
- 查看服务器日志中的Sheet名称匹配信息
- 确认新数据查询返回了结果

### 问题2：数据错位
**可能原因**：
- 项目名称不一致（如有前后空格）
- 数据行顺序变化

**解决方法**：
- 检查ProfitReportManagementVO中的itemName是否一致
- 查看日志中的行匹配信息

### 问题3：文件无法读取
**可能原因**：
- 上传的不是Excel文件
- 文件格式损坏
- 文件不是通过系统导出的

**解决方法**：
- 确保使用系统导出的原始文件
- 检查文件扩展名是否为.xlsx

## 调试技巧

### 1. 开启详细日志
在`application.yml`中设置：
```yaml
logging:
  level:
    cn.iocoder.yudao.framework.excel: DEBUG
    cn.iocoder.yudao.module.amazon.service.profitreport: DEBUG
```

### 2. 检查中间数据
在`ExcelMergeUtils.mergeSheetDataSimple`方法中添加断点，查看：
- historyData的内容和结构
- newData的内容
- 合并后的mergedData

### 3. 验证Sheet名称
在日志中查找"处理Sheet"和"创建新Sheet"，确认Sheet名称匹配情况

## 代码位置

关键代码文件：
1. `/ProfitReportController.java` - 第166-279行（exportAggregateExcel方法）
2. `/ProfitReportServiceImpl.java` - 第1933-2014行（exportPureSpuSheetsWithMerge方法）
3. `/ExcelMergeUtils.java` - 完整的合并逻辑实现

## 联系支持

如果问题仍然存在，请提供：
1. 服务器完整日志（包含合并操作的部分）
2. 历史Excel文件样本
3. 具体的错误描述和截图