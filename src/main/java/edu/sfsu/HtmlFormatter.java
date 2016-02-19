package edu.sfsu;

import edu.sfsu.db.Course;
import edu.sfsu.db.CourseRequirements;
import edu.sfsu.db.Student;

import java.util.ArrayList;
import java.util.List;

public class HtmlFormatter {

    final private static List<Object> transfers;

    static {
        transfers = new ArrayList<>();
        transfers.add("Transfer 1");
        transfers.add("Transfer 2");
        transfers.add("Transfer 3");
        transfers.add("Transfer 4");
        transfers.add("Transfer 5");
    }

    private static String generateCheckpoint(String studentId, String date, String checkpointDescr,
                                             String checkpointId, boolean showDateField) {
        String html = "";
        html += "<tr>";
        html += "<td class='mdl-data-table__cell--non-numeric'>";
        html += String
                .format("<label id='label_%s' class='mdl-switch mdl-js-switch mdl-js-ripple-effect' for='%s'",
                        checkpointId, checkpointId);

        if (!showDateField) {
            html += " style='display: none;'";
        }

        html += ">";
        html += String.format("<input type='checkbox' id='%s' class='mdl-switch__input' ",
                checkpointId);
        html += String.format("onclick=\"return checkpoint_toggled('%s', '%s');\"", studentId,
                checkpointId);
        if (date != null && !"".equals(date)) {
            html += " checked";
        }
        html += "/>";
        html += "<span class='mdl-switch__label'></span>";
        html += "</label>";
        html += "</td>";
        html += String.format("<td class='mdl-data-table__cell--non-numeric'>%s</td>",
                checkpointDescr);
        html += "<td class='mdl-data-table__cell--non-numeric'>";
        html += String.format("<form onsubmit=\"return update_checkpoints('%s');\">", studentId);
        html += "<div class='mdl-textfield mdl-js-textfield'>";
        html += String.format(
                "<input class='mdl-textfield__input' type='text' size='10' id='date_%s'",
                checkpointId);

        if (date != null && !"".equals(date)) {
            html += String.format(" value='%s'", e(date));
        }

        html += "/>";
        html += String.format("<label class='mdl-textfield__label' for='date_%s'>", checkpointId);
        html += showDateField ? "Date" : "Comment";
        html += "...</label>";
        html += "</div>";
        html += "</form>";
        html += "</td>";
        html += "</tr>";
        return html;
    }

    private static String generateGeneralSection(Student student) {
        String html = "<div class='general'>";
        html += "<h5>General</h5>";
        html += "<table class='mdl-data-table mdl-js-data-table mdl-shadow--2dp'>";
        html += "<thead>";
        html += "<tr>";
        html += "<th class='mdl-data-table__cell--non-numeric'></th>";
        html += "<th class='mdl-data-table__cell--non-numeric'>Checkpoint</th>";
        html += "<th class='mdl-data-table__cell--non-numeric'>Date/Comment</th>";
        html += "</tr>";
        html += "</thead>";
        html += "<tbody>";
        html += generateCheckpoint(student.id, student.checkpointOralPresentation,
                "Senior Oral Presentation", "oral_presentation", false);
        html += generateCheckpoint(student.id, student.checkpointAdvising413, "413 Advising",
                "advising_413", true);
        html += generateCheckpoint(student.id, student.checkpointSubmittedApplication,
                "Submitted Graduate Application", "submitted_appl", true);
        html += "</tbody>";
        html += "</table>";
        html += "</div>";
        return html;
    }

    private static String generateCommentField(Student student, String courseName) {
        String id = courseName.replace(" ", "_");
        String html = "<td class='mdl-data-table__cell--non-numeric'>";
        html += String.format("<form onsubmit=\"return update_comment('%s', '%s');\">", student.id,
                id);
        html += "<div class='mdl-textfield mdl-js-textfield full-width'>";
        html += String.format("<input class='mdl-textfield__input full-width' type='text' id='%s'",
                id);
        if (student.comments.containsKey(id)) {
            html += String.format(" value='%s'", e(student.comments.get(id)));
        }
        html += "/>";
        html += String
                .format("<label class='mdl-textfield__label' for='%s'>Comment...</label>", id);
        html += "</div>";
        html += "</form>";
        html += "</td>";
        return html;
    }

