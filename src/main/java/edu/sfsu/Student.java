package edu.sfsu;

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
}
