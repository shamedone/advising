package edu.sfsu.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student {

    final static private List<String> PASSING_GRADES;

    static {
        PASSING_GRADES = new ArrayList<>();
        PASSING_GRADES.add("A");
        PASSING_GRADES.add("A-");
        PASSING_GRADES.add("B+");
        PASSING_GRADES.add("B");
        PASSING_GRADES.add("B-");
        PASSING_GRADES.add("C+");
        PASSING_GRADES.add("C");
        PASSING_GRADES.add("C-");
        PASSING_GRADES.add("D+");
        PASSING_GRADES.add("D");
        PASSING_GRADES.add("D-");
    }


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
    public List<Course>        courses;
    public Map<String, String> comments;


    public Course requirementFor(String courseName) {
        Course wip = null;
        for (Course course : courses) {
            if (course.courseName.equals(courseName)) {
                if (!PASSING_GRADES.contains(course.grade)) {
                    continue;
                }
                if (course.grade.equals("")) {
                    // Course taken this semester
                    wip = course;
                    continue;
                }
                return course;
            }
        }
        return wip;
    }
}
