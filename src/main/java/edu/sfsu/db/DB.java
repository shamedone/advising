package edu.sfsu.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public class DB {

    private BasicDataSource dataSource;


    public DB(String driver, String url, String user, String passwd) {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(user);
        dataSource.setPassword(passwd);
        dataSource.setUrl(url);
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
