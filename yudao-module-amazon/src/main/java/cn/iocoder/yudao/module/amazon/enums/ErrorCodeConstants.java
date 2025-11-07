package cn.iocoder.yudao.module.amazon.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

// TODO 待办：请将下面的错误码复制到 yudao-module-amazon 模块的 ErrorCodeConstants 类中。注意，请给“TODO 补充编号”设置一个错误码编号！！！

/**
 * ERP 错误码枚举类
 * <p>
 * erp 系统，使用 1-111-000-000 段
 */
public interface ErrorCodeConstants {
    ErrorCode KEYWORD_TASK_NOT_EXISTS = new ErrorCode(1111000000, "关键词排名任务不存在");


    ErrorCode KEYWORD_RANKING_NOT_EXISTS = new ErrorCode(1111000100, "关键词排名任务结果不存在");

    ErrorCode KEYWORD_ASIN_RANKING_NOT_EXISTS = new ErrorCode(1111000200, "关键词排名ASIN任务结果不存在");

    ErrorCode TASK_EXEC_LOG_NOT_EXISTS = new ErrorCode(1111000300, "Amazon任务执行日志不存在");

    ErrorCode LISTING_PRICE_NOT_EXISTS = new ErrorCode(1111000400, "价格监控结果不存在");

    ErrorCode ASIN_REVIEW_NOT_EXISTS = new ErrorCode(1111000500, "Amazon ASIN商品评论数据不存在");

    ErrorCode SHOPS_NOT_EXISTS = new ErrorCode(1111000600, "亚马逊店铺信息不存在");
    
    ErrorCode KEYWORD_TASK_EXCEED_LIMIT = new ErrorCode(1111000700, "活跃任务数量超过限制，每个用户最多只能创建3个活跃任务");

    ErrorCode PROFIT_REPORT_NOT_EXISTS = new ErrorCode(1111000800, "亚马逊利润报表数据不存在");

    ErrorCode REVIEW_SNAPSHOTS_NOT_EXISTS = new ErrorCode(1111000900, "亚马逊评论快照不存在");

    ErrorCode PRICE_RECORDS_NOT_EXISTS = new ErrorCode(1111001000, "亚马逊产品价格记录不存在");


}
