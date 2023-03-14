package com.peilei.springframework.core.convert;

import com.sun.istack.internal.Nullable;

/**
 * 类型转换抽象接口
 */
public interface ConversionService {
    /**
     * 判断 sourceType 是否能转换为 targetType
     * @param sourceType
     * @param targetType
     * @return
     */
    boolean canConvert(@Nullable Class<?> sourceType, Class<?> targetType);

    /**
     * 将 source 转换为 targetType 类型
     * @param source
     * @param targetType
     * @return
     * @param <T>
     */
    <T> T convert(Object source, Class<T> targetType);
}
