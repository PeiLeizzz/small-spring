package com.peilei.springframework.test.converter;

import com.peilei.springframework.core.convert.converter.ConverterRegistry;
import com.peilei.springframework.core.convert.support.GenericConversionService;

public class MyConverterService extends GenericConversionService {
    public MyConverterService() {
        addDefaultConverters(this);
    }

    public static void addDefaultConverters(ConverterRegistry converterRegistry) {
        converterRegistry.addConverter(new StringToLocalDateConverter("yyyy-MM-dd"));
    }
}
