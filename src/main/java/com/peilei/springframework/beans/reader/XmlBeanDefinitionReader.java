package com.peilei.springframework.beans.reader;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import com.peilei.springframework.beans.definition.BeanDefinition;
import com.peilei.springframework.beans.definition.BeanReference;
import com.peilei.springframework.beans.definition.PropertyValue;
import com.peilei.springframework.beans.exception.BeansException;
import com.peilei.springframework.beans.registry.BeanDefinitionRegistry;
import com.peilei.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import com.peilei.springframework.core.io.Resource;
import com.peilei.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;

/**
 * xml 形式的 Bean 定义读取并加载类
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {
    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry, ResourceLoader resourceLoader) {
        super(registry, resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(Resource resource) throws BeansException {
        try {
            try (InputStream inputStream = resource.getInputStream()) {
                doLoadBeanDefinitions(inputStream);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new BeansException("IOException parsing XML document from " + resource, e);
        }
    }

    @Override
    public void loadBeanDefinitions(Resource... resources) throws BeansException {
        for (Resource resource : resources) {
            loadBeanDefinitions(resource);
        }
    }

    @Override
    public void loadBeanDefinitions(String location) throws BeansException {
        ResourceLoader resourceLoader = getResourceLoader();
        Resource resource = resourceLoader.getResource(location);
        loadBeanDefinitions(resource);
    }

    @Override
    public void loadBeanDefinitions(String ...locations) throws BeansException {
        for (String location : locations) {
            loadBeanDefinitions(location);
        }
    }

    /**
     * 加载 Bean 信息核心逻辑
     * 包含解析 xml 结构，读取其中的 Bean 定义信息及其属性值集合，并将其注册到 Registry 中
     * 如果同一个 Bean 的定义出现了多次，则会抛出错误
     * @param inputStream
     * @throws ClassNotFoundException
     * @throws BeansException
     */
    protected void doLoadBeanDefinitions(InputStream inputStream) throws ClassNotFoundException, BeansException {
        Document doc = XmlUtil.readXML(inputStream);
        Element root = doc.getDocumentElement();

        // 解析 component-scan 标签，扫描包中的类并提取相关信息，用于组装 BeanDefinition
        NodeList componentScanNodes = root.getElementsByTagName("component-scan");
        for (int i = 0; i < componentScanNodes.getLength(); i++) {
            if (!(componentScanNodes.item(i) instanceof Element)) {
                continue;
            }
            Element componentScan = (Element) componentScanNodes.item(0);
            String scanPath = componentScan.getAttribute("base-package");
            if (StrUtil.isEmpty(scanPath)) {
                throw new BeansException("The value of base-package attribute can not be empty or null");
            }
            scanPackage(scanPath);
        }

        NodeList childNodes = root.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            // 判断元素类型
            if (!(childNodes.item(i) instanceof Element)) {
                continue;
            }

            // 判断对象
            if (!childNodes.item(i).getNodeName().equals("bean")) {
                continue;
            }

            // 解析标签
            Element bean = (Element) childNodes.item(i);
            String id = bean.getAttribute("id");
            String name = bean.getAttribute("name");
            String className = bean.getAttribute("class");
            String initMethod = bean.getAttribute("init-method");
            String destroyMethodName = bean.getAttribute("destroy-method");
            String beanScope = bean.getAttribute("scope");

            // 获取 Class，方便获取类中的名称
            Class<?> clazz = Class.forName(className);
            // 优先级 id > name
            String beanName = StrUtil.isNotEmpty(id)? id: name;
            if (StrUtil.isEmpty(beanName)) {
                // Bean 默认名称为类名的小写字母开头驼峰形式
                beanName = StrUtil.lowerFirst(clazz.getSimpleName());
            }

            // 定义 Bean
            BeanDefinition beanDefinition = new BeanDefinition(clazz);
            beanDefinition.setInitMethodName(initMethod);
            beanDefinition.setDestroyMethodName(destroyMethodName);

            if (StrUtil.isNotEmpty(beanScope)) {
                beanDefinition.setScope(beanScope);
            }

            // 读取属性并填充
            for (int j = 0; j < bean.getChildNodes().getLength(); j++) {
                if (!(bean.getChildNodes().item(j) instanceof Element)) {
                    continue;
                }

                if (!bean.getChildNodes().item(j).getNodeName().equals("property")) {
                    continue;
                }

                // 解析标签
                Element property = (Element) bean.getChildNodes().item(j);
                String attrName = property.getAttribute("name");
                String attrValue = property.getAttribute("value");
                String attrRef = property.getAttribute("ref");
                // 得到属性值：引用对象 / 值对象
                Object value = StrUtil.isNotEmpty(attrRef)? new BeanReference(attrRef): attrValue;
                // 创建属性信息
                PropertyValue propertyValue = new PropertyValue(attrName, value);
                beanDefinition.getPropertyValues().addPropertyValue(propertyValue);
            }

            // Bean 重复定义
            if (getRegistry().containsBeanDefinition(beanName)) {
                throw new BeansException("Duplicate beanName[" + beanName + "] is not allowed");
            }

            getRegistry().registerBeanDefinition(beanName, beanDefinition);
        }
    }

    /**
     * 扫描包路径
     * @param scanPath
     */
    private void scanPackage(String scanPath) {
        String[] basePackages = StrUtil.splitToArray(scanPath, ',');
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getRegistry());
        scanner.doScan(basePackages);
    }
}
