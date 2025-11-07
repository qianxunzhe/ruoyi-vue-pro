-- 修改后的利润报表表结构 (按天同步)
-- 去掉 sync_start_date 和 sync_end_date，新增 sync_date
-- 以 sync_date + asin 作为唯一键

-- 如果存在旧表，需要备份
-- CREATE TABLE amazon_profit_report_backup AS SELECT * FROM amazon_profit_report;

-- 删除旧表
-- DROP TABLE IF EXISTS amazon_profit_report;

-- 创建新表结构
CREATE TABLE amazon_profit_report (
    id BIGSERIAL PRIMARY KEY,
    sync_date DATE NOT NULL COMMENT '同步日期（按天）',
    asin VARCHAR(20) NOT NULL COMMENT 'ASIN',
    parent_asin VARCHAR(20) COMMENT '父ASIN',
    local_sku VARCHAR(100) COMMENT 'SKU',
    spu VARCHAR(100) COMMENT 'SPU',
    sid VARCHAR(50) COMMENT 'SID（店铺ID）',
    store_name VARCHAR(200) COMMENT '店铺名称',
    country_code VARCHAR(10) COMMENT '国家代码',
    
    -- 销售数量相关
    total_sales_quantity INTEGER DEFAULT 0 COMMENT '总销售数量',
    fba_sales_quantity INTEGER DEFAULT 0 COMMENT 'FBA销售数量',
    fbm_sales_quantity INTEGER DEFAULT 0 COMMENT 'FBM销售数量',
    refunds_quantity INTEGER DEFAULT 0 COMMENT '退款数量',
    fba_returns_quantity INTEGER DEFAULT 0 COMMENT 'FBA退货数量',
    
    -- 销售金额相关
    total_sales_amount DECIMAL(20,4) DEFAULT 0 COMMENT '总销售金额',
    fba_sale_amount DECIMAL(20,4) DEFAULT 0 COMMENT 'FBA销售金额',
    fbm_sale_amount DECIMAL(20,4) DEFAULT 0 COMMENT 'FBM销售金额',
    total_sales_refunds DECIMAL(20,4) DEFAULT 0 COMMENT '总退款金额',
    
    -- 广告相关
    total_ads_sales DECIMAL(20,4) DEFAULT 0 COMMENT '广告总销售额',
    total_ads_cost DECIMAL(20,4) DEFAULT 0 COMMENT '广告总花费',
    ads_sp_sales DECIMAL(20,4) DEFAULT 0 COMMENT 'SP广告销售额',
    ads_sp_cost DECIMAL(20,4) DEFAULT 0 COMMENT 'SP广告花费',
    ads_sd_sales DECIMAL(20,4) DEFAULT 0 COMMENT 'SD广告销售额',
    ads_sd_cost DECIMAL(20,4) DEFAULT 0 COMMENT 'SD广告花费',
    ads_sb_sales DECIMAL(20,4) DEFAULT 0 COMMENT 'SB广告销售额',
    ads_sb_cost DECIMAL(20,4) DEFAULT 0 COMMENT 'SB广告花费',
    
    -- 费用相关
    platform_fee DECIMAL(20,4) DEFAULT 0 COMMENT '平台费用',
    total_fba_delivery_fee DECIMAL(20,4) DEFAULT 0 COMMENT 'FBA总配送费用',
    total_storage_fee DECIMAL(20,4) DEFAULT 0 COMMENT '总仓储费用',
    promotion_fee DECIMAL(20,4) DEFAULT 0 COMMENT '推广费用',
    
    -- 成本相关
    cg_price_total DECIMAL(20,4) DEFAULT 0 COMMENT '采购成本总额',
    cg_transport_costs_total DECIMAL(20,4) DEFAULT 0 COMMENT '头程运输成本总额',
    cg_other_costs_total DECIMAL(20,4) DEFAULT 0 COMMENT '其他成本总额',
    total_cost DECIMAL(20,4) DEFAULT 0 COMMENT '合计成本',
    
    -- 利润相关
    gross_profit DECIMAL(20,4) DEFAULT 0 COMMENT '毛利润',
    gross_rate DECIMAL(10,4) DEFAULT 0 COMMENT '毛利率(%)',
    
    -- 其他信息
    currency_code VARCHAR(10) COMMENT '货币代码',
    transaction_status VARCHAR(50) COMMENT '交易状态',
    local_name VARCHAR(500) COMMENT '产品本地名称/品名',
    other_fee_str JSONB COMMENT '其他费用明细（JSON格式存储）',
    
    -- 系统字段
    sync_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '数据同步时间',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT DEFAULT 0 COMMENT '租户编号'
);

-- 创建唯一索引（sync_date + asin）
CREATE UNIQUE INDEX uk_profit_report_date_asin ON amazon_profit_report(sync_date, asin) WHERE deleted = FALSE;

-- 创建其他业务索引
CREATE INDEX idx_profit_report_sync_date ON amazon_profit_report(sync_date);
CREATE INDEX idx_profit_report_asin ON amazon_profit_report(asin);
CREATE INDEX idx_profit_report_parent_asin ON amazon_profit_report(parent_asin);
CREATE INDEX idx_profit_report_sku ON amazon_profit_report(local_sku);
CREATE INDEX idx_profit_report_spu ON amazon_profit_report(spu);
CREATE INDEX idx_profit_report_sid ON amazon_profit_report(sid);
CREATE INDEX idx_profit_report_store ON amazon_profit_report(store_name);
CREATE INDEX idx_profit_report_country ON amazon_profit_report(country_code);

-- 添加注释
COMMENT ON TABLE amazon_profit_report IS '亚马逊利润报表数据（按天）';
COMMENT ON COLUMN amazon_profit_report.sync_date IS '同步日期（数据所属日期）';

-- 同步日志表也需要相应调整
CREATE TABLE IF NOT EXISTS amazon_profit_report_sync_log (
    id BIGSERIAL PRIMARY KEY,
    sync_date DATE NOT NULL COMMENT '同步的数据日期',
    sync_type VARCHAR(20) COMMENT '同步类型（FULL/INCREMENTAL）',
    sync_status VARCHAR(20) COMMENT '同步状态（PROCESSING/SUCCESS/FAILED）',
    total_count INTEGER DEFAULT 0 COMMENT '同步总条数',
    success_count INTEGER DEFAULT 0 COMMENT '成功条数',
    failed_count INTEGER DEFAULT 0 COMMENT '失败条数',
    error_msg TEXT COMMENT '错误信息',
    start_time TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP COMMENT '结束时间',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

CREATE INDEX idx_sync_log_date ON amazon_profit_report_sync_log(sync_date);
CREATE INDEX idx_sync_log_status ON amazon_profit_report_sync_log(sync_status);

COMMENT ON TABLE amazon_profit_report_sync_log IS '利润报表同步日志';