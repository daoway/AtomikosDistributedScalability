package com.blogspot.ostas.lora.nodes.manager;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.blogspot.ostas.lora.model.User;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Component("cacheNodeManager")
public class CacheNodeManagerImpl implements ApplicationContextAware, ICacheNodeManager {
    private ApplicationContext applicationContext;
    private DefaultListableBeanFactory defaultListableBeanFactory;
    private GenericBeanDefinition h2XA;
    private GenericBeanDefinition h2DataSource;
    private GenericBeanDefinition sessionFactoryBeanDefinition;
    private final Properties hibernateSessionFactoryProperties;
    private final Class[] annotatedClasses;

    public CacheNodeManagerImpl() {
        //same for all XA resources, so init goes here
        hibernateSessionFactoryProperties = new Properties();
        hibernateSessionFactoryProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        hibernateSessionFactoryProperties.setProperty("hibernate.hbm2ddl.auto", "create");
        hibernateSessionFactoryProperties.setProperty("hibernate.transaction.factory_class", "com.atomikos.icatch.jta.hibernate3.AtomikosJTATransactionFactory");
        hibernateSessionFactoryProperties.setProperty("hibernate.transaction.manager_lookup_class", "com.atomikos.icatch.jta.hibernate3.TransactionManagerLookup");
        annotatedClasses = new Class[]{User.class};
    }

    @Override
    public void registerCacheNode(int nodeNumber) {
        h2XA = new GenericBeanDefinition();
        h2XA.setBeanClass(org.h2.jdbcx.JdbcDataSource.class);
        MutablePropertyValues h2XaPropertyValues = new MutablePropertyValues();
        h2XaPropertyValues.add("URL", "jdbc:h2:mem:army/distributedJtaCache_Node_" + nodeNumber + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=true");
        h2XaPropertyValues.add("user", "sa");
        h2XaPropertyValues.add("password", "");
        h2XA.setPropertyValues(h2XaPropertyValues);
        h2XA.setScope("singleton");
        defaultListableBeanFactory.registerBeanDefinition("h2jdbcXA_Node_" + nodeNumber, h2XA);

        h2DataSource = new GenericBeanDefinition();
        h2DataSource.setBeanClass(AtomikosDataSourceBean.class);
        MutablePropertyValues h2DataSourceMutablePropertyValues = new MutablePropertyValues();
        h2DataSourceMutablePropertyValues.addPropertyValue("xaDataSource", new RuntimeBeanReference("h2jdbcXA_Node_" + nodeNumber));
        h2DataSourceMutablePropertyValues.addPropertyValue("uniqueResourceName", "h2DataSource_Node_" + nodeNumber);
        h2DataSourceMutablePropertyValues.addPropertyValue("poolSize", 1);
        h2DataSourceMutablePropertyValues.addPropertyValue("minPoolSize", 1);
        h2DataSourceMutablePropertyValues.addPropertyValue("maxPoolSize", 1);
        h2DataSourceMutablePropertyValues.addPropertyValue("testQuery", "select 1");

        h2DataSource.setPropertyValues(h2DataSourceMutablePropertyValues);
        h2DataSource.setInitMethodName("init");
        h2DataSource.setDestroyMethodName("close");
        defaultListableBeanFactory.registerBeanDefinition("h2DataSource_Node_" + nodeNumber, h2DataSource);

        //session factory
        sessionFactoryBeanDefinition = new GenericBeanDefinition();
        sessionFactoryBeanDefinition.setBeanClass(AnnotationSessionFactoryBean.class);
        MutablePropertyValues mutablePropertyValuesSessionFactoryBeanDefinition = new MutablePropertyValues();
        mutablePropertyValuesSessionFactoryBeanDefinition.addPropertyValue("annotatedClasses", annotatedClasses);
        mutablePropertyValuesSessionFactoryBeanDefinition.addPropertyValue("dataSource", new RuntimeBeanReference("h2DataSource_Node_" + nodeNumber));
        mutablePropertyValuesSessionFactoryBeanDefinition.addPropertyValue("hibernateProperties", hibernateSessionFactoryProperties);
        sessionFactoryBeanDefinition.setPropertyValues(mutablePropertyValuesSessionFactoryBeanDefinition);
        defaultListableBeanFactory.registerBeanDefinition("h2sessionFactory_Node_" + nodeNumber, sessionFactoryBeanDefinition);
    }

    @Override
    public void unregisterCacheNode(int nodeNumber) {
        defaultListableBeanFactory.removeBeanDefinition("h2sessionFactory_Node_" + nodeNumber);
        defaultListableBeanFactory.removeBeanDefinition("h2DataSource_Node_" + nodeNumber);
        defaultListableBeanFactory.removeBeanDefinition("h2jdbcXA_Node_" + nodeNumber);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void defaultListableBeanFactoryInit() {
        defaultListableBeanFactory = ((GenericApplicationContext) applicationContext).getDefaultListableBeanFactory();
    }
}
