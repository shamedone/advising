package edu.sfsu;

import edu.sfsu.db.Course;
import edu.sfsu.db.DB;
import edu.sfsu.db.Student;
import edu.sfsu.edu.sfsu.http.Server;

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

        Server server = new Server(db);
        server.start();
    }

}
