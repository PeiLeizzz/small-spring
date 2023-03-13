package com.peilei.springframework.test;

import cn.hutool.core.io.IoUtil;
import com.peilei.springframework.aop.combine.TargetSource;
import com.peilei.springframework.aop.combine.AdvisedSupport;
import com.peilei.springframework.aop.aspectj.AspectJExpressionPointCut;
import com.peilei.springframework.aop.proxy.Cglib2AopProxy;
import com.peilei.springframework.aop.proxy.JdkDynamicAopProxy;
import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.definition.BeanReference;
import com.peilei.springframework.beans.definition.PropertyValue;
import com.peilei.springframework.beans.definition.PropertyValues;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.factory.DefaultListableBeanFactory;
import com.peilei.springframework.beans.reader.XmlBeanDefinitionReader;
import com.peilei.springframework.beans.strategy.SimpleInstantiationStrategy;
import com.peilei.springframework.context.ClassPathXmlApplicationContext;
import com.peilei.springframework.core.io.DefaultResourceLoader;
import com.peilei.springframework.core.io.Resource;
import com.peilei.springframework.test.aop.AopUserService;
import com.peilei.springframework.test.aop.IUserService;
import com.peilei.springframework.test.aop.UserServiceInterceptor;
import com.peilei.springframework.test.bean.UserDao;
import com.peilei.springframework.test.bean.UserService;
import com.peilei.springframework.test.common.MyBeanFactoryPostProcessor;
import com.peilei.springframework.test.common.MyBeanPostProcessor;
import com.peilei.springframework.test.event.CustomEvent;
import org.junit.Before;
import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class ApiTest {
    private DefaultResourceLoader resourceLoader;

    @Before
    public void init() {
        resourceLoader = new DefaultResourceLoader();
    }

    @Test
    public void test_classpath() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:important.properties");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }

    @Test
    public void test_url() throws IOException {
        Resource resource = resourceLoader.getResource("https://www.baidu.com");
        InputStream inputStream = resource.getInputStream();
        String content = IoUtil.readUtf8(inputStream);
        System.out.println(content);
    }

    /**
     * 不通过应用上下文
     */
    @Test
    public void test_BeanFactoryPostProcessorAndBeanPostProcessor() throws BeansException {
        // 初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 读取配置文件 & 注册 Bean
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions("classpath:spring.xml");

        // BeanDefinition 加载完成 & Bean 实例化之前，修改 BeanDefinition 的属性值
        // 手动创建 processor
        MyBeanFactoryPostProcessor beanFactoryPostProcessor = new MyBeanFactoryPostProcessor();
        beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

        // Bean 实例化之后，修改 Bean 属性的值
        MyBeanPostProcessor beanPostProcessor = new MyBeanPostProcessor();
        // 手动创建 processor
        beanFactory.addBeanPostProcessor(beanPostProcessor);

        // 构造并获取 Bean 对象 & 调用方法
        UserService userService = beanFactory.getBean("userService", UserService.class);
        userService.queryUserInfo();
    }

    /**
     * 应用上下文
     */
    @Test
    public void test_xml() throws BeansException {
        // 初始化 BeanFactory
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.registerShutdownHook();

        // 构造并获取获取 Bean 对象 & 调用方法
        UserService userService = applicationContext.getBean("userService", UserService.class);
        userService.queryUserInfo();
        System.out.println("ApplicationContext: " + userService.getApplicationContext());
        System.out.println("BeanFactory: " + userService.getBeanFactory());
    }

    @Test
    public void test_BeanFactory_byXml() throws BeansException {
        // 初始化 BeanFactory
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // 读取配置文件 & 注册 Bean
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions("classpath:spring.xml");

        // 获取 Bean 对象，并调用方法
        UserService userService = factory.getBean("userService", UserService.class);
        userService.queryUserInfo();
    }

    @Test
    public void test_BeanFactory_byManual() throws BeansException {
        // 初始化 BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.setInstantiationStrategy(new SimpleInstantiationStrategy());

        // 注册 UserDao
        beanFactory.registerBeanDefinition("userDao", new BeanDefinition(UserDao.class));

        // 设置 UserService 属性
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("name", "10001"));
        propertyValues.addPropertyValue(new PropertyValue("userDao", new BeanReference("userDao")));

        // 注册 UserService
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class, propertyValues);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // 第一次获取 Bean（实例化）
        UserService userService = (UserService) beanFactory.getBean("userService", "test");
        userService.queryUserInfo();

        // 第二次获取 Bean（单例）
        UserService userService_singleton = (UserService) beanFactory.getBean("userService", "test2");
        userService_singleton.queryUserInfo();
    }

    @Test
    public void test_prototype() throws BeansException {
        // 初始化 FactoryBean
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-proxy.xml");
        applicationContext.registerShutdownHook();

        // 获取 Bean 对象调用方法
        com.peilei.springframework.test.ch10.UserService userService01 = applicationContext.getBean("userService", com.peilei.springframework.test.ch10.UserService.class);
        com.peilei.springframework.test.ch10.UserService userService02 = applicationContext.getBean("userService", com.peilei.springframework.test.ch10.UserService.class);

        // 配置 scope="prototype/singleton"
        System.out.println(userService01);
        System.out.println(userService02);

        // 打印十六进制哈希
        System.out.println(userService01 + " 十六进制哈希：" + Integer.toHexString(userService01.hashCode()));
        System.out.println(ClassLayout.parseInstance(userService01).toPrintable());
    }

    @Test
    public void test_factory_bean() throws BeansException {
        // 初始化 FactoryBean
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-proxy.xml");
        applicationContext.registerShutdownHook();

        // 调用代理方法
        com.peilei.springframework.test.ch10.UserService userService = applicationContext.getBean("userService", com.peilei.springframework.test.ch10.UserService.class);
        System.out.println("代理方法：" + userService.queryUserInfo());
    }

    @Test
    public void test_event() throws BeansException {
        // 初始化 FactoryBean
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-event.xml");
        applicationContext.registerShutdownHook();

        applicationContext.publishEvent(new CustomEvent(applicationContext, 1204129048102948L, "成功了"));
    }

    @Test
    public void test_aop() throws NoSuchMethodException {
        AspectJExpressionPointCut pointCut = new AspectJExpressionPointCut("execution(* com.peilei.springframework.test.bean.UserService.*(..))");
        Class<UserService> clazz = UserService.class;
        Method method = clazz.getDeclaredMethod("queryUserInfo");

        System.out.println(pointCut.matches(clazz));
        System.out.println(pointCut.matches(method, clazz));
    }

    @Test
    public void test_dynamic() {
        // 目标对象
        IUserService userService = new AopUserService();

        // 组装代理信息
        AdvisedSupport advised = new AdvisedSupport();
        advised.setTargetSource(new TargetSource(userService));
        advised.setMethodInterceptor(new UserServiceInterceptor());
        advised.setMethodMatcher(new AspectJExpressionPointCut("execution(* com.peilei.springframework.test.aop.IUserService.*(..))"));

        // JDK 代理对象
        IUserService proxy_jdk = (IUserService) new JdkDynamicAopProxy(advised).getProxy();
        System.out.println("测试结果：" + proxy_jdk.queryUserInfo());

        // Cglib 代理对象
        IUserService proxy_cglib = (IUserService) new Cglib2AopProxy(advised).getProxy();
        System.out.println("测试结果：" + proxy_cglib.register("test"));
    }

    @Test
    public void test_spring_aop() throws BeansException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-aop.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试结果：" + userService.queryUserInfo());
    }

    @Test
    public void test_auto_property() throws BeansException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-property.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试结果：" + userService);
    }

    @Test
    public void test_scan() throws BeansException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-scan.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("测试结果：" + userService.queryUserInfo());
    }
}
