package com.github.zalesskyi.base.database;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import javax.sql.DataSource;

/**
 * Класс, предоставляющий пул соединений с БД.
 */
public class ConnectionPool {
    private GenericObjectPool gPool = null;


    public GenericObjectPool getConnectionPool() {
        return gPool;
    }


    public DataSource setupPool()
            throws ClassNotFoundException {
        Class.forName(DbSchema.JDBC_DRIVER);

        gPool = new GenericObjectPool();
        gPool.setMaxActive(5);

        ConnectionFactory conFactory = new DriverManagerConnectionFactory(
                DbSchema.DB_URL, DbSchema.Credentials.LOGIN, DbSchema.Credentials.PASSWORD);

        PoolableConnectionFactory pcf = new PoolableConnectionFactory(conFactory, gPool, null, null, false, true);
        return new PoolingDataSource(gPool);
    }
}
