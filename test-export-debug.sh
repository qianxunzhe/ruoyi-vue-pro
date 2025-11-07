#!/bin/bash

# 测试导出功能的脚本

echo "测试利润报表导出功能..."

# 1. 测试按SPU聚合查询
echo "1. 测试按SPU聚合查询:"
curl -X GET "http://localhost:48080/admin-api/amazon/profit-report/aggregate/spu?startDate=2025-08-18&endDate=2025-08-24" \
  -H "Authorization: Bearer test" | jq '.'

echo ""
echo "2. 测试按SPU+SID聚合查询:"
curl -X GET "http://localhost:48080/admin-api/amazon/profit-report/aggregate/spu-sid?startDate=2025-08-18&endDate=2025-08-24" \
  -H "Authorization: Bearer test" | jq '.'

echo ""
echo "3. 导出测试（多Sheet模式）:"
echo "URL: http://localhost:48080/admin-api/amazon/profit-report/export-aggregate-excel?exportFormat=STANDARD&groupBy=SPU&startDate=2025-08-18&endDate=2025-08-24&multiSheet=true"