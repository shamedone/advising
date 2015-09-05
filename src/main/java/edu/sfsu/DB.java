package edu.sfsu;

import java.sql.*;

public class DB {

    private Connection connection;


    public DB(String url, String user, String passwd) {
        connection = null;

        try {
            connection = DriverManager.getConnection(url, user, passwd);
        } catch (SQLException e) {
            // Do nothing
        }
    }

    public boolean isConnected() {
        return connection != null;
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

    public Student getStudent(String id) {
        Student student = new Student(id);
        try {
            String query = "select * from CMSCOMMON.SFO_CR_MAIN_MV where emplid = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            boolean first = true;
            while (rs.next()) {
                if (first) {
                    first = false;
                    student.name = rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME");
                    student.email = rs.getString("EMAIL_ADDR");
                }
                Course course = new Course();
                course.course = rs.getString("SUBJECT") + rs.getString("CATALOG_NBR");
                course.semester = rs.getString("STRM");
                course.grade = rs.getString("CRSE_GRADE_OFF");
                course.transferred = false;
                student.courses.add(course);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            return null;
        }
        return student;
    }
}
