-- =============================================
-- 利润报表新增缺失的收入字段脚本
-- 表名: amazon_profit_report
-- 说明: 添加包装收入、买家交易保障索赔额、积分抵减收入三个字段
-- 执行时间: 2025-01-28
-- =============================================

-- 1. 包装收入
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS gift_wrap_credits NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.gift_wrap_credits IS '包装收入';

-- 2. 买家交易保障索赔额
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS guarantee_claims NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.guarantee_claims IS '买家交易保障索赔额';

-- 3. 积分抵减收入
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS cost_of_points_granted NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.cost_of_points_granted IS '积分抵减收入';

-- =============================================
-- 执行说明：
-- 1. 请在执行前备份数据库
-- 2. 建议在测试环境先执行验证
-- 3. 所有字段都使用 IF NOT EXISTS 避免重复添加
-- 4. 默认值都设置为0，避免NULL值问题
-- 5. 这三个字段来源于领星API：
--    - giftWrapCredits -> gift_wrap_credits
--    - guaranteeClaims -> guarantee_claims  
--    - costOfPointsGranted -> cost_of_points_granted
-- =============================================