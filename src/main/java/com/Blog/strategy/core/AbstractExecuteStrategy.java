package com.Blog.strategy.core;


/**
 * 提供统一的策略接口
 *
 * @param <REQUEST> 请求参数
 * @param <RESPONSE> 返回值
 */
public interface AbstractExecuteStrategy<REQUEST,RESPONSE> {

    /**
     * 执行策略标识
     */
    default String mark() {
        return null;
    }

    /**
     * 执行策略
     *
     * @param requestParam 执行策略入参
     */
    default void execute(REQUEST requestParam) {

    }

    /**
     * 执行策略，带返回值
     *
     * @param requestParam 执行策略入参
     * @return 执行策略后返回值
     */
    default RESPONSE executeResp(REQUEST requestParam) {
        return null;
    }

}
