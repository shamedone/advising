package edu.sfsu;

import edu.sfsu.db.Student;
import edu.sfsu.db.Course;

public class HtmlFormatter {

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

    private static String generateCommentField(Student student, String courseName) {
        String id = courseName.replace(" ", "_");
        String html = "<td class=\"mdl-data-table__cell--non-numeric\">\n";
        html += "<form onsubmit=\"return update_comment('" + student.id + "', '" + id + "');\">\n";
        html += "<div class=\"mdl-textfield mdl-js-textfield\">\n";
        html += "<input class=\"mdl-textfield__input\" type=\"text\" id=\"" + id + "\"";
        if (student.comments.containsKey(id)) {
            html += " value=\"" + student.comments.get(id) + "\"";
        }
        html += "/>\n";
        html += "<label class=\"mdl-textfield__label\" for=\"" + id + "\">Comment...</label>\n";
        html += "</div>\n";
        html += "</form>\n";
        html += "</td>\n";
        return html;
    }

    private static String generateClassList(Student student, String heading, String[] classes,
            boolean showMissing) {
        String html = "";
        html += "<h4>" + heading + "</h4>\n";
        html += "<table class=\"mdl-data-table mdl-js-data-table mdl-shadow--2dp\">\n";
        html += "<thead>\n";
        html += "<tr>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Course</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Semester</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Grade</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Comment</th>\n";
        html += "</tr>\n";
        html += "</thead>\n";
        html += "<tbody>\n";
        for (String clazz : classes) {
            Course course = student.requirementFor(clazz);
            if (course == null) {
                if (showMissing) {
                    html += "<tr>\n";
                    html += "<td class=\"mdl-data-table__cell--non-numeric\">" + clazz + "</td>\n";
                    html += "<td class=\"mdl-data-table__cell--non-numeric\"></td>\n";
                    html += "<td class=\"mdl-data-table__cell--non-numeric\"></td>\n";
                    html += generateCommentField(student, clazz);
                    html += "</tr>\n";
                }
                continue;
            }
            html += "<tr>\n";
            html += "<td class=\"mdl-data-table__cell--non-numeric\">" + clazz;
            if (course.transferred) {
                html += " (T)";
            }
            html += "</td>\n";
            html += "<td class=\"mdl-data-table__cell--non-numeric\">"
                    + formatSemester(course.semester) + "</td>\n";
            html += "<td class=\"mdl-data-table__cell--non-numeric\">" + course.grade + "</td>\n";
            html += generateCommentField(student, course.courseName);
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

        html += generateClassList(student, "Core", Course.CORE, true);
        html += generateClassList(student, "Electives", Course.ELECTIVES, false);
        return html;
    }
}
