#!/bin/bash

# 利润报表按店铺依次同步测试脚本

echo "========================"
echo "利润报表按店铺同步测试"
echo "========================"

# API基础配置
BASE_URL="http://localhost:48080"
API_PATH="/admin-api/amazon/profit-report/sync"

# 获取当前日期
CURRENT_DATE=$(date +%Y-%m-%d)
# 获取前一天日期
YESTERDAY=$(date -v-1d +%Y-%m-%d 2>/dev/null || date -d "yesterday" +%Y-%m-%d)

echo "同步日期范围：$YESTERDAY 至 $CURRENT_DATE"
echo ""

# 测试1：指定单个店铺同步
echo "测试1：指定单个店铺同步"
echo "----------------------------------------"
curl -X POST "${BASE_URL}${API_PATH}" \
  -H "Content-Type: application/json" \
  -H "tenant-id: 1" \
  -H "Authorization: Bearer test" \
  -d '{
    "startDate": "'$YESTERDAY'",
    "endDate": "'$CURRENT_DATE'",
    "sids": [101],
    "syncType": "FULL"
  }'
echo -e "\n"

# 等待2秒
sleep 2

# 测试2：指定多个店铺同步（将按店铺ID依次同步）
echo "测试2：指定多个店铺同步（将按店铺ID依次同步）"
echo "----------------------------------------"
curl -X POST "${BASE_URL}${API_PATH}" \
  -H "Content-Type: application/json" \
  -H "tenant-id: 1" \
  -H "Authorization: Bearer test" \
  -d '{
    "startDate": "'$YESTERDAY'",
    "endDate": "'$CURRENT_DATE'",
    "sids": [101, 102, 103],
    "syncType": "FULL"
  }'
echo -e "\n"

# 等待2秒
sleep 2

# 测试3：不指定店铺，同步所有活跃店铺（将自动获取并按店铺ID依次同步）
echo "测试3：不指定店铺，同步所有活跃店铺"
echo "----------------------------------------"
curl -X POST "${BASE_URL}${API_PATH}" \
  -H "Content-Type: application/json" \
  -H "tenant-id: 1" \
  -H "Authorization: Bearer test" \
  -d '{
    "startDate": "'$YESTERDAY'",
    "endDate": "'$CURRENT_DATE'",
    "syncType": "INCREMENTAL"
  }'
echo -e "\n"

echo "========================"
echo "测试完成"
echo "========================"
echo ""
echo "注意事项："
echo "1. 现在同步会按照店铺ID依次进行"
echo "2. 每个店铺单独同步，避免数据混乱"
echo "3. 如果不指定店铺，会自动获取所有活跃店铺并依次同步"
echo "4. 查看服务器日志可以看到详细的按店铺同步过程"