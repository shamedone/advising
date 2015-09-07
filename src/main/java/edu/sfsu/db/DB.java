package edu.sfsu.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private String     url;
    private String     user;
    private String     passwd;

    private Connection connection;


    public DB(String url, String user, String passwd) {
        this.url = url;
        this.user = user;
        this.passwd = passwd;
        connection = null;
    }

    protected Connection getConnection() {
        if (isConnected()) {
            return connection;
        }

        try {
            connection = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            e.printStackTrace();
            // Do nothing
        }
        return connection;
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            // Do nothing
        }
        connection = null;
    }
}
