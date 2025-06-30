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
    // TODO 待办：请将下面的错误码复制到 yudao-module-amazon 模块的 ErrorCodeConstants 类中。注意，请给“TODO 补充编号”设置一个错误码编号！！！
// ========== Amazon任务执行日志 TODO 补充编号 ==========
    ErrorCode TASK_EXEC_LOG_NOT_EXISTS = new ErrorCode(1111000300, "Amazon任务执行日志不存在");
}
