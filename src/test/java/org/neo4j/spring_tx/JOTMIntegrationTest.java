package org.neo4j.spring_tx;

import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.objectweb.jotm.Current;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.jta.ManagedTransactionAdapter;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author mh
 * @since 21.02.11
 */

public class JOTMIntegrationTest {
    @Test
    public void testLoadConfig() throws SystemException, NotSupportedException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-tx-text-context.xml");
        GraphDatabaseService gds = ctx.getBean(GraphDatabaseService.class);
        Current current = ctx.getBean("jotm", Current.class);
        JtaTransactionManager tm = ctx.getBean("transactionManager", JtaTransactionManager.class);
        Transaction transaction = tm.createTransaction("jotm", 1000);
        assertEquals(ManagedTransactionAdapter.class, transaction.getClass());
        assertEquals(Current.class, ((ManagedTransactionAdapter) transaction).getTransactionManager().getClass());
        Map<Object, Object> config = ((EmbeddedGraphDatabase) gds).getConfig().getParams();
        assertEquals("spring-jta", config.get(Config.TXMANAGER_IMPLEMENTATION));

        org.neo4j.graphdb.Transaction tx = gds.beginTx();
        try {
            Index<Node> index = gds.index().forNodes("node");
            IndexHits<Node> indexHits = index.get("field", "value");
            assertEquals(false, indexHits.hasNext());
            tx.success();
        } finally {
            tx.finish();
        }
    }
}
