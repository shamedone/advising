package edu.sfsu;

import edu.sfsu.db.Student;
import edu.sfsu.db.Course;

public class HtmlFormatter {
    final private static String[] CORE      = { "CSC 210", "CSC 212", "CSC 413", "CSC 415",
            "CSC 510", "CSC 600", "CSC 648" };
    final private static String[] ELECTIVES = { "CSC 520", "CSC 631" };


    private static String generateClassList(Student student, String heading, String[] classes) {
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
                continue;
            }
            html += "<tr>\n";
            html += "<th class=\"mdl-data-table__cell--non-numeric\">" + clazz;
            if (course.transferred) {
                html += " (T)";
            }
            html += "</th>\n";
            html += "<th class=\"mdl-data-table__cell--non-numeric\">" + course.semester
                    + "</th>\n";
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

        html += generateClassList(student, "Core", CORE);
        html += generateClassList(student, "Electives", ELECTIVES);
        return html;
    }
}
