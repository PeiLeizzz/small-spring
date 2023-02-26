package com.peilei.springframework.core.io;

import cn.hutool.core.lang.Assert;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 默认的包装资源加载器
 * 能够根据 location 动态判断合适的加载方法
 */
public class DefaultResourceLoader implements ResourceLoader {
    @Override
    public Resource getResource(String location) {
        Assert.notNull(location, "location must not be null");

        // ClassPathResource
        if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()));
        }

        try {
            // UrlResource
            URL url = new URL(location);
            return new UrlResource(url);
        } catch (MalformedURLException e) {
            // FileSystemResource
            return new FileSystemResource(location);
        }
    }
}
