package com.peilei.springframework.core.convert.converter;

/**
 * 类型范围转换工厂，可以将 S 转换为 R 及其子类
 * @param <S>
 * @param <R>
 */
public interface ConverterFactory<S, R> {
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
