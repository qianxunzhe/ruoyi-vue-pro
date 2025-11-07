-- 添加小类排名相关字段，与大类排名保持一致的存储方式
-- 适用于PostgreSQL和MySQL

-- 修改原有的small_cate_rank字段为整数类型（如果存在JSON类型的旧字段）
-- 注意：如果small_cate_rank字段已经是JSONB类型，需要先重命名
DO $$ 
BEGIN
    -- 检查small_cate_rank字段是否存在且为JSONB类型
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'amazon_product_performance' 
        AND column_name = 'small_cate_rank'
        AND data_type = 'jsonb'
    ) THEN
        -- 重命名旧的JSON字段为small_cate_rank_detail
        ALTER TABLE amazon_product_performance 
        RENAME COLUMN small_cate_rank TO small_cate_rank_detail;
    END IF;
END $$;

-- 添加小类排名字段（第一个小类的排名）
ALTER TABLE amazon_product_performance 
ADD COLUMN IF NOT EXISTS small_cate_rank INT DEFAULT NULL COMMENT '小类排名';

-- 添加上一次小类排名字段
ALTER TABLE amazon_product_performance 
ADD COLUMN IF NOT EXISTS prev_small_cate_rank INT DEFAULT NULL COMMENT '上一次小类排名';

-- 添加小类排名分类字段
ALTER TABLE amazon_product_performance 
ADD COLUMN IF NOT EXISTS small_rank_category VARCHAR(500) DEFAULT NULL COMMENT '小类排名分类';

-- 添加小类排名详细信息字段（JSON格式，包含所有小类的排名）
ALTER TABLE amazon_product_performance 
ADD COLUMN IF NOT EXISTS small_cate_rank_detail JSONB DEFAULT NULL COMMENT '小类排名详细信息';

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS idx_product_performance_small_cate_rank 
ON amazon_product_performance(small_cate_rank);

CREATE INDEX IF NOT EXISTS idx_product_performance_small_rank_category 
ON amazon_product_performance(small_rank_category);

-- MySQL版本（如果使用MySQL，请使用以下语句）
-- ALTER TABLE amazon_product_performance 
--   MODIFY COLUMN small_cate_rank INT DEFAULT NULL COMMENT '小类排名',
--   ADD COLUMN prev_small_cate_rank INT DEFAULT NULL COMMENT '上一次小类排名',
--   ADD COLUMN small_rank_category VARCHAR(500) DEFAULT NULL COMMENT '小类排名分类',
--   ADD COLUMN small_cate_rank_detail JSON DEFAULT NULL COMMENT '小类排名详细信息';

-- 数据迁移（如果有旧数据需要迁移）
-- 从JSON格式的small_cate_rank_detail中提取第一个小类的数据
-- UPDATE amazon_product_performance 
-- SET small_cate_rank = (small_cate_rank_detail->0->>'rank')::INT,
--     prev_small_cate_rank = (small_cate_rank_detail->0->>'prevRank')::INT,
--     small_rank_category = small_cate_rank_detail->0->>'category'
-- WHERE small_cate_rank_detail IS NOT NULL 
-- AND jsonb_array_length(small_cate_rank_detail) > 0;