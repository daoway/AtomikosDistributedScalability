package com.blogspot.ostas.lora.nodes.manager;

import org.springframework.context.ApplicationContext;

public interface ICacheNodeManager {
    public void registerCacheNode(int nodeNumber);
    public void unregisterCacheNode(int nodeNumber);
}
