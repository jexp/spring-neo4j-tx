package org.neo4j.spring_tx;

import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.objectweb.jotm.Current;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.transaction.TransactionManager;

/**
 * @author mh
 * @since 21.02.11
 */

public class JOTMIntegrationTest {
    @Test
    public void testLoadConfig() {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-tx-text-context.xml");
        GraphDatabaseService gds = ctx.getBean(GraphDatabaseService.class);
        Current current = ctx.getBean("jotm", Current.class);
    }
}
