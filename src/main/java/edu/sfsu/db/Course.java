package edu.sfsu.db;

import java.util.ArrayList;
import java.util.List;

public class Course {
    public String courseName;
    public String semester;
    public String transferSchool;
    public String transferSchoolType;
    public String transferCourse;
    public String grade;

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

    public boolean isPassingGrade() {
        if (PASSING_GRADES.contains(grade)) {
            return true;
        }
        // Gotta love those exceptions
        return courseName.equals("CSC 412") && "CR".equals(grade);
    }
}
