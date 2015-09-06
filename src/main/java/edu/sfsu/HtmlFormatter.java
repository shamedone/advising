package edu.sfsu;

import edu.sfsu.db.Student;
import edu.sfsu.db.Course;

public class HtmlFormatter {
    final private static String[] CORE      = { "MATH 226", "MATH 227", "MATH 324", "MATH 325",
            "PHYS 220", "PHYS 230", "BIOL 100", "BIOL 210", "CSC 210", "CSC 220", "CSC 230",
            "CSC 256", "CSC 300GW", "CSC 340", "CSC 413", "CSC 415", "CSC 510", "CSC 600", "CSC 648" };
    final private static String[] ELECTIVES = { "CSC 520", "CSC 615", "CSC 620", "CSC 621",
            "CSC 630", "CSC 631", "CSC 637", "CSC 639", "CSC 641", "CSC 642", "CSC 644", "CSC 645",
            "CSC 648", "CSC 650", "CSC 651", "CSC 656", "CSC 658", "CSC 665", "CSC 667", "CSC 668",
            "CSC 675", "CSC 690", "CSC 693", "CSC 694", "CSC 695", "CSC 697", "CSC 699" };


    private static String formatSemester(String semester) {
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
        String html;
        year += semester.substring(1, 3);
        char lastDigit = semester.charAt(3);
        switch (lastDigit) {
        case '1':
            html = "Winter";
            break;
        case '3':
            html = "Spring";
            break;
        case '5':
            html = "Summer";
            break;
        case '7':
            html = "Fall";
            break;
        default:
            return "-";
        }
        html += " " + year;
        return html;
    }

    private static String generateClassList(Student student, String heading, String[] classes, boolean showMissing) {
        String html = "";
        html += "<h4>" + heading + "</h4>\n";
        html += "<table class=\"mdl-data-table mdl-js-data-table mdl-shadow--2dp\">\n";
        html += "<thead>\n";
        html += "<tr>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Course</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Semester</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Grade</th>\n";
        html += "</tr>\n";
        for (String clazz : classes) {
            Course course = student.requirementFor(clazz);
            if (course == null) {
                if (showMissing) {
                    html += "<tr>\n";
                    html += "<th class=\"mdl-data-table__cell--non-numeric\">" + clazz + "</th>\n";
                    html += "<th class=\"mdl-data-table__cell--non-numeric\"></th>\n";
                    html += "<th class=\"mdl-data-table__cell--non-numeric\"></th>\n";
                    html += "</tr>\n";
                }
                continue;
            }
            html += "<tr>\n";
            html += "<th class=\"mdl-data-table__cell--non-numeric\">" + clazz;
            if (course.transferred) {
                html += " (T)";
            }
            html += "</th>\n";
            html += "<th class=\"mdl-data-table__cell--non-numeric\">"
                    + formatSemester(course.semester) + "</th>\n";
            html += "<th class=\"mdl-data-table__cell--non-numeric\">" + course.grade + "</th>\n";
            html += "</tr>\n";
        }
        html += "</tbody>\n";
        html += "</table>\n";
        return html;
    }

    public static String generateHtml(Student student) {
        String html = "";
        html += "<h5>" + student.name + " (" + student.id + ") &lt;<a href=\"mailto:";
        html += student.email + "\">" + student.email + "</a>&gt;</h5>\n";

        html += generateClassList(student, "Core", CORE, true);
        html += generateClassList(student, "Electives", ELECTIVES, false);
        return html;
    }
}
