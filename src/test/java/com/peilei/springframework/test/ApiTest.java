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
import com.peilei.springframework.core.convert.converter.Converter;
import com.peilei.springframework.core.convert.support.StringToNumberConverterFactory;
import com.peilei.springframework.core.io.DefaultResourceLoader;
import com.peilei.springframework.core.io.Resource;
import com.peilei.springframework.test.anno.AnnoUserService;
import com.peilei.springframework.test.aop.AopUserService;
import com.peilei.springframework.test.aop.IUserService;
import com.peilei.springframework.test.aop.UserServiceInterceptor;
import com.peilei.springframework.test.bean.UserDao;
import com.peilei.springframework.test.bean.UserService;
import com.peilei.springframework.test.common.MyBeanFactoryPostProcessor;
import com.peilei.springframework.test.common.MyBeanPostProcessor;
import com.peilei.springframework.test.converter.ConvertHusband;
import com.peilei.springframework.test.converter.ConvertUserService;
import com.peilei.springframework.test.event.CustomEvent;
import com.peilei.springframework.test.reference.Husband;
import com.peilei.springframework.test.reference.Wife;
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
     * ????????????????????????
     */
    @Test
    public void test_BeanFactoryPostProcessorAndBeanPostProcessor() throws BeansException {
        // ????????? BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // ?????????????????? & ?????? Bean
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions("classpath:spring.xml");

        // BeanDefinition ???????????? & Bean ???????????????????????? BeanDefinition ????????????
        // ???????????? processor
        MyBeanFactoryPostProcessor beanFactoryPostProcessor = new MyBeanFactoryPostProcessor();
        beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);

        // Bean ???????????????????????? Bean ????????????
        MyBeanPostProcessor beanPostProcessor = new MyBeanPostProcessor();
        // ???????????? processor
        beanFactory.addBeanPostProcessor(beanPostProcessor);

        // ??????????????? Bean ?????? & ????????????
        UserService userService = beanFactory.getBean("userService", UserService.class);
        userService.queryUserInfo();
    }

    /**
     * ???????????????
     */
    @Test
    public void test_xml() throws BeansException {
        // ????????? BeanFactory
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        applicationContext.registerShutdownHook();

        // ????????????????????? Bean ?????? & ????????????
        UserService userService = applicationContext.getBean("userService", UserService.class);
        userService.queryUserInfo();
        System.out.println("ApplicationContext: " + userService.getApplicationContext());
        System.out.println("BeanFactory: " + userService.getBeanFactory());
    }

    @Test
    public void test_BeanFactory_byXml() throws BeansException {
        // ????????? BeanFactory
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        // ?????????????????? & ?????? Bean
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions("classpath:spring.xml");

        // ?????? Bean ????????????????????????
        UserService userService = factory.getBean("userService", UserService.class);
        userService.queryUserInfo();
    }

    @Test
    public void test_BeanFactory_byManual() throws BeansException {
        // ????????? BeanFactory
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.setInstantiationStrategy(new SimpleInstantiationStrategy());

        // ?????? UserDao
        beanFactory.registerBeanDefinition("userDao", new BeanDefinition(UserDao.class));

        // ?????? UserService ??????
        PropertyValues propertyValues = new PropertyValues();
        propertyValues.addPropertyValue(new PropertyValue("name", "10001"));
        propertyValues.addPropertyValue(new PropertyValue("userDao", new BeanReference("userDao")));

        // ?????? UserService
        BeanDefinition beanDefinition = new BeanDefinition(UserService.class, propertyValues);
        beanFactory.registerBeanDefinition("userService", beanDefinition);

        // ??????????????? Bean???????????????
        UserService userService = (UserService) beanFactory.getBean("userService", "test");
        userService.queryUserInfo();

        // ??????????????? Bean????????????
        UserService userService_singleton = (UserService) beanFactory.getBean("userService", "test2");
        userService_singleton.queryUserInfo();
    }

    @Test
    public void test_prototype() throws BeansException {
        // ????????? FactoryBean
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-proxy.xml");
        applicationContext.registerShutdownHook();

        // ?????? Bean ??????????????????
        com.peilei.springframework.test.ch10.UserService userService01 = applicationContext.getBean("userService", com.peilei.springframework.test.ch10.UserService.class);
        com.peilei.springframework.test.ch10.UserService userService02 = applicationContext.getBean("userService", com.peilei.springframework.test.ch10.UserService.class);

        // ?????? scope="prototype/singleton"
        System.out.println(userService01);
        System.out.println(userService02);

        // ????????????????????????
        System.out.println(userService01 + " ?????????????????????" + Integer.toHexString(userService01.hashCode()));
        System.out.println(ClassLayout.parseInstance(userService01).toPrintable());
    }

    @Test
    public void test_factory_bean() throws BeansException {
        // ????????? FactoryBean
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-proxy.xml");
        applicationContext.registerShutdownHook();

        // ??????????????????
        com.peilei.springframework.test.ch10.UserService userService = applicationContext.getBean("userService", com.peilei.springframework.test.ch10.UserService.class);
        System.out.println("???????????????" + userService.queryUserInfo());
    }

    @Test
    public void test_event() throws BeansException {
        // ????????? FactoryBean
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-event.xml");
        applicationContext.registerShutdownHook();

        applicationContext.publishEvent(new CustomEvent(applicationContext, 1204129048102948L, "?????????"));
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
        // ????????????
        IUserService userService = new AopUserService();

        // ??????????????????
        AdvisedSupport advised = new AdvisedSupport();
        advised.setTargetSource(new TargetSource(userService));
        advised.setMethodInterceptor(new UserServiceInterceptor());
        advised.setMethodMatcher(new AspectJExpressionPointCut("execution(* com.peilei.springframework.test.aop.IUserService.*(..))"));

        // JDK ????????????
        IUserService proxy_jdk = (IUserService) new JdkDynamicAopProxy(advised).getProxy();
        System.out.println("???????????????" + proxy_jdk.queryUserInfo());

        // Cglib ????????????
        IUserService proxy_cglib = (IUserService) new Cglib2AopProxy(advised).getProxy();
        System.out.println("???????????????" + proxy_cglib.register("test"));
    }

    @Test
    public void test_spring_aop() throws BeansException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-aop.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("???????????????" + userService.queryUserInfo());
    }

    @Test
    public void test_auto_property() throws BeansException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-property.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("???????????????" + userService);
    }

    @Test
    public void test_scan() throws BeansException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-scan.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("???????????????" + userService.queryUserInfo());
    }

    @Test
    public void test_auto_wired() throws BeansException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-anno.xml");
        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("???????????????" + userService.queryUserInfo());
    }

    @Test
    public void test_circle() throws BeansException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-ref.xml");
        Husband husband = applicationContext.getBean("husband", Husband.class);
        Wife wife = applicationContext.getBean("wife", Wife.class);
        System.out.println("husband: " + husband.queryWife());
        System.out.println("wife: " + wife.queryHusband());
    }

    @Test
    public void test_convert() throws BeansException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring-convert.xml");
        ConvertHusband husband = applicationContext.getBean("husband", ConvertHusband.class);
        System.out.println("???????????????" + husband);

        IUserService userService = applicationContext.getBean("userService", IUserService.class);
        System.out.println("???????????????" + userService.queryUserInfo());
        System.out.println(((ConvertUserService) userService).getCardId().getClass());
    }

    @Test
    public void test_StringToNumberConverter() {
        StringToNumberConverterFactory converterFactory = new StringToNumberConverterFactory();
        Converter<String, Integer> stringIntegerConverter = converterFactory.getConverter(Integer.class);
        System.out.println("???????????????" + stringIntegerConverter.convert("1234"));
        Converter<String, Long> stringLongConverter = converterFactory.getConverter(Long.class);
        System.out.println("???????????????" + stringLongConverter.convert("1234"));
    }
}
