package com.blogspot.ostas.lora;

import com.blogspot.ostas.lora.context.MyConfigH2Node;
import com.blogspot.ostas.lora.nodes.manager.CacheNodeManagerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = {MyConfigH2Node.class})
@TransactionConfiguration(transactionManager = "jtaTransactionManager", defaultRollback = false)
public class CacheNodeManagerImplTestCase {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private CacheNodeManagerImpl cacheNodeManager;

    @Test
    public void init() {
        Assert.assertNotNull(applicationContext);
        Assert.assertNotNull(cacheNodeManager);
    }

    @Test
    public void cacheRegisterTest() {
        cacheNodeManager.registerCacheNode(111);
        Assert.assertNotNull(applicationContext.getBean("h2jdbcXA_Node_" + 111));
    }

    @Test(expected = org.springframework.beans.factory.NoSuchBeanDefinitionException.class)
    public void cacheUnregisterTest() {
        cacheNodeManager.unregisterCacheNode(111);
        Assert.assertNull(applicationContext.getBean("h2jdbcXA_Node_" + 111));
        Assert.assertNull(applicationContext.getBean("h2DataSource_Node_" + 111));
        cacheNodeManager.registerCacheNode(111);
    }
}
