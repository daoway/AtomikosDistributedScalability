package com.blogspot.ostas.lora.nodes.replication;

import com.blogspot.ostas.lora.model.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Component
@Transactional
public class ReplicateObject {
    private static final StopWatch WATCH = new StopWatch();

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void replicate(SessionFactory[] sessionFactories, User user) {
        for (SessionFactory factory : sessionFactories) {
            factory.getCurrentSession().save(user);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public long replicateAndGetTimeMs(SessionFactory[] sessionFactories, User user) {
        WATCH.start();
        for (SessionFactory factory : sessionFactories) {
            factory.getCurrentSession().save(user);
        }
        WATCH.stop();
        return WATCH.getLastTaskTimeMillis();
    }
}
