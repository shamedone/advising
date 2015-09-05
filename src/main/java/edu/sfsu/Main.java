package edu.sfsu;

import edu.sfsu.db.Course;
import edu.sfsu.db.DB;
import edu.sfsu.db.Student;

public class Main {

    final private static String URL      = "jdbc:oracle:thin:@//dbgrid-scan.sfsu.edu:1521/repl";
    final private static String USERNAME = "AASCIAPT";
    final private static String PASSWD   = "welcome1$";


    public static void main(String[] argv) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver missing");
            return;
        }

        DB db = new DB(URL, USERNAME, PASSWD);
        if (!db.isConnected()) {
            System.out.println("Connection failed!");
            return;
        }

        Student student = db.getStudent("913770590");

        if (student != null) {
            System.out.println(student.name + " <" + student.email + "> (" + student.id + ")");
            for (Course course : student.courses) {
                System.out.print("    ");
                System.out.print(course.transferred ? "* " : "  ");
                System.out.println(course.course + " (" + course.grade + ")");
            }
        } else {
            System.out.println("Student not found");
        }
        db.close();
    }

}
