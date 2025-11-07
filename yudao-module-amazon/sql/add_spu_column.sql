-- 为产品表现表添加SPU字段
-- PostgreSQL版本
ALTER TABLE amazon_product_performance ADD COLUMN IF NOT EXISTS spu VARCHAR(100) COMMENT 'SPU编码';

-- MySQL版本
-- ALTER TABLE amazon_product_performance ADD COLUMN spu VARCHAR(100) COMMENT 'SPU编码' AFTER sku;

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_product_performance_spu ON amazon_product_performance(spu);