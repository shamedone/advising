package edu.sfsu.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {

    public Student(String id) {
        this.id = id;
        name = "";
        email = "";
        courses = new ArrayList<>();
        comments = new HashMap<>();
    }


    public String              id;
    public String              name;
    public String              email;
    public String              checkpointOralPresentation;
    public String              checkpointAdvising413;
    public String              checkpointSubmittedApplication;
    public List<Course>        courses;
    public Map<String, String> comments;


    public Course requirementFor(Object course) {
        if (course instanceof String) {
            return requirementFor((String) course);
        }
        // List of alternative courses
        for (Object courseName : (List<Object>) course) {
            Course c = requirementFor((String) courseName);
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    private Course requirementFor(String courseName) {
        Course wip = null;
        for (Course course : courses) {
            if (course.courseName.equals(courseName)) {
                if (course.grade.equals("")) {
                    // Course taken this semester
                    wip = course;
                    continue;
                }
                if (!course.isPassingGrade()) {
                    continue;
                }
                return course;
            }
        }
        return wip;
    }
}
