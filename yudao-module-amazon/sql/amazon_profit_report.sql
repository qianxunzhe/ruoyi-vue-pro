-- 亚马逊利润报表数据表
CREATE TABLE amazon_profit_report (
    id BIGSERIAL PRIMARY KEY,
    
    -- ============ 业务主键字段 ============
    sync_start_date DATE NOT NULL,                    -- 同步查询的开始日期
    sync_end_date DATE NOT NULL,                      -- 同步查询的结束日期
    
    -- ============ 核心业务字段 ============
    asin VARCHAR(50) NOT NULL,                        -- 亚马逊标准识别号
    parent_asin VARCHAR(50),                          -- 父ASIN（变体组）
    local_sku VARCHAR(100),                           -- 本地SKU编码
    spu VARCHAR(100),                                 -- 标准产品单位（预留字段）
    sid VARCHAR(50),                                  -- 店铺ID
    store_name VARCHAR(200),                          -- 店铺名称
    country_code VARCHAR(10),                         -- 国家代码
    
    -- ============ 销售数量相关 ============
    total_sales_quantity INTEGER DEFAULT 0,           -- 总销售数量
    fba_sales_quantity INTEGER DEFAULT 0,             -- FBA销售数量（亚马逊配送）
    fbm_sales_quantity INTEGER DEFAULT 0,             -- FBM销售数量（卖家自配送）
    refunds_quantity INTEGER DEFAULT 0,               -- 退款数量
    fba_returns_quantity INTEGER DEFAULT 0,           -- FBA退货数量
    
    -- ============ 销售金额相关（单位：货币） ============
    total_sales_amount DECIMAL(18,2) DEFAULT 0,       -- 总销售金额
    fba_sale_amount DECIMAL(18,2) DEFAULT 0,          -- FBA销售金额
    fbm_sale_amount DECIMAL(18,2) DEFAULT 0,          -- FBM销售金额
    total_sales_refunds DECIMAL(18,2) DEFAULT 0,      -- 总退款金额
    
    -- ============ 广告相关 ============
    total_ads_sales DECIMAL(18,2) DEFAULT 0,          -- 广告总销售额
    total_ads_cost DECIMAL(18,2) DEFAULT 0,           -- 广告总花费
    ads_sp_sales DECIMAL(18,2) DEFAULT 0,             -- SP广告销售额（Sponsored Products）
    ads_sp_cost DECIMAL(18,2) DEFAULT 0,              -- SP广告花费
    ads_sd_sales DECIMAL(18,2) DEFAULT 0,             -- SD广告销售额（Sponsored Display）
    ads_sd_cost DECIMAL(18,2) DEFAULT 0,              -- SD广告花费
    ads_sb_sales DECIMAL(18,2) DEFAULT 0,             -- SB广告销售额（Sponsored Brands）
    ads_sb_cost DECIMAL(18,2) DEFAULT 0,              -- SB广告花费
    
    -- ============ 费用相关 ============
    platform_fee DECIMAL(18,2) DEFAULT 0,             -- 平台费用（佣金）
    total_fba_delivery_fee DECIMAL(18,2) DEFAULT 0,   -- FBA总配送费用
    total_storage_fee DECIMAL(18,2) DEFAULT 0,        -- 总仓储费用
    promotion_fee DECIMAL(18,2) DEFAULT 0,            -- 推广费用
    
    -- ============ 成本相关 ============
    cg_price_total DECIMAL(18,2) DEFAULT 0,           -- 采购成本总额
    cg_transport_costs_total DECIMAL(18,2) DEFAULT 0, -- 头程运输成本总额
    cg_other_costs_total DECIMAL(18,2) DEFAULT 0,     -- 其他成本总额
    total_cost DECIMAL(18,2) DEFAULT 0,               -- 合计成本
    
    -- ============ 利润相关 ============
    gross_profit DECIMAL(18,2) DEFAULT 0,             -- 毛利润
    gross_rate DECIMAL(10,4) DEFAULT 0,               -- 毛利率（百分比）
    
    -- ============ 其他业务字段 ============
    currency_code VARCHAR(10),                        -- 货币代码（USD、EUR、GBP等）
    transaction_status VARCHAR(50),                   -- 交易状态
    local_name VARCHAR(500),                          -- 产品本地名称/品名

    -- ============ 扩展字段 ============
    other_fee_str JSONB,                              -- 其他费用明细（JSON格式存储）
    
    -- ============ 系统字段 ============
    sync_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    -- 数据同步时间
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 记录创建时间
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 记录更新时间
    creator VARCHAR(64),                              -- 创建人
    updater VARCHAR(64),                              -- 更新人
    deleted INT2 NOT NULL DEFAULT 0,                  -- 逻辑删除标记（0:未删除 1:已删除）
    tenant_id INT8 NOT NULL DEFAULT 0,                -- 租户ID（多租户隔离）
    
    -- 联合唯一键：防止重复数据
    CONSTRAINT uk_sync_asin UNIQUE(sync_start_date, sync_end_date, asin)
);

-- ============ 创建索引优化查询性能 ============
COMMENT ON TABLE amazon_profit_report IS '亚马逊利润报表数据表';

