package edu.sfsu.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CommentDB extends DB {

    final static private String DB_NAME        = "ADVISING";
    final static private String TABLE_NAME     = "COMMENTS";

    final static private String KEY_STUDENT_ID = "STUDENT_ID";
    final static private String KEY_COURSE     = "COURSE";
    final static private String KEY_COMMENT    = "COMMENT";


    public CommentDB(String url, String user, String passwd) {
        super(url, user, passwd);
        if (isConnected()) {
            try {
                setupDB();
            } catch (SQLException e) {
                close();
            }
        }
    }

    private void setupDB() throws SQLException {
        boolean hasDB = false;
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
                close();
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
    }

    public void getComments(Student student) {
        String query = "select * from " + TABLE_NAME + " where " + KEY_STUDENT_ID + " = ?";
        try {
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
        }
    }

    public void updateComment(String id, String course, String comment) {
        try {
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
        }
    }
}
