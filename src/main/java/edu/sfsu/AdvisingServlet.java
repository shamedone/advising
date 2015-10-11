package edu.sfsu;

import edu.sfsu.HtmlFormatter;
import edu.sfsu.db.CampusDB;
import edu.sfsu.db.CommentDB;
import edu.sfsu.db.Student;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AdvisingServlet extends HttpServlet {

    final private static String CAMPUS_DRIVER    = "oracle.jdbc.driver.OracleDriver";
    final private static String CAMPUS_URL       = "jdbc:oracle:thin:@//dbgrid-scan.sfsu.edu:1521/repl";
    final private static String CAMPUS_USERNAME  = "AASCIAPT";
    final private static String CAMPUS_PASSWD    = "welcome1$";

    /**
     * <pre>
     *     mysql -u root -p
     *     CREATE USER 'advisor'@'localhost' IDENTIFIED BY 'd4yY76s0wM1';
     *     GRANT ALL PRIVILEGES ON * . * TO 'advisor'@'localhost';
     *     FLUSH PRIVILEGES;
     * </pre>
     */
    final private static String COMMENT_DRIVER   = "com.mysql.jdbc.Driver";
    final private static String COMMENT_URL      = "jdbc:mysql://localhost:3307/";
    final private static String COMMENT_USERNAME = "advisor";
    final private static String COMMENT_PASSWD   = "d4yY76s0wM1";

    private CommentDB           commentDB;
    private CampusDB            campusDB;


    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            Class.forName(CAMPUS_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver missing");
            return;
        }

        try {
            Class.forName(COMMENT_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver missing");
            return;
        }

        commentDB = new CommentDB(COMMENT_DRIVER, COMMENT_URL, COMMENT_USERNAME, COMMENT_PASSWD);
        campusDB = new CampusDB(CAMPUS_DRIVER, CAMPUS_URL, CAMPUS_USERNAME, CAMPUS_PASSWD);

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String path = request.getServletPath();

        if (path.equals("/lookup-student")) {
            String id = request.getParameter("id");
            processLookupStudent(out, id);
        }

        if (path.equals("/update-comment")) {
            String id = request.getParameter("id");
            String course = request.getParameter("course");
            String comment = request.getParameter("comment");
            processUpdateComment(out, id, course, comment);
        }

        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    private void processLookupStudent(PrintWriter out, String id) throws IOException {
        Student student = campusDB.getStudent(id);
        String html;
        if (student != null) {
            commentDB.getComments(student);
            html = HtmlFormatter.generateHtml(student);
        } else {
            html = "<b>Student not found</b>";
        }
        out.println(html);
    }

    private void processUpdateComment(PrintWriter out, String id, String course, String comment) {
        commentDB.updateComment(id, course, comment);
    }
}
