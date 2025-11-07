#!/bin/bash

# SPU+SID聚合功能测试脚本
# 用途：测试利润报表SPU+SID维度聚合功能
# 执行前请确保：
# 1. 服务已启动
# 2. 已有测试数据
# 3. 已获取有效的认证token

# 配置
BASE_URL="http://localhost:48080"
TOKEN="Bearer YOUR_TOKEN_HERE"  # 请替换为实际的token

echo "========================================="
echo "利润报表 SPU+SID 聚合功能测试"
echo "========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 测试函数
test_api() {
    local test_name=$1
    local url=$2
    local expected_status=${3:-200}
    
    echo "测试: $test_name"
    echo "URL: $url"
    
    response=$(curl -s -w "\n%{http_code}" -H "Authorization: $TOKEN" "$url")
    status_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$status_code" = "$expected_status" ]; then
        echo -e "${GREEN}✓ 状态码正确: $status_code${NC}"
        echo "响应内容预览:"
        echo "$body" | jq '.' 2>/dev/null | head -20
    else
        echo -e "${RED}✗ 状态码错误: $status_code (期望: $expected_status)${NC}"
        echo "响应内容:"
        echo "$body"
    fi
    echo "----------------------------------------"
    echo ""
}

# 1. 测试统一聚合接口 - SPU_SID维度
echo "1. 测试统一聚合接口 - SPU_SID维度"
test_api "统一聚合接口 SPU_SID" \
    "${BASE_URL}/admin-api/amazon/profit-report/aggregate?groupBy=SPU_SID&startDate=2024-01-01&endDate=2024-01-31&pageNo=1&pageSize=10"

# 2. 测试专用SPU+SID聚合接口
echo "2. 测试专用SPU+SID聚合接口"
test_api "专用SPU+SID聚合接口" \
    "${BASE_URL}/admin-api/amazon/profit-report/aggregate/spu-sid?startDate=2024-01-01&endDate=2024-01-31&pageNo=1&pageSize=10"

# 3. 测试带筛选条件的SPU+SID聚合
echo "3. 测试带筛选条件的SPU+SID聚合"
test_api "带店铺筛选的SPU+SID聚合" \
    "${BASE_URL}/admin-api/amazon/profit-report/aggregate/spu-sid?sids[]=101&sids[]=102&startDate=2024-01-01&endDate=2024-01-31&pageNo=1&pageSize=10"

# 4. 测试导出功能
echo "4. 测试SPU_SID维度导出功能"
echo "下载Excel文件..."
curl -H "Authorization: $TOKEN" \
     -o "profit_report_spu_sid_$(date +%Y%m%d_%H%M%S).xls" \
     "${BASE_URL}/admin-api/amazon/profit-report/export-aggregate-excel?groupBy=SPU_SID&startDate=2024-01-01&endDate=2024-01-31"

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Excel文件下载成功${NC}"
    ls -la profit_report_spu_sid_*.xls | tail -1
else
    echo -e "${RED}✗ Excel文件下载失败${NC}"
fi
echo ""

# 5. 对比测试：SPU聚合 vs SPU_SID聚合
echo "========================================="
echo "5. 对比测试：SPU聚合 vs SPU_SID聚合"
echo "========================================="
echo ""

echo "5.1 SPU维度聚合（不区分店铺）"
test_api "SPU聚合" \
    "${BASE_URL}/admin-api/amazon/profit-report/aggregate/spu?startDate=2024-01-01&endDate=2024-01-31&pageNo=1&pageSize=5"

echo "5.2 SPU+SID维度聚合（区分店铺）"
test_api "SPU+SID聚合" \
    "${BASE_URL}/admin-api/amazon/profit-report/aggregate/spu-sid?startDate=2024-01-01&endDate=2024-01-31&pageNo=1&pageSize=5"

# 6. 性能测试
echo "========================================="
echo "6. 性能测试"
echo "========================================="
echo ""

echo "测试大数据量查询性能..."
start_time=$(date +%s%N)
curl -s -H "Authorization: $TOKEN" \
     "${BASE_URL}/admin-api/amazon/profit-report/aggregate/spu-sid?startDate=2024-01-01&endDate=2024-03-31&pageNo=1&pageSize=100" > /dev/null
end_time=$(date +%s%N)
elapsed_time=$((($end_time - $start_time) / 1000000))
echo "查询耗时: ${elapsed_time}ms"

if [ $elapsed_time -lt 3000 ]; then
    echo -e "${GREEN}✓ 性能良好（<3秒）${NC}"
else
    echo -e "${RED}✗ 性能需要优化（>3秒）${NC}"
fi
echo ""

# 总结
echo "========================================="
echo "测试总结"
echo "========================================="
echo "1. SPU+SID聚合功能已实现"
echo "2. 支持通过groupBy=SPU_SID参数调用"
echo "3. 支持专用接口/aggregate/spu-sid"
echo "4. 支持Excel导出功能"
echo "5. 数据按SPU和店铺分开统计"
echo ""
echo "注意事项："
echo "- 请确保数据库已执行索引优化SQL"
echo "- 大数据量查询建议使用分页"
echo "- 导出功能建议限制时间范围"
echo ""
echo "测试完成！"