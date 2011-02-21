package org.neo4j.spring_tx;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.kernel.Config;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.objectweb.jotm.Current;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.jta.ManagedTransactionAdapter;

/**
 * @author mh
 * @since 21.02.11
 */

public class JOTMIntegrationTest
{
    private ClassPathXmlApplicationContext ctx;
    private GraphDatabaseService gds;

    @Before
    public void setUp() throws Exception
    {
        ctx = new ClassPathXmlApplicationContext(
                "classpath:spring-tx-text-context.xml" );
        gds = ctx.getBean( GraphDatabaseService.class );
    }

    @After
    public void tearDown() throws Exception
    {
        if ( ctx != null ) ctx.close();
    }

    @Test
    public void testIndexDependencies() throws Exception
    {
        JtaTransactionManager tm = ctx.getBean( "transactionManager",
                JtaTransactionManager.class );
//        Transaction transaction = tm.createTransaction( "jotm", 1000 );
        org.neo4j.graphdb.Transaction transaction = gds.beginTx();
        Node node = null;
        try
        {
            // Index<Node> index = gds.index().forNodes("node");
            // IndexHits<Node> indexHits = index.get("field", "value");
            node = gds.createNode();
            assertNotNull( node );
            // assertEquals(false, indexHits.hasNext());
            transaction.success();
        }
        finally
        {
            transaction.finish();
        }
        Node readBackOutsideOfTx = gds.getNodeById( node.getId() );
        assertEquals( node, readBackOutsideOfTx );
        try
        {
            transaction = gds.beginTx();
            Node readBackInsideOfTx = gds.getNodeById( node.getId() );
            assertEquals( node, readBackInsideOfTx );
            transaction.success();
        }
        finally
        {
            transaction.finish();
        }
    }

    @Test
    public void testLoadConfig() throws SystemException, NotSupportedException
    {
        Current current = ctx.getBean( "jotm", Current.class );
        JtaTransactionManager tm = ctx.getBean( "transactionManager",
                JtaTransactionManager.class );
        Transaction transaction = tm.createTransaction( "jotm", 1000 );
        assertEquals( ManagedTransactionAdapter.class, transaction.getClass() );
        assertEquals(
                Current.class,
                ( (ManagedTransactionAdapter) transaction ).getTransactionManager().getClass() );
        Map<Object, Object> config = ( (EmbeddedGraphDatabase) gds ).getConfig().getParams();
        assertEquals( "spring-jta",
                config.get( Config.TXMANAGER_IMPLEMENTATION ) );

    }
}
