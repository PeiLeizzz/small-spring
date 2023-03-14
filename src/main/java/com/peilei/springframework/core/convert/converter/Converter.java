package com.peilei.springframework.core.convert.converter;

/**
 * 单个类型转换处理接口
 * @param <S>
 * @param <T>
 */
public interface Converter<S, T> {
    /**
     * S 类型转换为 T 类型
     * @param source
     * @return
     */
    T convert(S source);
}
