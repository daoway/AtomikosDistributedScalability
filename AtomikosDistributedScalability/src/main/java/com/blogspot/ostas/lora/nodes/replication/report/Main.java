package com.blogspot.ostas.lora.nodes.replication.report;

import com.blogspot.ostas.lora.context.MyConfigH2Node;
import com.blogspot.ostas.lora.model.User;
import com.blogspot.ostas.lora.nodes.manager.ICacheNodeManager;
import com.blogspot.ostas.lora.nodes.replication.ReplicateObject;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class Main {
    private GenericApplicationContext applicationContext;
    private ICacheNodeManager cacheNodeManager;
    private ReplicateObject replicateObject;
    private SessionFactory[] sessionFactories;

    public Main() {
        applicationContext = new AnnotationConfigApplicationContext(MyConfigH2Node.class);
        cacheNodeManager = (ICacheNodeManager) applicationContext.getBean("cacheNodeManager");
        replicateObject = (ReplicateObject) applicationContext.getBean("replicateObject");
    }

    public long getReplicationTimeAcrossNodes(int nodesCount, User obj) throws InterruptedException {
        for (int i = 0; i < nodesCount; i++) {
            cacheNodeManager.registerCacheNode(i);
        }
        sessionFactories = new SessionFactory[nodesCount];
        for (int i = 0; i < nodesCount; i++) {
            sessionFactories[i] = (SessionFactory) applicationContext.getBean("h2sessionFactory_Node_" + i);
        }
        long execTime = replicateObject.replicateAndGetTimeMs(sessionFactories, obj);
        for (int i = 0; i < nodesCount; i++) {
            cacheNodeManager.unregisterCacheNode(i);
        }
        sessionFactories = null;
        return execTime;
    }

    public static void main(String args[]) throws SQLException, InterruptedException {
        Main main = new Main();
        User obj = new User();
        obj.setName("test");
        obj.setPasswd("passwd");
        List<ExecutionItem> executionItemList = new LinkedList<ExecutionItem>();
        ExecutionItem executionItem;
        int higth = 2000;
        int low = 100;
        int delta = 100;
        //just for init
        main.getReplicationTimeAcrossNodes(10, obj);
        int j = 1;
        for (int i = low; i <= higth; i += delta, j++) {
            executionItem = new ExecutionItem();
            executionItem.setId(j);
            executionItem.setNodesCount(i);
            executionItem.setExecTime(main.getReplicationTimeAcrossNodes(i, obj));
            executionItemList.add(executionItem);
        }
        for (ExecutionItem execItem : executionItemList) {
            System.out.printf("Test result : %s\n", execItem.toString());
        }
    }
}
