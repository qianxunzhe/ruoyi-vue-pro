-- 修复deleted字段类型问题
-- 将BOOLEAN类型改为SMALLINT，以兼容MyBatis Plus的逻辑删除功能

-- 1. 首先添加临时列
ALTER TABLE amazon_product_performance ADD COLUMN deleted_new SMALLINT;

-- 2. 将现有数据转换
UPDATE amazon_product_performance 
SET deleted_new = CASE WHEN deleted = true THEN 1 ELSE 0 END;

-- 3. 删除旧列
ALTER TABLE amazon_product_performance DROP COLUMN deleted;

-- 4. 重命名新列
ALTER TABLE amazon_product_performance RENAME COLUMN deleted_new TO deleted;

-- 5. 设置默认值和非空约束
ALTER TABLE amazon_product_performance 
ALTER COLUMN deleted SET NOT NULL,
ALTER COLUMN deleted SET DEFAULT 0;

-- 6. 添加注释
COMMENT ON COLUMN amazon_product_performance.deleted IS '是否删除 0-未删除 1-已删除';