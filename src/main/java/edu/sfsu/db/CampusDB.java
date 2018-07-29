package edu.sfsu.db;

import java.util.List;

public interface CampusDB {
    Student getStudent(String id);
    void getStudentInfo(List<Student> students);
}