    private static String generateClassList(Student student, String heading, List<Object> classes,
                                            boolean showMissing, boolean showDetails) {
        String html = "";
        html += String.format("<h5>%s</h5>", heading);
        html += "<table class='mdl-data-table mdl-js-data-table mdl-shadow--2dp full-width'>";
        html += "<thead>";
        html += "<tr>";
        html += "<th class='mdl-data-table__cell--non-numeric'>Course</th>";
        if (showDetails) {
            html += "<th class='mdl-data-table__cell--non-numeric'>Transfer</th>";
            html += "<th class='mdl-data-table__cell--non-numeric'>Semester</th>";
            html += "<th class='mdl-data-table__cell--non-numeric'>Grade</th>";
        }
        html += "<th class='mdl-data-table__cell--non-numeric full-width'>Comment</th>";
        html += "</tr>";
        html += "</thead>";
        html += "<tbody>";
        for (Object clazz : classes) {
            Course course = student.requirementFor(clazz);
            if (course == null) {
                if (showMissing) {
                    String courseName = clazz instanceof String ? (String) clazz
                            : ((String[]) clazz)[0];
                    html += "<tr>";
                    html += String.format("<td class='mdl-data-table__cell--non-numeric'>%s</td>",
                            replaceSpaces(courseName));
                    if (showDetails) {
                        html += "<td class='mdl-data-table__cell--non-numeric'></td>";
                        html += "<td class='mdl-data-table__cell--non-numeric'></td>";
                        html += "<td class='mdl-data-table__cell--non-numeric'></td>";
                    }
                    html += generateCommentField(student, courseName);
                    html += "</tr>";
                }
                continue;
            }
            html += "<tr>";
            html += String.format("<td class='mdl-data-table__cell--non-numeric'>%s</td>",
                    replaceSpaces(course.courseName));
            if (showDetails) {
                html += "<td class='mdl-data-table__cell--non-numeric'>";
                if (course.transferCourse != null && course.transferSchool != null) {
                    html += String.format("%s (%s)", replaceSpaces(course.transferCourse),
                            replaceSpaces(course.transferSchool));
                }
                html += "</td>";
                html += String.format("<td class='mdl-data-table__cell--non-numeric'>%s</td>",
                        replaceSpaces(course.semester));
                html += String.format("<td class='mdl-data-table__cell--non-numeric'>%s</td>",
                        course.grade);
            }
            html += generateCommentField(student, course.courseName);
            html += "</tr>";
        }
        html += "</tbody>";
        html += "</table>";
        return html;
    }

    private static String replaceSpaces(String s) {
        return s.replaceAll(" ", "&nbsp;");
    }

    private static String e(String s) {
        return s.replace("'", "&#39;");
    }

    public static String generateHtml(Student student) {
        String html = "";
        html += String.format("<h4>%s (%s) &lt;<a href='mailto:%s'>%s</a>&gt;</h4>", student.name,
                student.id, student.email, student.email);

        html += generateGeneralSection(student);
        html += "<div class='vertical-padding'></div>";
        html += generateClassList(student, "Core", CourseRequirements.core, true, true);
        html += "<div class='vertical-padding'></div>";
        html += generateClassList(student, "Electives", CourseRequirements.electives, false, true);
        html += "<div class='vertical-padding'></div>";
        html += generateClassList(student, "Transfers", transfers, true, false);
        return html;
    }

    public static String generate413AdvisingList(List<Student> students) {
        String html = "<html><head><title>CSC 413 Advising List</title><link rel='stylesheet' href='material-print.css'></head><body>";
        html += "<table><thead>";
        html += "<th>Student ID</th>";
        html += "<th>Student Name</th>";
        html += "<th>Date</th>";
        html += "</thead><tbody>";
        for (Student student : students) {
            html += "<tr>";
            html += String.format("<td>%s</td>", student.id);
            html += String.format("<td>%s</td>", student.name);
            html += String.format("<td>%s</td>", student.checkpointAdvising413);
            html += "</tr>";
        }
        html += "</tbody></table>";
        html += "</body></html>";
        return html;
    }
}
