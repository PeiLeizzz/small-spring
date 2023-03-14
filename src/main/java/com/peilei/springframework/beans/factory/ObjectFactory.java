package com.peilei.springframework.beans.factory;

import com.peilei.springframework.beans.exception.BeansException;

/**
 * 返回实际对象的工厂接口
 * @param <T>
 */
public interface ObjectFactory<T> {
    T getObject() throws BeansException;
}
