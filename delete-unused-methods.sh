#!/bin/bash

# 删除未使用的方法的脚本
FILE="/Users/chengxiao/code/java/ruoyi-vue-pro/yudao-module-amazon/src/main/java/cn/iocoder/yudao/module/amazon/service/profitreport/ProfitReportServiceImpl.java"

# 备份原文件
cp "$FILE" "$FILE.bak"

# 删除buildDateAggVO方法（1110-1214行）
sed -i '' '1110,1214d' "$FILE"

# 删除buildAsinAggVO方法（996-1108行）- 注意行号已经改变
sed -i '' '996,1108d' "$FILE"

# 删除buildSkuAggVO方法（513-577行）- 注意行号已经改变
sed -i '' '513,577d' "$FILE"

echo "删除完成"