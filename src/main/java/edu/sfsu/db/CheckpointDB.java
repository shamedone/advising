package edu.sfsu.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CheckpointDB extends DB {

    final static private String DB_NAME               = "ADVISING";
    final static private String TABLE_NAME            = "CHECKPOINTS";
    final static private String KEY_STUDENT_ID        = "STUDENT_ID";
    final static private String KEY_STUDENT_NAME      = "STUDENT_NAME";
    final static private String KEY_STUDENT_EMAIL     = "STUDENT_EMAIL";
    final static private String KEY_ORAL_PRESENTATION = "ORAL_PRESENTATION";
    final static private String KEY_ADVISING_413      = "ADVISING_413";
    final static private String KEY_SUBMITTED_APPL    = "SUBMITTED_APPL";


    public CheckpointDB(String driver, String url, String user, String passwd) {
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
                    + " VARCHAR(11) NOT NULL PRIMARY KEY, " + KEY_STUDENT_NAME + " TEXT, "
                    + KEY_STUDENT_EMAIL + " TEXT, " + KEY_ORAL_PRESENTATION + " TEXT, "
                    + KEY_ADVISING_413 + " TEXT, " + KEY_SUBMITTED_APPL + " TEXT)";
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

    public void getCheckpoints(Student student) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setCatalog(DB_NAME);
            String query = "select * from " + TABLE_NAME + " where " + KEY_STUDENT_ID + " = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, student.id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                student.checkpointOralPresentation = rs.getString(KEY_ORAL_PRESENTATION);
                student.checkpointAdvising413 = rs.getString(KEY_ADVISING_413);
                student.checkpointSubmittedApplication = rs.getString(KEY_SUBMITTED_APPL);
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

    public void updateCheckpoints(String id, String studentName, String studentEmail,
            String checkpointOralPresentation, String checkpointAdvising413,
            String checkpointSubmittedApplication) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setCatalog(DB_NAME);
            String query = "insert into " + TABLE_NAME + "(" + KEY_STUDENT_ID + ", "
                    + KEY_STUDENT_NAME + ", " + KEY_STUDENT_EMAIL + ", " + KEY_ORAL_PRESENTATION
                    + ", " + KEY_ADVISING_413 + ", " + KEY_SUBMITTED_APPL
                    + ") values (?, ?, ?, ?, ?, ?) on duplicate key update ";
            query += KEY_STUDENT_NAME + " = values(" + KEY_STUDENT_NAME + "), ";
            query += KEY_STUDENT_EMAIL + " = values(" + KEY_STUDENT_EMAIL + "), ";
            query += KEY_ORAL_PRESENTATION + " = values(" + KEY_ORAL_PRESENTATION + "), ";
            query += KEY_ADVISING_413 + " = values(" + KEY_ADVISING_413 + "), ";
            query += KEY_SUBMITTED_APPL + " = values(" + KEY_SUBMITTED_APPL + ")";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, studentName);
            ps.setString(3, studentEmail);
            ps.setString(4, checkpointOralPresentation);
            ps.setString(5, checkpointAdvising413);
            ps.setString(6, checkpointSubmittedApplication);
            ps.executeUpdate();
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

    public List<Student> generateList(String type) {
        List<Student> list = new ArrayList<>();
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setCatalog(DB_NAME);
            String query = "select * from " + TABLE_NAME;
            if (type.equals("413")) {
                query += " where " + KEY_ADVISING_413 + " <> ''";
            }
            if (type.equals("graduated")) {
                query += " where " + KEY_SUBMITTED_APPL + " <> ''";
            }
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Student student = new Student(rs.getString(KEY_STUDENT_ID));
                student.name = rs.getString(KEY_STUDENT_NAME);
                student.email = rs.getString(KEY_STUDENT_EMAIL);
                student.checkpointOralPresentation = rs.getString(KEY_ORAL_PRESENTATION);
                student.checkpointAdvising413 = rs.getString(KEY_ADVISING_413);
                student.checkpointSubmittedApplication = rs.getString(KEY_SUBMITTED_APPL);
                list.add(student);
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
        return list;
    }
}
