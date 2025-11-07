-- 产品表现数据表
CREATE TABLE `amazon_product_performance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `asin` varchar(20) NOT NULL COMMENT 'ASIN',
  `parent_asin` varchar(20) DEFAULT NULL COMMENT '父ASIN',
  `msku` varchar(100) DEFAULT NULL COMMENT 'MSKU（seller_sku）',
  `sku` varchar(100) DEFAULT NULL COMMENT 'SKU（local_sku）',
  `sid` bigint(20) NOT NULL COMMENT '店铺ID',
  `mid` int(11) DEFAULT NULL COMMENT '站点ID',
  `seller_name` varchar(100) DEFAULT NULL COMMENT '店铺名称',
  `country` varchar(50) DEFAULT NULL COMMENT '国家',
  `currency_code` varchar(10) DEFAULT NULL COMMENT '货币类型',
  `currency_icon` varchar(10) DEFAULT NULL COMMENT '币种符号',
  
  -- 基础信息
  `item_name` varchar(500) DEFAULT NULL COMMENT '产品标题',
  `small_image_url` varchar(500) DEFAULT NULL COMMENT '缩略图地址',
  `amazon_url` varchar(500) DEFAULT NULL COMMENT '亚马逊前台地址',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `status` tinyint(4) DEFAULT NULL COMMENT '商品状态：-1未同步 1active 0inactive 2incomplete',
  
  -- 销售数据
  `volume` int(11) DEFAULT 0 COMMENT '销量',
  `order_items` int(11) DEFAULT 0 COMMENT '订单量',
  `amount` decimal(15,2) DEFAULT 0.00 COMMENT '销售额',
  `net_amount` decimal(15,2) DEFAULT 0.00 COMMENT '净销售额',
  `avg_custom_price` decimal(10,2) DEFAULT NULL COMMENT '销售均价',
  `avg_volume` decimal(10,2) DEFAULT NULL COMMENT '平均销量',
  
  -- 环比数据
  `volume_chain` int(11) DEFAULT 0 COMMENT '环比销量',
  `volume_chain_ratio` decimal(10,4) DEFAULT 0.0000 COMMENT '销量环比',
  `amount_chain` decimal(15,2) DEFAULT 0.00 COMMENT '环比销售额',
  `amount_chain_ratio` decimal(10,4) DEFAULT 0.0000 COMMENT '销售额环比',
  `order_items_chain` int(11) DEFAULT 0 COMMENT '环比订单量',
  `order_chain_ratio` decimal(10,4) DEFAULT 0.0000 COMMENT '订单量环比',
  
  -- 促销数据
  `promotion_volume` int(11) DEFAULT 0 COMMENT '促销销量',
  `promotion_amount` decimal(15,2) DEFAULT 0.00 COMMENT '促销销售额',
  `promotion_order_items` int(11) DEFAULT 0 COMMENT '促销订单量',
  `promotion_discount` decimal(10,2) DEFAULT 0.00 COMMENT '促销折扣',
  
  -- 毛利数据
  `gross_profit` decimal(15,2) DEFAULT 0.00 COMMENT '结算毛利润',
  `predict_gross_profit` decimal(15,2) DEFAULT 0.00 COMMENT '订单毛利润',
  `gross_margin` decimal(10,4) DEFAULT 0.0000 COMMENT '结算毛利率',
  `predict_gross_margin` decimal(10,4) DEFAULT 0.0000 COMMENT '订单毛利率',
  `roi` decimal(10,4) DEFAULT 0.0000 COMMENT 'ROI',
  
  -- 评价数据
  `reviews_count` int(11) DEFAULT 0 COMMENT '评论数',
  `avg_star` decimal(3,1) DEFAULT 0.0 COMMENT '评分',
  `prev_star` decimal(3,1) DEFAULT 0.0 COMMENT '前一个评分',
  `comment_rate` decimal(10,4) DEFAULT NULL COMMENT '留评率',
  
  -- 退货数据
  `return_count` int(11) DEFAULT 0 COMMENT '退款量',
  `return_rate` decimal(10,4) DEFAULT 0.0000 COMMENT '退款率',
  `return_goods_count` int(11) DEFAULT 0 COMMENT '退货量',
  `return_goods_rate` decimal(10,4) DEFAULT 0.0000 COMMENT '退货率',
  `return_amount` decimal(15,2) DEFAULT 0.00 COMMENT '退款金额',
  
  -- 库存数据
  `afn_fulfillable_quantity` int(11) DEFAULT 0 COMMENT 'FBA可售',
  `afn_inbound_receiving_quantity` int(11) DEFAULT 0 COMMENT 'FBA入库中',
  `afn_inbound_shipped_quantity` int(11) DEFAULT 0 COMMENT 'FBA在途',
  `afn_inbound_working_quantity` int(11) DEFAULT 0 COMMENT 'FBA计划入库',
  `afn_unsellable_quantity` int(11) DEFAULT 0 COMMENT 'FBA不可售',
  `reserved_fc_processing` int(11) DEFAULT 0 COMMENT '调仓中',
  `reserved_fc_transfers` int(11) DEFAULT 0 COMMENT '待调仓',
  `fbm_quantity` int(11) DEFAULT 0 COMMENT 'FBM可售',
  `reserved_customerorders` int(11) DEFAULT 0 COMMENT '待发货',
  `stock_up_num` int(11) DEFAULT 0 COMMENT '实际在途',
  `available_days` int(11) DEFAULT 0 COMMENT '可售预估天数',
  `fbm_available_days` int(11) DEFAULT 0 COMMENT 'FBM可售天数',
  `month_stock_sales_ratio` decimal(10,2) DEFAULT NULL COMMENT '月库销比',
  `inventory_sales_ratio` decimal(10,4) DEFAULT 0.0000 COMMENT '存销比',
  
  -- 流量数据
  `clicks` int(11) DEFAULT 0 COMMENT '点击量',
  `sessions` int(11) DEFAULT 0 COMMENT 'Sessions-Browser',
  `sessions_mobile` int(11) DEFAULT 0 COMMENT 'Sessions-Mobile',
  `sessions_total` int(11) DEFAULT 0 COMMENT 'Sessions-Total',
  `page_views` int(11) DEFAULT 0 COMMENT 'PV-Browser',
  `page_views_mobile` int(11) DEFAULT 0 COMMENT 'PV-Mobile',
  `page_views_total` int(11) DEFAULT 0 COMMENT 'PV-Total',
  `buy_box_percentage` decimal(10,4) DEFAULT NULL COMMENT 'Buybox占比',
  
  -- 转化率数据
  `cvr` decimal(10,4) DEFAULT 0.0000 COMMENT '转化率',
  `ctr` decimal(10,4) DEFAULT 0.0000 COMMENT '点击率',
  `volume_cvr` decimal(10,4) DEFAULT 0.0000 COMMENT '销量CVR',
  `ad_cvr` decimal(10,4) DEFAULT 0.0000 COMMENT '广告CVR',
  
  -- 广告数据
  `impressions` int(11) DEFAULT 0 COMMENT '曝光量',
  `ad_clicks` int(11) DEFAULT 0 COMMENT '广告点击量',
  `ad_order_quantity` int(11) DEFAULT 0 COMMENT '广告订单量',
  `ad_sales_amount` decimal(15,2) DEFAULT 0.00 COMMENT '广告销售额',
  `spend` decimal(15,2) DEFAULT 0.00 COMMENT '广告花费',
  `cpc` decimal(10,2) DEFAULT NULL COMMENT 'CPC',
  `cpm` decimal(10,2) DEFAULT NULL COMMENT 'CPM',
  `cpo` decimal(10,2) DEFAULT NULL COMMENT 'CPO',
  `acos` decimal(10,4) DEFAULT 0.0000 COMMENT 'ACOS',
  `acoas` decimal(10,4) DEFAULT 0.0000 COMMENT 'ACoAS',
  `roas` decimal(10,4) DEFAULT 0.0000 COMMENT 'ROAS',
  `asoas` decimal(10,4) DEFAULT 0.0000 COMMENT 'ASoAS',
  `adv_rate` decimal(10,4) DEFAULT 0.0000 COMMENT '广告订单量占比',
  `tacos` decimal(10,4) DEFAULT 0.0000 COMMENT 'TACOS',
  
  -- 广告细分数据
  `ads_sp_cost` decimal(15,2) DEFAULT 0.00 COMMENT 'SP广告费',
  `ads_sp_sales` decimal(15,2) DEFAULT 0.00 COMMENT 'SP广告销售额',
  `ads_sd_cost` decimal(15,2) DEFAULT 0.00 COMMENT 'SD广告费',
  `ads_sd_sales` decimal(15,2) DEFAULT 0.00 COMMENT 'SD广告销售额',
  `shared_ads_sb_cost` decimal(15,2) DEFAULT 0.00 COMMENT 'SB广告费',
  `shared_ads_sb_sales` decimal(15,2) DEFAULT 0.00 COMMENT 'SB广告销售额',
  `shared_ads_sbv_cost` decimal(15,2) DEFAULT 0.00 COMMENT 'SBV广告费',
  `shared_ads_sbv_sales` decimal(15,2) DEFAULT 0.00 COMMENT 'SBV广告销售额',
  `shared_cost_of_advertising` decimal(15,2) DEFAULT 0.00 COMMENT '差异分摊',
  `ad_direct_sales_amount` decimal(15,2) DEFAULT 0.00 COMMENT '直接成交销售额',
  `ad_direct_order_quantity` int(11) DEFAULT 0 COMMENT '直接成交订单量',
  
  -- 排名数据
  `cate_rank` int(11) DEFAULT 0 COMMENT '大类排名',
  `prev_cate_rank` int(11) DEFAULT 0 COMMENT '上一次大类排名',
  `rank_category` varchar(200) DEFAULT NULL COMMENT '大类排名分类',
  `small_cate_rank` json DEFAULT NULL COMMENT '小类排名JSON',
  
  -- 其他字段
  `categories` json DEFAULT NULL COMMENT '分类JSON数组',
  `brands` json DEFAULT NULL COMMENT '品牌JSON数组',
  `principal_names` json DEFAULT NULL COMMENT '负责人JSON数组',
  `developer_names` json DEFAULT NULL COMMENT '开发人JSON数组',
  `suppliers` json DEFAULT NULL COMMENT '供应商JSON数组',
  `attributes` json DEFAULT NULL COMMENT '属性JSON数组',
  `tag_set` json DEFAULT NULL COMMENT '标签信息JSON数组',
  `price_list` json DEFAULT NULL COMMENT '价格列表JSON',
  
  -- SKU维度特有字段
  `local_name` varchar(200) DEFAULT NULL COMMENT '品名',
  `cg_price` decimal(10,2) DEFAULT NULL COMMENT '采购成本',
  `whs_value` decimal(15,2) DEFAULT NULL COMMENT '可用货值',
  `local_quantity` int(11) DEFAULT NULL COMMENT '本地可用',
  `oversea_quantity` int(11) DEFAULT NULL COMMENT '海外仓可用',
  `avg_landed_price` decimal(10,2) DEFAULT NULL COMMENT '平均售价',
  `model` json DEFAULT NULL COMMENT '型号JSON数组',
  
  -- 业务字段
  `summary_field` varchar(50) DEFAULT NULL COMMENT '汇总维度：asin/parent_asin/msku/sku',
  `start_date` date NOT NULL COMMENT '数据开始日期',
  `end_date` date NOT NULL COMMENT '数据结束日期',
  `sync_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '同步时间',
  
  -- 运营分析扩展字段（用户需求）
  `prime_price` decimal(10,2) DEFAULT NULL COMMENT 'Prime价格',
  `coupon_price` decimal(10,2) DEFAULT NULL COMMENT 'Coupon价格',
  `coupon_discount` decimal(10,2) DEFAULT NULL COMMENT 'Coupon折扣率',
  `target_orders` int(11) DEFAULT NULL COMMENT '目标订单',
  `promotion_activity` varchar(200) DEFAULT NULL COMMENT '促销活动',
  `keyword_count` int(11) DEFAULT 0 COMMENT '关键词数量',
  `p1_keywords` int(11) DEFAULT 0 COMMENT 'P1关键词数量',
  `p2_keywords` int(11) DEFAULT 0 COMMENT 'P2关键词数量',
  `p3_keywords` int(11) DEFAULT 0 COMMENT 'P3关键词数量',
  `total_traffic` int(11) DEFAULT 0 COMMENT '总流量',
  `total_sales_amount` decimal(15,2) DEFAULT 0.00 COMMENT '总销售额',
  `inventory_age_0_90` int(11) DEFAULT 0 COMMENT '0-90天库龄',
  `inventory_age_91_180` int(11) DEFAULT 0 COMMENT '91-180天库龄',
  `inventory_age_180_plus` int(11) DEFAULT 0 COMMENT '180+天库龄',
  `expected_redundant_products` int(11) DEFAULT 0 COMMENT '预计冗余商品',
  `storage_fee_30_days` decimal(10,2) DEFAULT 0.00 COMMENT '30天仓储费',
  `storage_fee_180_365_quantity` int(11) DEFAULT 0 COMMENT '180-365天收费数量',
  `storage_fee_180_365_amount` decimal(10,2) DEFAULT 0.00 COMMENT '180-365天收费金额',
  `break_even_tacos` decimal(10,4) DEFAULT NULL COMMENT '盈利平衡TACOS',
  `operational_analysis` text DEFAULT NULL COMMENT '运营分析',
  
  -- 系统字段
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户编号',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_asin_sid_date` (`asin`,`sid`,`start_date`,`end_date`,`summary_field`),
  KEY `idx_parent_asin` (`parent_asin`),
  KEY `idx_msku` (`msku`),
  KEY `idx_sku` (`sku`),
  KEY `idx_sid` (`sid`),
  KEY `idx_date_range` (`start_date`,`end_date`),
  KEY `idx_sync_time` (`sync_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='亚马逊产品表现数据表';