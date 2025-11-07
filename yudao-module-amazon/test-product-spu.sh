#!/bin/bash

# 测试产品表现同步-SPU匹配功能
# 使用sync-all接口同步所有产品，验证SPU字段是否正确填充

API_BASE="http://localhost:48080/admin-api"
TOKEN="Bearer test"  # 需要替换为实际的token

echo "测试产品表现同步SPU匹配功能"
echo "================================"

# 1. 调用sync-all接口同步所有产品数据
echo "1. 同步所有产品数据（包含SPU匹配）..."
curl -X POST "${API_BASE}/amazon/product-performance/sync-all" \
  -H "Content-Type: application/json" \
  -H "Authorization: ${TOKEN}" \
  -H "tenant-id: 1" \
  -d '{
    "sids": [1801559726194946],
    "startDate": "2024-01-01",
    "endDate": "2024-01-01",
    "summaryField": "asin"
  }' | jq '.'

echo ""
echo "2. 查询产品表现数据，检查SPU字段..."
# 查询数据验证SPU字段是否已填充
curl -X GET "${API_BASE}/amazon/product-performance/page?pageNo=1&pageSize=10&startDate=2024-01-01&endDate=2024-01-01" \
  -H "Authorization: ${TOKEN}" \
  -H "tenant-id: 1" | jq '.data.list[] | {asin: .asin, sku: .sku, spu: .spu, itemName: .itemName}'

echo ""
echo "测试完成！"
echo "请检查上述输出中的SPU字段是否已正确填充。"