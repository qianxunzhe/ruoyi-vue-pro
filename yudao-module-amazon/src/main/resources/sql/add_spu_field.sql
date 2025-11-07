-- 添加SPU字段到产品表现表
ALTER TABLE `amazon_product_performance` 
ADD COLUMN `spu` varchar(100) DEFAULT NULL COMMENT 'SPU（产品编码）' AFTER `sku`;

-- PostgreSQL版本
-- ALTER TABLE amazon_product_performance 
-- ADD COLUMN spu varchar(100) DEFAULT NULL;
-- COMMENT ON COLUMN amazon_product_performance.spu IS 'SPU（产品编码）';