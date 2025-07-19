-- 关键词排名表性能优化索引
-- 请在数据库中执行这些索引创建语句以提升查询性能

-- 1. 主要查询条件的复合索引 - 以taskId为核心
CREATE INDEX IF NOT EXISTS idx_amazon_keyword_ranking_main 
ON amazon_keyword_ranking (task_id, city, crawl_date, deleted);

-- 1.1. 包含rankType的索引 - 当指定rankType时使用
CREATE INDEX IF NOT EXISTS idx_amazon_keyword_ranking_main_with_rank_type 
ON amazon_keyword_ranking (task_id, city, rank_type, crawl_date, deleted);

-- 2. 关键词查询索引 - 基于taskId
CREATE INDEX IF NOT EXISTS idx_amazon_keyword_ranking_keyword 
ON amazon_keyword_ranking (task_id, keyword, crawl_date);

-- 3. 位置查询索引 - 基于taskId  
CREATE INDEX IF NOT EXISTS idx_amazon_keyword_ranking_position 
ON amazon_keyword_ranking (task_id, page_number, position_in_page, crawl_date);

-- 4. 时间排序索引 - 基于taskId
CREATE INDEX IF NOT EXISTS idx_amazon_keyword_ranking_time 
ON amazon_keyword_ranking (task_id, crawl_date, crawl_time);

-- 5. ASIN查询索引 - 基于taskId
CREATE INDEX IF NOT EXISTS idx_amazon_keyword_ranking_asin 
ON amazon_keyword_ranking (task_id, asin, keyword, crawl_date);

-- 6. 覆盖索引 - 包含查询所需的所有字段（无rankType）
CREATE INDEX IF NOT EXISTS idx_amazon_keyword_ranking_covering 
ON amazon_keyword_ranking (task_id, city, crawl_date, deleted) 
INCLUDE (keyword, asin, page_number, position_in_page, crawl_time, rank_type);

-- 6.1. 覆盖索引 - 包含rankType的版本
CREATE INDEX IF NOT EXISTS idx_amazon_keyword_ranking_covering_with_rank_type 
ON amazon_keyword_ranking (task_id, city, rank_type, crawl_date, deleted) 
INCLUDE (keyword, asin, page_number, position_in_page, crawl_time);

-- 查看表的索引状态
-- SELECT indexname, indexdef FROM pg_indexes WHERE tablename = 'amazon_keyword_ranking';