package edu.sfsu;

import edu.sfsu.db.CampusDB;
import edu.sfsu.db.CommentDB;
import edu.sfsu.edu.sfsu.http.Server;

public class Main {

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
    final private static String COMMENT_URL      = "jdbc:mysql://localhost:3307/";
    final private static String COMMENT_USERNAME = "advisor";
    final private static String COMMENT_PASSWD   = "d4yY76s0wM1";


    public static void main(String[] argv) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver missing");
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver missing");
            return;
        }

        CommentDB commentDb = new CommentDB(COMMENT_URL, COMMENT_USERNAME, COMMENT_PASSWD);
        if (!commentDb.isConnected()) {
            System.out.println("Connection to CommentDB failed!");
            return;
        }

        CampusDB campusDb = new CampusDB(CAMPUS_URL, CAMPUS_USERNAME, CAMPUS_PASSWD);
        if (!campusDb.isConnected()) {
            System.out.println("Connection to CampusDB failed!");
            return;
        }

        Server server = new Server(campusDb, commentDb);
        server.start();
    }

}
