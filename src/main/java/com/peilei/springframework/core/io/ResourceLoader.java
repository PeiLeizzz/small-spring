package com.peilei.springframework.core.io;

/**
 * 包装资源加载器
 */
public interface ResourceLoader {
    // 默认的 classpath 文件前缀
    String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 根据 location 地址，自动选择适合的资源加载器
     * @param location
     * @return
     */
    Resource getResource(String location);
}
