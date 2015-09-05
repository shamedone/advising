package edu.sfsu.db;

import java.util.ArrayList;
import java.util.List;

public class Student {

    public Student(String id) {
        this.id = id;
        name = "";
        email = "";
        courses = new ArrayList<>();
    }


    public String       id;
    public String       name;
    public String       email;
    public List<Course> courses;


    public Course requirementFor(String courseName) {
        for (Course course : courses) {
            if (course.courseName.equals(courseName)) {
                if (course.transferred) {
                    return course;
                }
                if (course.grade.equals("F") || course.grade.equals("CR")) {
                    continue;
                }
                return course;
            }
        }
        return null;
    }
}
