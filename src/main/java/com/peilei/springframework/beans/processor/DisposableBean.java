package com.peilei.springframework.beans.processor;

/**
 * Bean 销毁方法接口
 */
public interface DisposableBean {
    /**
     * Bean 销毁方法
     * @throws Exception
     */
    void destroy() throws Exception;
}
