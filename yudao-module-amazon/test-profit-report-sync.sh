#!/bin/bash

# 测试利润报表同步功能的脚本

API_BASE_URL="http://localhost:48080"
ACCESS_TOKEN="YOUR_ACCESS_TOKEN_HERE"

echo "========================================"
echo "测试利润报表同步功能"
echo "========================================"

# 1. 测试增量同步 - 同步最近3天的数据
echo "1. 测试增量同步（最近3天）"
END_DATE=$(date +%Y-%m-%d)
START_DATE=$(date -d "3 days ago" +%Y-%m-%d 2>/dev/null || date -v-3d +%Y-%m-%d)

curl -X POST "${API_BASE_URL}/admin-api/amazon/profit-report/sync" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d '{
    "startDate": "'${START_DATE}'",
    "endDate": "'${END_DATE}'",
    "syncType": "INCREMENTAL",
    "sids": [101, 102],
    "asins": ["B001234567", "B002345678"]
  }' | json_pp

echo ""
echo "========================================"

# 2. 测试SKU维度聚合查询
echo "2. 测试SKU维度聚合"
curl -X POST "${API_BASE_URL}/admin-api/amazon/profit-report/aggregate/sku" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d '{
    "startDate": "'${START_DATE}'",
    "endDate": "'${END_DATE}'",
    "pageNo": 1,
    "pageSize": 10
  }' | json_pp

echo ""
echo "========================================"

# 3. 测试SPU维度聚合查询
echo "3. 测试SPU维度聚合"
curl -X POST "${API_BASE_URL}/admin-api/amazon/profit-report/aggregate/spu" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d '{
    "startDate": "'${START_DATE}'",
    "endDate": "'${END_DATE}'",
    "pageNo": 1,
    "pageSize": 10
  }' | json_pp

echo ""
echo "========================================"

# 4. 测试店铺维度聚合查询
echo "4. 测试店铺维度聚合"
curl -X POST "${API_BASE_URL}/admin-api/amazon/profit-report/aggregate/shop" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ${ACCESS_TOKEN}" \
  -d '{
    "startDate": "'${START_DATE}'",
    "endDate": "'${END_DATE}'",
    "pageNo": 1,
    "pageSize": 10
  }' | json_pp

echo ""
echo "========================================"
echo "测试完成"
echo "========================================"