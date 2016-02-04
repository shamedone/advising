package edu.sfsu;

import edu.sfsu.db.CampusDB;
import edu.sfsu.db.CheckpointDB;
import edu.sfsu.db.CommentDB;
import edu.sfsu.db.Student;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.servlet.*;

public class AdvisingServlet extends HttpServlet {

    private String password;
    private String token;

    private CommentDB    commentDB;
    private CheckpointDB checkpointDB;
    private CampusDB     campusDB;


    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        InputStream is = context.getResourceAsStream("/WEB-INF/advising.properties");
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            throw new ServletException(e.getMessage());
        }

        password = props.getProperty("PASSWORD");
        token = UUID.randomUUID().toString();

        String campusDriver = props.getProperty("CAMPUS_DRIVER");
        String campusUrl = props.getProperty("CAMPUS_URL");
        String campusUsername = props.getProperty("CAMPUS_USERNAME");
        String campusPasswd = props.getProperty("CAMPUS_PASSWD");

        String commentDriver = props.getProperty("COMMENT_DRIVER");
        String commentUrl = props.getProperty("COMMENT_URL");
        String commentUsername = props.getProperty("COMMENT_USERNAME");
        String commentPasswd = props.getProperty("COMMENT_PASSWD");

        String checkpointDriver = props.getProperty("CHECKPOINT_DRIVER");
        String checkpointUrl = props.getProperty("CHECKPOINT_URL");
        String checkpointUsername = props.getProperty("CHECKPOINT_USERNAME");
        String checkpointPasswd = props.getProperty("CHECKPOINT_PASSWD");

        try {
            Class.forName(campusDriver);
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver missing");
            return;
        }

        try {
            Class.forName(commentDriver);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver missing");
            return;
        }

        commentDB = new CommentDB(commentDriver, commentUrl, commentUsername, commentPasswd);
        checkpointDB = new CheckpointDB(checkpointDriver, checkpointUrl, checkpointUsername,
                checkpointPasswd);
        campusDB = new CampusDB(campusDriver, campusUrl, campusUsername, campusPasswd);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String path = request.getServletPath();

        if (path.equals("/login")) {
            String passwd = request.getParameter("passwd");
            processLogin(out, passwd);
            out.close();
            return;
        }

        String token = request.getParameter("token");
        if (!token.equals(this.token)) {
            out.close();
            return;
        }

        if (path.equals("/lookup-student")) {
            String id = request.getParameter("id");
            processLookupStudent(out, id);
        }

        if (path.equals("/update-comment")) {
            String id = request.getParameter("id");
            String course = request.getParameter("course");
            String comment = request.getParameter("comment");
            processUpdateComment(id, course, comment);
        }

        if (path.equals("/update-checkpoints")) {
            String id = request.getParameter("id");
            String checkpointOralPresentation = request.getParameter("checkpointOralPresentation");
            String checkpointAdvising413 = request.getParameter("checkpointAdvising413");
            String checkpointSubmittedAppl = request.getParameter("checkpointSubmittedAppl");
            processUpdateCheckpoints(id, checkpointOralPresentation, checkpointAdvising413,
                    checkpointSubmittedAppl);
        }

        if (path.equals("/generate-413-list")) {
            process413AdvisingList(out);
        }
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    private void processLogin(PrintWriter out, String passwd) {
        out.print(passwd.equals(password) ? token : "");
    }

    private void processLookupStudent(PrintWriter out, String id) throws IOException {
        Student student = campusDB.getStudent(id);
        String html;
        if (student != null) {
            commentDB.getComments(student);
            checkpointDB.getCheckpoints(student);
            html = HtmlFormatter.generateHtml(student);
        } else {
            html = "<b>Student not found</b>";
        }
        out.println(html);
    }

    private void processUpdateComment(String id, String course, String comment) {
        commentDB.updateComment(id, course, comment);
    }

    private void processUpdateCheckpoints(String id, String checkpointOralPresentation,
            String checkpointAdvising413, String checkpointSubmittedApplication) {
        checkpointDB.updateCheckpoints(id, checkpointOralPresentation, checkpointAdvising413,
                checkpointSubmittedApplication);
    }

    private void process413AdvisingList(PrintWriter out) {
        List<Student> students = checkpointDB.generate413AdvisingList();
        campusDB.getStudentInfo(students);
        String html = HtmlFormatter.generate413AdvisingList(students);
        out.write(html);
    }
}
