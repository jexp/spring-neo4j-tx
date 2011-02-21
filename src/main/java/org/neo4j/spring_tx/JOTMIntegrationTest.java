package org.neo4j.spring_tx;

import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.objectweb.jotm.Current;
import org.objectweb.jotm.TransactionImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

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
        assertEquals(TransactionImpl.class, transaction.getClass());
    }
}
