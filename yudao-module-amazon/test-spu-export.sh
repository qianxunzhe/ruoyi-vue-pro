#!/bin/bash

# 测试SPU维度导出功能

# 设置基础URL
BASE_URL="http://localhost:48080"

# 设置认证Token（需要根据实际情况修改）
TOKEN="Bearer test1"

# 测试日期（使用当前日期往前推）
END_DATE=$(date -v -1d '+%Y-%m-%d')  # 昨天
START_DATE=$(date -v -7d '+%Y-%m-%d')  # 7天前

echo "=== 测试SPU维度导出 ==="
echo "日期范围: ${START_DATE} 至 ${END_DATE}"
echo ""

# 1. 先测试是否有数据
echo "1. 检查是否有数据..."
curl -X GET "${BASE_URL}/admin-api/amazon/profit-report/aggregate/spu-sid?startDate=${START_DATE}&endDate=${END_DATE}&pageSize=10" \
  -H "Authorization: ${TOKEN}" \
  -H "tenant-id: 1" | jq '.'

echo ""
echo "2. 导出SPU维度Excel..."
# 导出Excel文件
curl -X GET "${BASE_URL}/admin-api/amazon/profit-report/export-aggregate-excel?groupBy=SPU&startDate=${START_DATE}&endDate=${END_DATE}" \
  -H "Authorization: ${TOKEN}" \
  -H "tenant-id: 1" \
  -o "profit_report_spu_$(date +%Y%m%d_%H%M%S).xlsx" \
  -v

echo ""
echo "3. 检查导出的文件..."
ls -la profit_report_spu_*.xlsx

echo ""
echo "4. 使用file命令检查文件类型..."
file profit_report_spu_*.xlsx

echo ""
echo "完成！请检查生成的文件。"