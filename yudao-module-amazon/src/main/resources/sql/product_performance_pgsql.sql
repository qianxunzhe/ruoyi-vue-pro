-- 产品表现数据表 (PostgreSQL版本)
CREATE TABLE amazon_product_performance (
  id BIGSERIAL PRIMARY KEY,
  asin VARCHAR(20) NOT NULL,
  parent_asin VARCHAR(20),
  msku VARCHAR(100),
  sku VARCHAR(100),
  sid BIGINT NOT NULL,
  mid INTEGER,
  seller_name VARCHAR(100),
  country VARCHAR(50),
  currency_code VARCHAR(10),
  currency_icon VARCHAR(10),
  
  -- 基础信息
  item_name VARCHAR(500),
  small_image_url VARCHAR(500),
  amazon_url VARCHAR(500),
  price DECIMAL(10,2),
  status SMALLINT,
  
  -- 销售数据
  volume INTEGER DEFAULT 0,
  order_items INTEGER DEFAULT 0,
  amount DECIMAL(15,2) DEFAULT 0.00,
  net_amount DECIMAL(15,2) DEFAULT 0.00,
  avg_custom_price DECIMAL(10,2),
  avg_volume DECIMAL(10,2),
  
  -- 环比数据
  volume_chain INTEGER DEFAULT 0,
  volume_chain_ratio DECIMAL(10,4) DEFAULT 0.0000,
  amount_chain DECIMAL(15,2) DEFAULT 0.00,
  amount_chain_ratio DECIMAL(10,4) DEFAULT 0.0000,
  order_items_chain INTEGER DEFAULT 0,
  order_chain_ratio DECIMAL(10,4) DEFAULT 0.0000,
  
  -- 促销数据
  promotion_volume INTEGER DEFAULT 0,
  promotion_amount DECIMAL(15,2) DEFAULT 0.00,
  promotion_order_items INTEGER DEFAULT 0,
  promotion_discount DECIMAL(10,2) DEFAULT 0.00,
  
  -- 毛利数据
  gross_profit DECIMAL(15,2) DEFAULT 0.00,
  predict_gross_profit DECIMAL(15,2) DEFAULT 0.00,
  gross_margin DECIMAL(10,4) DEFAULT 0.0000,
  predict_gross_margin DECIMAL(10,4) DEFAULT 0.0000,
  roi DECIMAL(10,4) DEFAULT 0.0000,
  
  -- 评价数据
  reviews_count INTEGER DEFAULT 0,
  avg_star DECIMAL(3,1) DEFAULT 0.0,
  prev_star DECIMAL(3,1) DEFAULT 0.0,
  comment_rate DECIMAL(10,4),
  
  -- 退货数据
  return_count INTEGER DEFAULT 0,
  return_rate DECIMAL(10,4) DEFAULT 0.0000,
  return_goods_count INTEGER DEFAULT 0,
  return_goods_rate DECIMAL(10,4) DEFAULT 0.0000,
  return_amount DECIMAL(15,2) DEFAULT 0.00,
  
  -- 库存数据
  afn_fulfillable_quantity INTEGER DEFAULT 0,
  afn_inbound_receiving_quantity INTEGER DEFAULT 0,
  afn_inbound_shipped_quantity INTEGER DEFAULT 0,
  afn_inbound_working_quantity INTEGER DEFAULT 0,
  afn_unsellable_quantity INTEGER DEFAULT 0,
  reserved_fc_processing INTEGER DEFAULT 0,
  reserved_fc_transfers INTEGER DEFAULT 0,
  fbm_quantity INTEGER DEFAULT 0,
  reserved_customerorders INTEGER DEFAULT 0,
  stock_up_num INTEGER DEFAULT 0,
  available_days INTEGER DEFAULT 0,
  fbm_available_days INTEGER DEFAULT 0,
  month_stock_sales_ratio DECIMAL(10,2),
  inventory_sales_ratio DECIMAL(10,4) DEFAULT 0.0000,
  
  -- 流量数据
  clicks INTEGER DEFAULT 0,
  sessions INTEGER DEFAULT 0,
  sessions_mobile INTEGER DEFAULT 0,
  sessions_total INTEGER DEFAULT 0,
  page_views INTEGER DEFAULT 0,
  page_views_mobile INTEGER DEFAULT 0,
  page_views_total INTEGER DEFAULT 0,
  buy_box_percentage DECIMAL(10,4),
  
  -- 转化率数据
  cvr DECIMAL(10,4) DEFAULT 0.0000,
  ctr DECIMAL(10,4) DEFAULT 0.0000,
  volume_cvr DECIMAL(10,4) DEFAULT 0.0000,
  ad_cvr DECIMAL(10,4) DEFAULT 0.0000,
  
  -- 广告数据
  impressions INTEGER DEFAULT 0,
  ad_clicks INTEGER DEFAULT 0,
  ad_order_quantity INTEGER DEFAULT 0,
  ad_sales_amount DECIMAL(15,2) DEFAULT 0.00,
  spend DECIMAL(15,2) DEFAULT 0.00,
  cpc DECIMAL(10,2),
  cpm DECIMAL(10,2),
  cpo DECIMAL(10,2),
  acos DECIMAL(10,4) DEFAULT 0.0000,
  acoas DECIMAL(10,4) DEFAULT 0.0000,
  roas DECIMAL(10,4) DEFAULT 0.0000,
  asoas DECIMAL(10,4) DEFAULT 0.0000,
  adv_rate DECIMAL(10,4) DEFAULT 0.0000,
  tacos DECIMAL(10,4) DEFAULT 0.0000,
  
  -- 广告细分数据
  ads_sp_cost DECIMAL(15,2) DEFAULT 0.00,
  ads_sp_sales DECIMAL(15,2) DEFAULT 0.00,
  ads_sd_cost DECIMAL(15,2) DEFAULT 0.00,
  ads_sd_sales DECIMAL(15,2) DEFAULT 0.00,
  shared_ads_sb_cost DECIMAL(15,2) DEFAULT 0.00,
  shared_ads_sb_sales DECIMAL(15,2) DEFAULT 0.00,
  shared_ads_sbv_cost DECIMAL(15,2) DEFAULT 0.00,
  shared_ads_sbv_sales DECIMAL(15,2) DEFAULT 0.00,
  shared_cost_of_advertising DECIMAL(15,2) DEFAULT 0.00,
  ad_direct_sales_amount DECIMAL(15,2) DEFAULT 0.00,
  ad_direct_order_quantity INTEGER DEFAULT 0,
  
  -- 排名数据
  cate_rank INTEGER DEFAULT 0,
  prev_cate_rank INTEGER DEFAULT 0,
  rank_category VARCHAR(200),
  small_cate_rank JSONB,
  
  -- 其他字段
  categories JSONB,
  brands JSONB,
  principal_names JSONB,
  developer_names JSONB,
  suppliers JSONB,
  attributes JSONB,
  tag_set JSONB,
  price_list JSONB,
  
  -- SKU维度特有字段
  local_name VARCHAR(200),
  cg_price DECIMAL(10,2),
  whs_value DECIMAL(15,2),
  local_quantity INTEGER,
  oversea_quantity INTEGER,
  avg_landed_price DECIMAL(10,2),
  model JSONB,
  
  -- 业务字段
  summary_field VARCHAR(50),
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  sync_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  
  -- 运营分析扩展字段
  prime_price DECIMAL(10,2),
  coupon_price DECIMAL(10,2),
  coupon_discount DECIMAL(10,2),
  target_orders INTEGER,
  promotion_activity VARCHAR(200),
  keyword_count INTEGER DEFAULT 0,
  p1_keywords INTEGER DEFAULT 0,
  p2_keywords INTEGER DEFAULT 0,
  p3_keywords INTEGER DEFAULT 0,
  total_traffic INTEGER DEFAULT 0,
  total_sales_amount DECIMAL(15,2) DEFAULT 0.00,
  inventory_age_0_90 INTEGER DEFAULT 0,
  inventory_age_91_180 INTEGER DEFAULT 0,
  inventory_age_180_plus INTEGER DEFAULT 0,
  expected_redundant_products INTEGER DEFAULT 0,
  storage_fee_30_days DECIMAL(10,2) DEFAULT 0.00,
  storage_fee_180_365_quantity INTEGER DEFAULT 0,
  storage_fee_180_365_amount DECIMAL(10,2) DEFAULT 0.00,
  break_even_tacos DECIMAL(10,4),
  operational_analysis TEXT,
  
  -- 系统字段
  creator VARCHAR(64) DEFAULT '',
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64) DEFAULT '',
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted SMALLINT NOT NULL DEFAULT 0,
  tenant_id BIGINT NOT NULL DEFAULT 0
);

