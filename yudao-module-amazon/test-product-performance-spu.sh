#!/bin/bash

# 产品表现SPU字段同步测试脚本
# 使用前请确保：
# 1. 数据库已执行 add_spu_field.sql 脚本
# 2. 系统已启动并可以访问

# API基础URL（请根据实际情况修改）
BASE_URL="http://localhost:48080"
# 访问令牌（请替换为实际的token）
AUTH_TOKEN="Bearer your-access-token-here"

echo "====================================="
echo "产品表现SPU字段同步测试"
echo "====================================="

# Step 1: 初始化SKU-SPU缓存
echo ""
echo "Step 1: 初始化SKU-SPU缓存..."
curl -X POST "$BASE_URL/amazon/product-performance/init-sku-spu-cache" \
  -H "Authorization: $AUTH_TOKEN" \
  -H "Content-Type: application/json" \
  --silent | jq .

echo ""
echo "等待5秒让缓存初始化完成..."
sleep 5

# Step 2: 同步产品表现数据（测试小批量）
echo ""
echo "Step 2: 同步产品表现数据..."
curl -X POST "$BASE_URL/amazon/product-performance/sync" \
  -H "Authorization: $AUTH_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sids": [109],
    "startDate": "2024-12-01",
    "endDate": "2024-12-01",
    "summaryField": "asin"
  }' \
  --silent | jq .

echo ""
echo "====================================="
echo "测试完成！"
echo "====================================="
echo ""
echo "请检查数据库中的SPU字段是否已填充："
echo "SELECT asin, sku, spu, item_name FROM amazon_product_performance WHERE spu IS NOT NULL LIMIT 10;"
echo ""