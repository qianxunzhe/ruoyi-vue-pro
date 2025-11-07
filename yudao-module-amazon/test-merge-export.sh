#!/bin/bash

# 利润报表合并导出功能测试脚本

# 配置
BASE_URL="http://localhost:48080/admin-api"
ENDPOINT="/amazon/profit-report/export-aggregate-excel"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================"
echo "利润报表合并导出功能测试"
echo "========================================"

# 检查是否提供了token
if [ -z "$1" ]; then
    echo -e "${RED}错误：请提供访问token${NC}"
    echo "用法: ./test-merge-export.sh <token>"
    exit 1
fi

TOKEN=$1

# 测试1：不合并历史文件（普通导出）
echo ""
echo -e "${YELLOW}测试1：普通导出（不合并历史文件）${NC}"
echo "----------------------------------------"

curl -X POST "${BASE_URL}${ENDPOINT}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -F "groupBy=SPU" \
  -F "startDate=2025-09-01" \
  -F "endDate=2025-09-07" \
  -F "sid=10730,10565,10549" \
  -F "mergeHistory=false" \
  -o "test_export_normal.xlsx"

if [ -f "test_export_normal.xlsx" ]; then
    echo -e "${GREEN}✓ 普通导出成功，文件已保存为: test_export_normal.xlsx${NC}"
    echo "  文件大小: $(ls -lh test_export_normal.xlsx | awk '{print $5}')"
else
    echo -e "${RED}✗ 普通导出失败${NC}"
fi

# 测试2：合并历史文件
echo ""
echo -e "${YELLOW}测试2：合并历史文件导出${NC}"
echo "----------------------------------------"

if [ -f "test_export_normal.xlsx" ]; then
    echo "使用刚才导出的文件作为历史文件..."
    
    curl -X POST "${BASE_URL}${ENDPOINT}" \
      -H "Authorization: Bearer ${TOKEN}" \
      -F "groupBy=SPU" \
      -F "startDate=2025-09-08" \
      -F "endDate=2025-09-14" \
      -F "sid=10730,10565,10549" \
      -F "mergeHistory=true" \
      -F "historyFile=@test_export_normal.xlsx" \
      -o "test_export_merged.xlsx"
    
    if [ -f "test_export_merged.xlsx" ]; then
        echo -e "${GREEN}✓ 合并导出成功，文件已保存为: test_export_merged.xlsx${NC}"
        echo "  文件大小: $(ls -lh test_export_merged.xlsx | awk '{print $5}')"
        echo ""
        echo "请打开 test_export_merged.xlsx 检查："
        echo "  1. 每个SPU Sheet是否包含两列数据"
        echo "  2. 第一列：2025-09-01至2025-09-07"
        echo "  3. 第二列：2025-09-08至2025-09-14"
    else
        echo -e "${RED}✗ 合并导出失败${NC}"
    fi
else
    echo -e "${RED}✗ 无法进行合并测试，因为第一步导出失败${NC}"
fi

# 测试3：多次合并
echo ""
echo -e "${YELLOW}测试3：多次合并测试${NC}"
echo "----------------------------------------"

if [ -f "test_export_merged.xlsx" ]; then
    echo "使用第二次导出的文件进行第三次合并..."
    
    curl -X POST "${BASE_URL}${ENDPOINT}" \
      -H "Authorization: Bearer ${TOKEN}" \
      -F "groupBy=SPU" \
      -F "startDate=2025-09-15" \
      -F "endDate=2025-09-21" \
      -F "sid=10730,10565,10549" \
      -F "mergeHistory=true" \
      -F "historyFile=@test_export_merged.xlsx" \
      -o "test_export_triple_merged.xlsx"
    
    if [ -f "test_export_triple_merged.xlsx" ]; then
        echo -e "${GREEN}✓ 第三次合并成功，文件已保存为: test_export_triple_merged.xlsx${NC}"
        echo "  文件大小: $(ls -lh test_export_triple_merged.xlsx | awk '{print $5}')"
        echo ""
        echo "请打开 test_export_triple_merged.xlsx 检查："
        echo "  1. 每个SPU Sheet是否包含三列数据"
        echo "  2. 第一列：2025-09-01至2025-09-07"
        echo "  3. 第二列：2025-09-08至2025-09-14"
        echo "  4. 第三列：2025-09-15至2025-09-21"
    else
        echo -e "${RED}✗ 第三次合并失败${NC}"
    fi
fi

echo ""
echo "========================================"
echo "测试完成"
echo "========================================"
echo ""
echo "生成的测试文件："
ls -la test_export_*.xlsx 2>/dev/null || echo "没有生成测试文件"

echo ""
echo "提示："
echo "1. 请检查服务器日志，查看合并过程的详细信息"
echo "2. 使用Excel打开生成的文件，验证数据是否正确合并"
echo "3. 特别注意检查每个Sheet中的列标题和数据对应关系"