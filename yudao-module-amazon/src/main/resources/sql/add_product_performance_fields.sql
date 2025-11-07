-- 添加产品表现表新字段
-- 适用于PostgreSQL和MySQL

-- 添加181-270天库龄字段
ALTER TABLE amazon_product_performance ADD COLUMN IF NOT EXISTS inv_age_181_to_270_days INT DEFAULT 0 COMMENT '181-270天库龄';

-- 添加271-365天库龄字段
ALTER TABLE amazon_product_performance ADD COLUMN IF NOT EXISTS inv_age_271_to_365_days INT DEFAULT 0 COMMENT '271-365天库龄';

-- 添加365+天库龄字段
ALTER TABLE amazon_product_performance ADD COLUMN IF NOT EXISTS inv_age_365_plus_days INT DEFAULT 0 COMMENT '365+天库龄';

-- 添加记录日期字段
ALTER TABLE amazon_product_performance ADD COLUMN IF NOT EXISTS record_date DATE COMMENT '记录日期';

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_product_performance_record_date ON amazon_product_performance(record_date);
CREATE INDEX IF NOT EXISTS idx_product_performance_sid_record_date ON amazon_product_performance(sid, record_date);
CREATE INDEX IF NOT EXISTS idx_product_performance_asin_sid_record ON amazon_product_performance(asin, sid, record_date);

-- MySQL版本（如果使用MySQL，请使用以下语句）
-- ALTER TABLE amazon_product_performance 
--   ADD COLUMN inv_age_181_to_270_days INT DEFAULT 0 COMMENT '181-270天库龄',
--   ADD COLUMN inv_age_271_to_365_days INT DEFAULT 0 COMMENT '271-365天库龄',
--   ADD COLUMN inv_age_365_plus_days INT DEFAULT 0 COMMENT '365+天库龄',
--   ADD COLUMN record_date DATE COMMENT '记录日期';