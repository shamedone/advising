package edu.sfsu.db;

public class Requirement {
    public Course passedCourse;
    public String gradeSequence;
    public String courseName;
    public String lastSem;


    public Requirement(String courseName) {
        this.passedCourse = null;
        this.gradeSequence = null;
        this.courseName = courseName;
    }

    public void addGrade(String grade) {
        if (gradeSequence == null) {
            gradeSequence = grade;
        } else {
            gradeSequence += "/" + grade;
        }
        this.lastSem = Util.formatSemester(Util.getCurrentSemester()); //records current semester as the semester attempted.
    }

    public void addGrade(Course c) {
        if (gradeSequence == null) {
            gradeSequence = c.grade;
        } else {
            gradeSequence += "/" + c.grade;
        }
        lastSem = c.semester; //record semester of most recent attempt for display.
    }

    public void setPassedCourse(Course c) {
        passedCourse = c;
        addGrade(c.grade);
        lastSem = c.semester;
    }
}
