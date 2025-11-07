-- 为SPU+SID聚合查询添加优化索引
-- 执行时间：2024-08-26
-- 功能说明：优化利润报表SPU+SID维度聚合查询性能

-- 1. 创建SPU+SID复合索引
-- 该索引用于优化按SPU和SID组合进行聚合查询的性能
CREATE INDEX IF NOT EXISTS idx_profit_report_spu_sid 
ON amazon_profit_report(spu, sid) 
WHERE deleted = FALSE;

-- 2. 创建覆盖索引（包含常用查询字段）
-- 该索引可以减少回表查询，提高查询效率
CREATE INDEX IF NOT EXISTS idx_profit_report_spu_sid_date 
ON amazon_profit_report(spu, sid, sync_date) 
WHERE deleted = FALSE;

-- 3. 创建SPU+SID+店铺名称的复合索引
-- 用于同时需要按店铺名称筛选的场景
CREATE INDEX IF NOT EXISTS idx_profit_report_spu_sid_store 
ON amazon_profit_report(spu, sid, store_name) 
WHERE deleted = FALSE;

-- 4. 分析表统计信息（PostgreSQL专用）
-- 确保查询优化器有最新的统计信息
ANALYZE amazon_profit_report;

-- 索引使用说明：
-- 1. idx_profit_report_spu_sid：主要用于SPU+SID维度聚合查询
-- 2. idx_profit_report_spu_sid_date：用于带日期范围的SPU+SID聚合查询
-- 3. idx_profit_report_spu_sid_store：用于需要店铺名称的SPU+SID查询

-- 查询索引使用情况（可选）
-- SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
-- FROM pg_stat_user_indexes
-- WHERE tablename = 'amazon_profit_report'
-- ORDER BY idx_scan DESC;

-- 注意事项：
-- 1. 在生产环境执行前，请先在测试环境验证
-- 2. 创建索引可能会暂时锁表，建议在低峰期执行
-- 3. 索引会占用额外的存储空间，请确保有足够的磁盘空间
-- 4. 定期使用REINDEX命令重建索引以保持性能