-- 创建唯一索引
CREATE UNIQUE INDEX uk_asin_sid_date ON amazon_product_performance(asin, sid, start_date, end_date, summary_field);

-- 创建普通索引
CREATE INDEX idx_parent_asin ON amazon_product_performance(parent_asin);
CREATE INDEX idx_msku ON amazon_product_performance(msku);
CREATE INDEX idx_sku ON amazon_product_performance(sku);
CREATE INDEX idx_sid ON amazon_product_performance(sid);
CREATE INDEX idx_date_range ON amazon_product_performance(start_date, end_date);
CREATE INDEX idx_sync_time ON amazon_product_performance(sync_time);

-- 添加表注释
COMMENT ON TABLE amazon_product_performance IS '亚马逊产品表现数据表';

-- 添加字段注释
COMMENT ON COLUMN amazon_product_performance.id IS '主键ID';
COMMENT ON COLUMN amazon_product_performance.asin IS 'ASIN';
COMMENT ON COLUMN amazon_product_performance.parent_asin IS '父ASIN';
COMMENT ON COLUMN amazon_product_performance.msku IS 'MSKU（seller_sku）';
COMMENT ON COLUMN amazon_product_performance.sku IS 'SKU（local_sku）';
COMMENT ON COLUMN amazon_product_performance.sid IS '店铺ID';
COMMENT ON COLUMN amazon_product_performance.mid IS '站点ID';
COMMENT ON COLUMN amazon_product_performance.seller_name IS '店铺名称';
COMMENT ON COLUMN amazon_product_performance.country IS '国家';
COMMENT ON COLUMN amazon_product_performance.currency_code IS '货币类型';
COMMENT ON COLUMN amazon_product_performance.currency_icon IS '币种符号';
COMMENT ON COLUMN amazon_product_performance.item_name IS '产品标题';
COMMENT ON COLUMN amazon_product_performance.small_image_url IS '缩略图地址';
COMMENT ON COLUMN amazon_product_performance.amazon_url IS '亚马逊前台地址';
COMMENT ON COLUMN amazon_product_performance.price IS '价格';
COMMENT ON COLUMN amazon_product_performance.status IS '商品状态：-1未同步 1active 0inactive 2incomplete';
COMMENT ON COLUMN amazon_product_performance.volume IS '销量';
COMMENT ON COLUMN amazon_product_performance.order_items IS '订单量';
COMMENT ON COLUMN amazon_product_performance.amount IS '销售额';
COMMENT ON COLUMN amazon_product_performance.net_amount IS '净销售额';
COMMENT ON COLUMN amazon_product_performance.avg_custom_price IS '销售均价';
COMMENT ON COLUMN amazon_product_performance.avg_volume IS '平均销量';
COMMENT ON COLUMN amazon_product_performance.summary_field IS '汇总维度：asin/parent_asin/msku/sku';
COMMENT ON COLUMN amazon_product_performance.start_date IS '数据开始日期';
COMMENT ON COLUMN amazon_product_performance.end_date IS '数据结束日期';
COMMENT ON COLUMN amazon_product_performance.sync_time IS '同步时间';
COMMENT ON COLUMN amazon_product_performance.deleted IS '是否删除';
COMMENT ON COLUMN amazon_product_performance.tenant_id IS '租户编号';

-- 创建更新时间触发器函数
CREATE OR REPLACE FUNCTION update_updated_time_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 创建触发器
CREATE TRIGGER update_amazon_product_performance_updated_time 
    BEFORE UPDATE ON amazon_product_performance 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_time_column();