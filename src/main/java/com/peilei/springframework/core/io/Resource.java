package com.peilei.springframework.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 资源加载接口
 */
public interface Resource {
    /**
     * 获取资源的输入流
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;
}
