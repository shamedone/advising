package edu.sfsu.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CampusDB extends DB {

    private String currentSemester;

    public CampusDB(String driver, String url, String user, String passwd) {
        super(driver, url, user, passwd);

        currentSemester = Util.getCurrentSemester();
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
                String semester = rs.getString("STRM");
                String grade = rs.getString("CRSE_GRADE_OFF").replaceAll(" ", "");
                if (!grade.equals("") || semester.equals(currentSemester)) {
                    Course course = new Course();
                    course.courseName = rs.getString("SUBJECT") + rs.getString("CATALOG_NBR");
                    course.semester = Util.formatSemester(semester);
                    course.grade = grade;
                    student.courses.add(course);
                }
            }
            rs.close();
            ps.close();

            // Courses transferred
            query = "select DISTINCT D.EMPLID, C.CRSE_ID, C.SUBJECT SFSU_SUBJECT, C.CATALOG_NBR AS SFSU_NBR, C.DESCR, D.EXT_COURSE_NBR, D.CRSE_GRADE_OFF "
                    + ", O.EXT_ORG_ID, O.DESCR AS SCHOOLNAME "
                    + ", A.LS_SCHOOL_TYPE "
                    + ", E.SCHOOL_SUBJECT, E.SCHOOL_CRSE_NBR "
                    + ", E.EXT_TERM, E.TERM_YEAR "
                    + "from CMSCOMMON.SFO_TRNS_CRSE_DTL D "
                    + ", CMSCOMMON.SFO_CLASS_TBL C "
                    + ", CMSCOMMON.SFO_EXT_ORG_TBL O "
                    + ", CMSCOMMON.SFO_EXT_ORG_TBL_ADM A "
                    + ", CMSCOMMON.SFO_EXT_COURSE_MV E "
                    + "where D.EMPLID = ? "
                    + "AND D.CRSE_ID = C.CRSE_ID "
                    + "AND D.ARTICULATION_TERM = C.STRM "
                    + "AND D.TRNSFR_SRC_ID = O.EXT_ORG_ID "
                    + "AND O.EFFDT = (SELECT MAX(EFFDT) FROM CMSCOMMON.SFO_EXT_ORG_TBL WHERE EXT_ORG_ID = O.EXT_ORG_ID) "
                    + "AND D.TRNSFR_SRC_ID = A.EXT_ORG_ID "
                    + "AND A.EFFDT = (SELECT MAX(EFFDT) FROM CMSCOMMON.SFO_EXT_ORG_TBL_ADM WHERE EXT_ORG_ID = A.EXT_ORG_ID)"
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
                course.transferSchoolType = rs.getString("LS_SCHOOL_TYPE");
                String transferSubject = rs.getString("SCHOOL_SUBJECT");
                String transferCourse = rs.getString("SCHOOL_CRSE_NBR");
                if (transferCourse == null || transferSubject == null) {
                    course.transferCourse = "unknown";
                } else {
                    course.transferCourse = transferSubject + " " + transferCourse;
                }
                if (isValidTransfer(course)) {
                    student.courses.add(course);
                }
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
        return student.name.equals("") ? null : student;
    }

    public void getStudentInfo(List<Student> students) {
        Connection connection = null;
        try {
            connection = getConnection();
            for (Student student : students) {
                String query = "select * from CMSCOMMON.SFO_CR_MAIN_MV where emplid = ?";
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, student.id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    student.name = rs.getString("FIRST_NAME") + " " + rs.getString("LAST_NAME");
                    student.email = rs.getString("EMAIL_ADDR");
                }
                rs.close();
                ps.close();
            }
        } catch (SQLException e) {
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    /**
     * <pre>
     *     cs = SFSU class
     *     ct = Class, that is shown as equivalent to 'cs' in the transfer transcript
     * 
     *     IF cs is an upper division class AND
     *        ct is a lower division class AND
     *        institution where ct was taken is a 2-year college
     *        THEN course cannot be used as a graduation requirement
     * </pre>
     */
    private boolean isValidTransfer(Course course) {
        if (!course.transferSchoolType.equals("CC")) {
            // If originating school is not a Community College, it is assumed
            // not to be 2-year school.
            return true;
        }
        final String DIGIT_FILTER = "[^0-9]+";
        int cs = 0;
        int ct = 0;
        try {
            cs = Integer.parseInt(course.courseName.replaceAll(DIGIT_FILTER, ""));
            ct = Integer.parseInt(course.transferCourse.replaceAll(DIGIT_FILTER, ""));
        } catch (NumberFormatException ex) {
            // If the numbers cannot be parsed, assume it can be transferred
            return true;
        }
        return !(cs >= 300 && ct < 300);
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
