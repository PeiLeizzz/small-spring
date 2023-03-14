package com.peilei.springframework.test.reference;

import com.peilei.springframework.beans.definition.FactoryBean;

import java.lang.reflect.Proxy;

public class HusbandMother implements FactoryBean<IMother> {
    @Override
    public IMother getObject() throws Exception {
        return (IMother) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{IMother.class},
                ((proxy, method, args) -> "proxy ..." + method.getName()));
    }

    @Override
    public Class<?> getObjectType() {
        return IMother.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
