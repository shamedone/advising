package edu.sfsu.db;

import java.sql.*;

public class CommentDB extends DB {

    final static private String DB_NAME = "ADVISING";
    final static private String TABLE_NAME = "COMMENTS";

    final static private String KEY_STUDENT_ID = "STUDENT_ID";
    final static private String KEY_COURSE = "COURSE";
    final static private String KEY_COMMENT = "COMMENT";


    public CommentDB(String driver, String url, String user, String passwd) {
        super(driver, url, user, passwd);
        setupDB();
    }

    private void setupDB() {
        Connection connection = null;
        boolean hasDB = false;
        try {
            connection = getConnection();
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if (databaseName.equals(DB_NAME)) {
                    hasDB = true;
                    break;
                }
            }
            resultSet.close();

            if (!hasDB) {
                Statement stmt = connection.createStatement();
                if (stmt == null) {
                    System.out.println("Couldn't create statement");
                    connection.close();
                    return;
                }
                String sql = "CREATE DATABASE " + DB_NAME;
                stmt.executeUpdate(sql);
                stmt.close();
            }
            connection.setCatalog(DB_NAME);
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + KEY_STUDENT_ID
                    + " VARCHAR(11) NOT NULL, " + KEY_COURSE + " VARCHAR(18) NOT NULL, " + KEY_COMMENT
                    + " TEXT NOT NULL)";
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public void getComments(Student student) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setCatalog(DB_NAME);
            String query = "select * from " + TABLE_NAME + " where " + KEY_STUDENT_ID + " = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, student.id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String course = rs.getString(KEY_COURSE);
                String comment = rs.getString(KEY_COMMENT);
                student.comments.put(course, comment);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    public void updateComment(String id, String course, String comment) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setCatalog(DB_NAME);
            String query = "select * from " + TABLE_NAME + " where " + KEY_STUDENT_ID + " = ? and " +
                    KEY_COURSE + " = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, course);
            ResultSet rs = ps.executeQuery();
            boolean hasComment = rs.next();
            rs.close();
            ps.close();

            if (hasComment) {
                query = "update " + TABLE_NAME + " set " + KEY_COMMENT + " = ? where " + KEY_STUDENT_ID +
                        " = ? and " + KEY_COURSE + " = ?";
                ps = connection.prepareStatement(query);
                ps.setString(1, comment);
                ps.setString(2, id);
                ps.setString(3, course);
                ps.executeUpdate();
                ps.close();
            } else {
                query = "insert into " + TABLE_NAME + "(" + KEY_STUDENT_ID + ", " + KEY_COURSE + ", " +
                        KEY_COMMENT + ") values (?, ?, ?)";
                ps = connection.prepareStatement(query);
                ps.setString(1, id);
                ps.setString(2, course);
                ps.setString(3, comment);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
