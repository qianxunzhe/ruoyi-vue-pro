# SPU导出按字母排序功能说明

## 功能描述
导出Excel时，所有SPU Sheet将按照SPU名称的字母顺序（A-Z）进行排序。

## 实现范围
已在以下三个导出方法中实现了SPU排序功能：

### 1. exportPureSpuSheets
- **路径**: `ProfitReportServiceImpl.java` 第1885-1891行
- **说明**: 纯SPU维度导出，所有SPU放在一个Excel的不同sheet中
- **排序方式**: 使用`List.sort()`对SPU列表进行A-Z排序

### 2. exportPureSpuSheetsWithMerge  
- **路径**: `ProfitReportServiceImpl.java` 第1959-1965行
- **说明**: 带历史文件合并的SPU导出
- **排序方式**: 使用`List.sort()`对SPU列表进行A-Z排序

### 3. exportSpuSheetsByShop
- **路径**: `ProfitReportServiceImpl.java` 第1795-1817行
- **说明**: 按店铺分文件，每个文件中按SPU分Sheet导出
- **排序方式**: 使用`TreeMap`自动排序，"其他"分类固定放在最后

## 排序规则
- 按SPU名称的字母顺序升序排列（A在前，Z在后）
- 忽略大小写差异（使用`compareToIgnoreCase`）
- 空值或null值排在最前
- 特殊分类"其他"始终排在最后（仅在exportSpuSheetsByShop中）

## 示例效果

### 排序前
```
Sheet顺序: MMC01, ABC01, ZZZ99, BBB02
```

### 排序后  
```
Sheet顺序: ABC01, BBB02, MMC01, ZZZ99
```

## 日志输出
每个方法在排序完成后都会输出日志：
```
SPU已按A-Z顺序排序
```

对于exportSpuSheetsByShop，还会输出：
```
店铺{sid}的SPU已按A-Z顺序排序，共{count}个SPU
```

## 性能影响
- 排序操作时间复杂度：O(n log n)
- 对于常见的SPU数量（<1000个），性能影响可忽略不计
- 使用TreeMap的方式在分组时直接排序，避免额外的排序步骤

## 注意事项
1. 排序仅影响Sheet的顺序，不影响数据内容
2. 合并历史文件时，新数据也会按排序后的顺序处理
3. Sheet名称超过31字符会被截断，但排序基于完整的SPU名称

## 测试验证
导出Excel后，打开文件检查Sheet标签的顺序是否按字母排序：
1. 第一个Sheet应该是字母顺序最前的SPU
2. 最后一个Sheet应该是字母顺序最后的SPU（或"其他"）
3. 中间的Sheet应该严格按字母顺序排列