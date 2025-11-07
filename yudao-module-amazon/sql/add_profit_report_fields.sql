-- =============================================
-- 利润报表新增字段脚本
-- 表名: amazon_profit_report
-- 说明: 根据业务需求新增必需字段
-- 执行时间: 2025-01-25
-- =============================================

-- 1. 收入相关字段
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS shipping_credits NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.shipping_credits IS '买家运费';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS promotional_rebates NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.promotional_rebates IS '促销折扣';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS fba_inventory_credit NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.fba_inventory_credit IS 'FBA库存赔偿';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS cash_on_delivery NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.cash_on_delivery IS 'COD货到付款';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS other_in_amount NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.other_in_amount IS '其他收入';

-- 2. 退款相关字段
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS fba_sales_refunds NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.fba_sales_refunds IS 'FBA销售退款额';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS fbm_sales_refunds NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.fbm_sales_refunds IS 'FBM销售退款额';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS total_fee_refunds NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.total_fee_refunds IS '费用退款额';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS selling_fee_refunds NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.selling_fee_refunds IS '平台费退款额';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS fba_transaction_fee_refunds NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.fba_transaction_fee_refunds IS '发货费退款额';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS other_transaction_fee_refunds NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.other_transaction_fee_refunds IS '其他订单费退款额';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS shipping_credit_refunds NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.shipping_credit_refunds IS '买家运费退款额';

-- 3. 费用相关字段（细分项）
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS fba_delivery_fee NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.fba_delivery_fee IS 'FBA发货费';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS mc_fba_delivery_fee NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.mc_fba_delivery_fee IS 'FBA发货费(多渠道)';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS other_transaction_fees NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.other_transaction_fees IS '其他订单费用';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS adjustments NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.adjustments IS '调整费用';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS total_platform_other_fee NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.total_platform_other_fee IS '平台其他费';

-- 4. 成本相关字段（单价）
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS cg_unit_price NUMERIC(18,4) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.cg_unit_price IS '采购均价';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS cg_transport_unit_costs NUMERIC(18,4) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.cg_transport_unit_costs IS '头程均价';

-- 5. 库存相关字段
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS fba_inventory_credit_quantity INTEGER DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.fba_inventory_credit_quantity IS '赔偿量';

-- 6. 站外推广费相关字段
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS custom_order_fee NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.custom_order_fee IS '订单其他费';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS custom_order_fee_principal NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.custom_order_fee_principal IS '站外推广费-本金';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS custom_order_fee_commission NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.custom_order_fee_commission IS '站外推广费-佣金';

-- 7. 额外的业务字段
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS refunds_rate NUMERIC(10,4) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.refunds_rate IS '退款率';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS fba_returns_quantity_rate NUMERIC(10,4) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.fba_returns_quantity_rate IS 'FBA退货率';

-- 8. 其他费用计算所需字段
ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS shared_fba_inbound_transportation_program_fee NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.shared_fba_inbound_transportation_program_fee IS '入库配置费（入仓手续费）';

ALTER TABLE amazon_profit_report 
ADD COLUMN IF NOT EXISTS shared_fba_integerernational_inbound_fee NUMERIC(18,2) DEFAULT 0;
COMMENT ON COLUMN amazon_profit_report.shared_fba_integerernational_inbound_fee IS 'FBA国际物流货运费';

-- =============================================
-- 执行说明：
-- 1. 请在执行前备份数据库
-- 2. 建议在测试环境先执行验证
-- 3. 所有字段都使用 IF NOT EXISTS 避免重复添加
-- 4. 默认值都设置为0，避免NULL值问题
-- =============================================