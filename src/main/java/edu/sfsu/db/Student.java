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
    public List<Course>        courses;
    public Map<String, String> comments;


    public Course requirementFor(String courseName) {
        for (Course course : courses) {
            if (course.courseName.equals(courseName)) {
                if (course.grade.equals("F") || course.grade.equals("CR")
                        || course.grade.equals("W") || course.grade.equals("")) {
                    continue;
                }
                return course;
            }
        }
        return null;
    }
}
