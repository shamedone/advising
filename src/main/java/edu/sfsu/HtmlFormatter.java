package edu.sfsu;

import edu.sfsu.db.Course;
import edu.sfsu.db.Student;

import java.util.List;

public class HtmlFormatter {

    private static String generateCheckpoint(String studentId, String date, String checkpointDescr,
            String checkpointId, boolean showDateField) {
        String html = "";
        html += "<tr>\n";
        html += "<td class=\"mdl-data-table__cell--non-numeric\">\n";
        html += "<label id=\"label_" + checkpointId
                + "\" class=\"mdl-switch mdl-js-switch mdl-js-ripple-effect\" for=\""
                + checkpointId + "\"";

        if (!showDateField) {
            html += " style=\"display: none;\"";
        }

        html += ">\n";
        html += "<input type=\"checkbox\" id=\"" + checkpointId + "\" class=\"mdl-switch__input\" ";
        html += "onclick=\"return checkpoint_toggled('" + studentId + "', '" + checkpointId
                + "');\"\n";
        if (date != null && !"".equals(date)) {
            html += " checked";
        }
        html += "/>\n";
        html += "<span class=\"mdl-switch__label\"></span>\n";
        html += "</label>\n";
        html += "</td>\n";
        html += "<td class=\"mdl-data-table__cell--non-numeric\">" + checkpointDescr + "</td>\n";
        html += "<td class=\"mdl-data-table__cell--non-numeric\">\n";
        html += "<form onsubmit=\"return update_checkpoints('" + studentId + "');\">\n";
        html += "<div class=\"mdl-textfield mdl-js-textfield\">\n";
        html += "<input class=\"mdl-textfield__input\" type=\"text\" size=\"10\" id=\"date_"
                + checkpointId + "\"";

        if (date != null && !"".equals(date)) {
            html += " value=\"" + date + "\"";
        }

        html += "/>\n";
        html += "<label class=\"mdl-textfield__label\" for=\"date_" + checkpointId
                + "\">";
        html += showDateField ? "Date" : "Comment";
        html += "...</label>\n";
        html += "</div>\n";
        html += "</form>\n";
        html += "</td>\n";
        html += "</tr>\n";
        return html;
    }

    private static String generateGeneralSection(Student student) {
        String html = "<div class=\"general\">\n";
        html += "<h5>General</h5>\n";
        html += "<table class=\"mdl-data-table mdl-js-data-table mdl-shadow--2dp\">\n";
        html += "<thead>\n";
        html += "<tr>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\"></th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Checkpoint</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Date/Comment</th>\n";
        html += "</tr>\n";
        html += "</thead>\n";
        html += "<tbody>\n";
        html += generateCheckpoint(student.id, student.checkpointOralPresentation,
                "Senior Oral Presentation", "oral_presentation", false);
        html += generateCheckpoint(student.id, student.checkpointAdvising413, "413 Advising",
                "advising_413", true);
        html += generateCheckpoint(student.id, student.checkpointSubmittedApplication,
                "Submitted Graduate Application", "submitted_appl", true);
        html += "</tbody>\n";
        html += "</table>\n";
        html += "</div>\n";
        return html;
    }

    private static String generateCommentField(Student student, String courseName) {
        String id = courseName.replace(" ", "_");
        String html = "<td class=\"mdl-data-table__cell--non-numeric\">\n";
        html += "<form onsubmit=\"return update_comment('" + student.id + "', '" + id + "');\">\n";
        html += "<div class=\"mdl-textfield mdl-js-textfield full-width\">\n";
        html += "<input class=\"mdl-textfield__input full-width\" type=\"text\" id=\"" + id + "\"";
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

    private static String generateClassList(Student student, String heading, Object[] classes,
            boolean showMissing) {
        String html = "";
        html += "<h5>" + heading + "</h5>\n";
        html += "<table class=\"mdl-data-table mdl-js-data-table mdl-shadow--2dp full-width\">\n";
        html += "<thead>\n";
        html += "<tr>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Course</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Transfer</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Semester</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric\">Grade</th>\n";
        html += "<th class=\"mdl-data-table__cell--non-numeric full-width\">Comment</th>\n";
        html += "</tr>\n";
        html += "</thead>\n";
        html += "<tbody>\n";
        for (Object clazz : classes) {
            Course course = student.requirementFor(clazz);
            if (course == null) {
                if (showMissing) {
                    String courseName = clazz instanceof String ? (String) clazz
                            : ((String[]) clazz)[0];
                    html += "<tr>\n";
                    html += "<td class=\"mdl-data-table__cell--non-numeric\">" + replaceSpaces(courseName)
                            + "</td>\n";
                    html += "<td class=\"mdl-data-table__cell--non-numeric\"></td>\n";
                    html += "<td class=\"mdl-data-table__cell--non-numeric\"></td>\n";
                    html += "<td class=\"mdl-data-table__cell--non-numeric\"></td>\n";
                    html += generateCommentField(student, courseName);
                    html += "</tr>\n";
                }
                continue;
            }
            html += "<tr>\n";
            html += "<td class=\"mdl-data-table__cell--non-numeric\">" + replaceSpaces(course.courseName);
            html += "</td>\n";
            html += "<td class=\"mdl-data-table__cell--non-numeric\">";
            if (course.transferCourse != null && course.transferSchool != null) {
                html += course.transferCourse + " (" + course.transferSchool + ")";
            }
            html += "</td>\n";
            html += "<td class=\"mdl-data-table__cell--non-numeric\">" + replaceSpaces(course.semester)
                    + "</td>\n";
            html += "<td class=\"mdl-data-table__cell--non-numeric\">" + course.grade + "</td>\n";
            html += generateCommentField(student, course.courseName);
            html += "</tr>\n";
        }
        html += "</tbody>\n";
        html += "</table>\n";
        return html;
    }

    private static String replaceSpaces(String s) {
        return s.replaceAll(" ", "&nbsp;");
    }

    public static String generateHtml(Student student) {
        String html = "";
        html += "<h4>" + student.name + " (" + student.id + ") &lt;<a href=\"mailto:";
        html += student.email + "\">" + student.email + "</a>&gt;</h4>\n";

        html += generateGeneralSection(student);
        html += "<p>&nbsp;</p>";
        html += generateClassList(student, "Core", Course.CORE, true);
        html += "<p>&nbsp;</p>";
        html += generateClassList(student, "Electives", Course.ELECTIVES, false);
        return html;
    }

    public static String generate413AdvisingList(List<Student> students) {
        String html = "<html><head><title>CSC 413 Advising List</title><link rel=\"stylesheet\" href=\"material-print.css\"></head><body>";
        html += "<table><thead>";
        html += "<th>Student ID</th>";
        html += "<th>Student Name</th>";
        html += "<th>Date</th>";
        html += "</thead><tbody>";
        for (Student student : students) {
            html += "<tr>";
            html += "<td>" + student.id + "</td>";
            html += "<td>" + student.name + "</td>";
            html += "<td>" + student.checkpointAdvising413 + "</td>";
            html += "</tr>";
        }
        html += "</tbody></table>";
        html += "</body></html>";
        return html;
    }
}
