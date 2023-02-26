package com.peilei.springframework.test.bean;

import com.peilei.springframework.beans.aware.BeanClassLoaderAware;
import com.peilei.springframework.beans.aware.BeanFactoryAware;
import com.peilei.springframework.beans.aware.BeanNameAware;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.BeanFactory;
import com.peilei.springframework.beans.processor.DisposableBean;
import com.peilei.springframework.beans.processor.InitializingBean;
import com.peilei.springframework.context.ApplicationContext;
import com.peilei.springframework.context.aware.ApplicationContextAware;

public class UserService implements InitializingBean, DisposableBean, BeanNameAware, BeanClassLoaderAware, ApplicationContextAware, BeanFactoryAware {
    private String name;
    private UserDao userDao;

    private String location;

    private String company;

    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;

    public UserService() {}
    public UserService(String name) {
        this.name = name;
    }
    public void queryUserInfo() {
        System.out.println("查询用户信息: " + userDao.queryUserName(name) + ", " + company + ", " + location);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("");
        sb.append("").append(name);
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("执行 UserService.destroy");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("执行 UserService.afterPropertiesSet");
    }


    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        System.out.println("ClassLoader: " + classLoader);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String beanName) {
        System.out.println("beanName: " + beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
