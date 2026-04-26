package config;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class DataSourceProvider {

    private static DataSource dataSource;

    private DataSourceProvider() {
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            try {
                Context initCtx = new InitialContext();
                Context envCtx = (Context) initCtx.lookup("java:comp/env");
                dataSource = (DataSource) envCtx.lookup("jdbc/converterDB");
            } catch (NamingException e) {
                throw new RuntimeException("Cannot load JNDI DataSource", e);
            }
        }
        return dataSource;
    }
}