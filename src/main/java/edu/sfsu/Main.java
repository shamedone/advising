package edu.sfsu;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class Main {

    public static void main(String[] argv) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC Driver missing");
            return;
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@//dbgrid-scan.sfsu.edu:1521/repl", "AASCIAPT", "welcome1$");
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection == null) {
            System.out.println("Failed to make connection!");
            return;
        }

        try {
            String query = "select * from CMSCOMMON.SFO_CR_MAIN_MV where emplid = '913770590'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String studentId = rs.getString("EMPLID");
                String firstName = rs.getString("FIRST_NAME");
                String lastName = rs.getString("LAST_NAME");
                String emailAddr = rs.getString("EMAIL_ADDR");
                String subject = rs.getString("SUBJECT");
                String catalogNr = rs.getString("CATALOG_NBR");

                System.out.println(firstName + " " + lastName + " <" + emailAddr + "> ("
                        + studentId + "): " + subject + catalogNr);
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
