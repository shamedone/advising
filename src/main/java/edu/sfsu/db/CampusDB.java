package edu.sfsu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CampusDB extends DB {

    public CampusDB(String driver, String url, String user, String passwd) {
        super(driver, url, user, passwd);
    }

    public Student getStudent(String id) {
        if (id.equals("0")) {
            return new DummyStudent(id);
        }
        Student student = new Student(id);
        Connection connection = null;
        try {
            connection = getConnection();
            // Courses taken at SFSU
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
                course.courseName = rs.getString("SUBJECT") + rs.getString("CATALOG_NBR");
                course.semester = formatSemester(rs.getString("STRM"));
                course.grade = rs.getString("CRSE_GRADE_OFF").replaceAll(" ", "");
                student.courses.add(course);
            }
            rs.close();
            ps.close();

            // Courses transferred
            query = "select DISTINCT D.EMPLID, C.CRSE_ID, C.SUBJECT SFSU_SUBJECT, C.CATALOG_NBR AS SFSU_NBR, C.DESCR, D.EXT_COURSE_NBR, D.CRSE_GRADE_OFF "
                    + ", O.EXT_ORG_ID, O.DESCR AS SCHOOLNAME "
                    + ", E.SCHOOL_SUBJECT, E.SCHOOL_CRSE_NBR "
                    + ", E.EXT_TERM, E.TERM_YEAR "
                    + "from CMSCOMMON.SFO_TRNS_CRSE_DTL D "
                    + ", CMSCOMMON.SFO_CLASS_TBL C "
                    + ", CMSCOMMON.SFO_EXT_ORG_TBL O "
                    + ", CMSCOMMON.SFO_EXT_COURSE_MV E "
                    + "where D.EMPLID = ? "
                    + "AND D.CRSE_ID = C.CRSE_ID "
                    + "AND D.ARTICULATION_TERM = C.STRM "
                    + "AND D.TRNSFR_SRC_ID = O.EXT_ORG_ID "
                    + "AND O.EFFDT = (SELECT MAX(EFFDT) FROM CMSCOMMON.SFO_EXT_ORG_TBL WHERE EXT_ORG_ID = O.EXT_ORG_ID) "
                    + "AND D.EMPLID = E.EMPLID(+) "
                    + "AND D.TRNSFR_SRC_ID = E.EXT_ORG_ID(+) "
                    + "AND D.EXT_COURSE_NBR = E.EXT_COURSE_NBR(+)";

            ps = connection.prepareStatement(query);
            ps.setString(1, id);
            rs = ps.executeQuery();

            while (rs.next()) {
                Course course = new Course();
                course.courseName = rs.getString("SFSU_SUBJECT") + rs.getString("SFSU_NBR");
                String term = rs.getString("EXT_TERM");
                String year = rs.getString("TERM_YEAR");
                course.semester = formatSemester(term, year);
                course.grade = rs.getString("CRSE_GRADE_OFF");
                course.transferSchool = rs.getString("SCHOOLNAME");
                String transferSubject = rs.getString("SCHOOL_SUBJECT");
                String transferCourse = rs.getString("SCHOOL_CRSE_NBR");
                if (transferCourse == null || transferSubject == null) {
                    course.transferCourse = "unknown";
                } else {
                    course.transferCourse = transferSubject + " " + transferCourse;
                }
                student.courses.add(course);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            return null;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
        return student.courses.size() == 0 ? null : student;
    }

    private String formatSemester(String semester) {
        if (semester.length() != 4) {
            return "-";
        }
        String year;
        char firstDigit = semester.charAt(0);
        switch (firstDigit) {
        case '1':
            year = "19";
            break;
        case '2':
            year = "20";
            break;
        default:
            return "-";
        }
        String term;
        year += semester.substring(1, 3);
        char lastDigit = semester.charAt(3);
        switch (lastDigit) {
        case '1':
            term = "Winter";
            break;
        case '3':
            term = "Spring";
            break;
        case '5':
            term = "Summer";
            break;
        case '7':
            term = "Fall";
            break;
        default:
            return "-";
        }
        return term + " " + year;
    }

    private String formatSemester(String term, String year) {
        if (term == null || year == null) {
            return "-";
        }
        String adjustedTerm = "";
        if (term.equals("SUMR")) {
            adjustedTerm = "Summer";
        } else if (term.equals("FALL")) {
            adjustedTerm = "Fall";
        } else if (term.equals("SPR")) {
            adjustedTerm = "Spring";
        } else if (term.equals("WINT")) {
            adjustedTerm = "Winter";
        }
        return adjustedTerm + " " + year;
    }
}
