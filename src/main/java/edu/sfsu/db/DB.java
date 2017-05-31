package edu.sfsu.db;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

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

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DB init(Class<? extends DB> clazz, String db, Properties props) {
        String driver = props.getProperty(db + "_DRIVER");
        String url = props.getProperty(db + "_URL");
        String username = props.getProperty(db + "_USERNAME");
        String passwd = props.getProperty(db + "_PASSWD");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC driver '" + driver + "' is missing");
            throw new RuntimeException(e);
        }

        DB instance = null;
        try {
            Constructor<? extends DB> constructor = clazz.getConstructor(
                    new Class[] { String.class, String.class, String.class, String.class });
            instance = constructor.newInstance(driver, url, username, passwd);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        return instance;
    }
}
