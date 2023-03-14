package com.peilei.springframework.core.convert.converter;

/**
 * 类型转换注册接口
 */
public interface ConverterRegistry {
    /**
     * 注册单个类型转换器
     * @param converter
     */
    void addConverter(Converter<?, ?> converter);

    /**
     * 注册通用类型转换器
     * @param converter
     */
    void addConverter(GenericConverter converter);

    /**
     * 注册范围类型转换器（转换工厂）
     * @param converterFactory
     */
    void addConverterFactory(ConverterFactory<?, ?> converterFactory);
}