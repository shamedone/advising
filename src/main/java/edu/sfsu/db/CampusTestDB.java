package edu.sfsu.db;

import java.util.List;

public class CampusTestDB extends DB implements CampusDB {

    public CampusTestDB(String driver, String url, String user, String passwd) {
    }

    public Student getStudent(String id) {
        if (!id.equals("123456789")) {
            return null;
        }
        Student student = new Student(id);
        student.id = "123456789";
        student.firstName = "John";
        student.lastName = "Doe";
        student.email = "john@doe.com";
        Course course = new Course();
        course.courseName = "CSC 415";
        course.grade = "A-";
        course.semester = "Spring 2018";
        student.courses.add(course);
        return student;
    }

    public void getStudentInfo(List<Student> students) {
    }

}
