#!/bin/bash

# 快速测试合并功能

echo "Excel合并功能快速测试"
echo "====================="
echo ""

# 提示用户输入token
read -p "请输入访问Token: " TOKEN

if [ -z "$TOKEN" ]; then
    echo "错误：Token不能为空"
    exit 1
fi

# 配置
BASE_URL="http://localhost:48080/admin-api"
ENDPOINT="/amazon/profit-report/export-aggregate-excel"

echo ""
echo "第1步：导出初始数据（2025-09-01至2025-09-07）"
curl -X POST "${BASE_URL}${ENDPOINT}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -F "groupBy=SPU" \
  -F "startDate=2025-09-01" \
  -F "endDate=2025-09-07" \
  -F "sid=10730" \
  -F "mergeHistory=false" \
  -o "step1_initial.xlsx" \
  -s

if [ -f "step1_initial.xlsx" ]; then
    echo "✓ 初始数据导出成功: step1_initial.xlsx ($(ls -lh step1_initial.xlsx | awk '{print $5}'))"
else
    echo "✗ 导出失败"
    exit 1
fi

echo ""
echo "第2步：合并新数据（2025-09-08至2025-09-14）"
curl -X POST "${BASE_URL}${ENDPOINT}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -F "groupBy=SPU" \
  -F "startDate=2025-09-08" \
  -F "endDate=2025-09-14" \
  -F "sid=10730" \
  -F "mergeHistory=true" \
  -F "historyFile=@step1_initial.xlsx" \
  -o "step2_merged.xlsx" \
  -s

if [ -f "step2_merged.xlsx" ]; then
    echo "✓ 合并成功: step2_merged.xlsx ($(ls -lh step2_merged.xlsx | awk '{print $5}'))"
    echo ""
    echo "=============================="
    echo "测试完成！"
    echo "=============================="
    echo ""
    echo "请打开以下文件检查结果："
    echo "1. step1_initial.xlsx - 应包含第一个时间段的数据"
    echo "2. step2_merged.xlsx - 应包含两个时间段的数据（两列）"
    echo ""
    echo "验证要点："
    echo "- 每个SPU Sheet应有两列数据"
    echo "- 列标题分别为两个日期范围"
    echo "- 数据应正确对齐"
else
    echo "✗ 合并失败"
    echo "请检查服务器日志获取详细错误信息"
fi