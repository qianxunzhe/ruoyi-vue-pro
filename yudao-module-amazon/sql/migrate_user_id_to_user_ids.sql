-- ========================================
-- 店铺用户ID迁移脚本
-- 功能：将amazon_shops表的user_id迁移到user_ids（JSONB数组）
-- 执行前请先备份数据！
-- ========================================

-- 步骤1：检查迁移前数据状态
SELECT
    COUNT(*) as total_shops,
    COUNT(user_id) as shops_with_user_id,
    COUNT(user_ids) as shops_with_user_ids
FROM amazon_shops
WHERE deleted = FALSE;

-- 步骤2：将user_id迁移到user_ids
-- 只迁移user_id不为NULL且不为0的记录
UPDATE amazon_shops
SET user_ids = jsonb_build_array(user_id)
WHERE user_id IS NOT NULL
  AND user_id != 0
  AND (user_ids IS NULL OR user_ids = '[]'::jsonb)
  AND deleted = FALSE;

-- 步骤3：验证迁移结果
-- 检查迁移后的数据
SELECT
    sid,
    name,
    user_id as old_user_id,
    user_ids as new_user_ids
FROM amazon_shops
WHERE deleted = FALSE
  AND user_id IS NOT NULL
  AND user_id != 0
ORDER BY sid
LIMIT 20;

-- 步骤4：统计迁移结果
SELECT
    COUNT(*) as total_migrated
FROM amazon_shops
WHERE deleted = FALSE
  AND user_id IS NOT NULL
  AND user_id != 0
  AND user_ids IS NOT NULL
  AND user_ids != '[]'::jsonb;

-- 步骤5（可选）：查询JSONB中包含特定用户的店铺
-- 示例：查询包含用户ID为1的所有店铺
-- SELECT * FROM amazon_shops WHERE user_ids @> '[1]'::jsonb;

-- 步骤6（可选）：如果需要回滚，可以清空user_ids
-- 注意：执行前请确认！
-- UPDATE amazon_shops SET user_ids = '[]'::jsonb WHERE deleted = FALSE;