-- 业务索引
CREATE INDEX idx_amazon_profit_report_sync_range ON amazon_profit_report(sync_start_date, sync_end_date);
CREATE INDEX idx_amazon_profit_report_asin ON amazon_profit_report(asin);
CREATE INDEX idx_amazon_profit_report_sku ON amazon_profit_report(local_sku);
CREATE INDEX idx_amazon_profit_report_spu ON amazon_profit_report(spu);
CREATE INDEX idx_amazon_profit_report_sid ON amazon_profit_report(sid);
CREATE INDEX idx_amazon_profit_report_parent_asin ON amazon_profit_report(parent_asin);

-- 系统索引
CREATE INDEX idx_amazon_profit_report_sync_time ON amazon_profit_report(sync_time);
CREATE INDEX idx_amazon_profit_report_tenant_id ON amazon_profit_report(tenant_id);
CREATE INDEX idx_amazon_profit_report_deleted ON amazon_profit_report(deleted);

-- ============ 字段注释 ============
COMMENT ON COLUMN amazon_profit_report.id IS '主键ID';
COMMENT ON COLUMN amazon_profit_report.sync_start_date IS '同步查询的开始日期';
COMMENT ON COLUMN amazon_profit_report.sync_end_date IS '同步查询的结束日期';

COMMENT ON COLUMN amazon_profit_report.asin IS 'ASIN';
COMMENT ON COLUMN amazon_profit_report.parent_asin IS '父ASIN';
COMMENT ON COLUMN amazon_profit_report.local_sku IS 'SKU';
COMMENT ON COLUMN amazon_profit_report.spu IS 'SPU';
COMMENT ON COLUMN amazon_profit_report.sid IS 'SID';
COMMENT ON COLUMN amazon_profit_report.store_name IS '店铺名称';
COMMENT ON COLUMN amazon_profit_report.country_code IS '国家代码';

COMMENT ON COLUMN amazon_profit_report.total_sales_quantity IS '总销售数量';
COMMENT ON COLUMN amazon_profit_report.fba_sales_quantity IS 'FBA销售数量';
COMMENT ON COLUMN amazon_profit_report.fbm_sales_quantity IS 'FBM销售数量';
COMMENT ON COLUMN amazon_profit_report.refunds_quantity IS '退款数量';
COMMENT ON COLUMN amazon_profit_report.fba_returns_quantity IS 'FBA退货数量';

COMMENT ON COLUMN amazon_profit_report.total_sales_amount IS '总销售金额';
COMMENT ON COLUMN amazon_profit_report.fba_sale_amount IS 'FBA销售金额';
COMMENT ON COLUMN amazon_profit_report.fbm_sale_amount IS 'FBM销售金额';
COMMENT ON COLUMN amazon_profit_report.total_sales_refunds IS '总退款金额';

COMMENT ON COLUMN amazon_profit_report.total_ads_sales IS '广告总销售额';
COMMENT ON COLUMN amazon_profit_report.total_ads_cost IS '广告总花费';
COMMENT ON COLUMN amazon_profit_report.ads_sp_sales IS 'SP广告销售额';
COMMENT ON COLUMN amazon_profit_report.ads_sp_cost IS 'SP广告花费';
COMMENT ON COLUMN amazon_profit_report.ads_sd_sales IS 'SD广告销售额';
COMMENT ON COLUMN amazon_profit_report.ads_sd_cost IS 'SD广告花费';
COMMENT ON COLUMN amazon_profit_report.ads_sb_sales IS 'SB广告销售额';
COMMENT ON COLUMN amazon_profit_report.ads_sb_cost IS 'SB广告花费';

COMMENT ON COLUMN amazon_profit_report.platform_fee IS '平台费用';
COMMENT ON COLUMN amazon_profit_report.total_fba_delivery_fee IS 'FBA总配送费用';
COMMENT ON COLUMN amazon_profit_report.total_storage_fee IS '总仓储费用';
COMMENT ON COLUMN amazon_profit_report.promotion_fee IS '推广费用';

COMMENT ON COLUMN amazon_profit_report.cg_price_total IS '采购成本总额';
COMMENT ON COLUMN amazon_profit_report.cg_transport_costs_total IS '头程运输成本总额';
COMMENT ON COLUMN amazon_profit_report.cg_other_costs_total IS '其他成本总额';
COMMENT ON COLUMN amazon_profit_report.total_cost IS '合计成本';

COMMENT ON COLUMN amazon_profit_report.gross_profit IS '毛利润';
COMMENT ON COLUMN amazon_profit_report.gross_rate IS '毛利率';

COMMENT ON COLUMN amazon_profit_report.currency_code IS '货币代码';
COMMENT ON COLUMN amazon_profit_report.transaction_status IS '交易状态';
COMMENT ON COLUMN amazon_profit_report.local_name IS '产品本地名称/品名';

COMMENT ON COLUMN amazon_profit_report.other_fee_str IS '其他费用明细（JSON格式存储）';

COMMENT ON COLUMN amazon_profit_report.sync_time IS '数据同步时间';
COMMENT ON COLUMN amazon_profit_report.create_time IS '记录创建时间';
COMMENT ON COLUMN amazon_profit_report.update_time IS '记录更新时间';
COMMENT ON COLUMN amazon_profit_report.creator IS '创建人';
COMMENT ON COLUMN amazon_profit_report.updater IS '更新人';
COMMENT ON COLUMN amazon_profit_report.deleted IS '逻辑删除标记（0:未删除 1:已删除）';
COMMENT ON COLUMN amazon_profit_report.tenant_id IS '租户ID（多租户隔离）';