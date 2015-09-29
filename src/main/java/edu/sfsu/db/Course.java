package edu.sfsu.db;

import java.util.ArrayList;
import java.util.List;

public class Course {
    public String                     courseName;
    public String                     semester;
    public String                     transferSchool;
    public String                     transferCourse;
    public String                     grade;

    final private static String[]     BIOLOGY_CORE = { "BIOL 100", "BIOL 101", "BIOL 176",
            "BIOL 210", "CHEM 115"                };

    final public static Object[]      CORE         = { "MATH 226", "MATH 227", "MATH 324",
            "MATH 325", "PHYS 220", "PHYS 222", "PHYS 230", "PHYS 232", BIOLOGY_CORE, "CSC 210",
            "CSC 220", "CSC 230", "CSC 256", "CSC 300GW", "CSC 340", "CSC 413", "CSC 415",
            "CSC 510", "CSC 600", "CSC 648"       };

    final public static String[]      ELECTIVES    = { "CSC 520", "CSC 615", "CSC 620", "CSC 621",
            "CSC 630", "CSC 631", "CSC 637", "CSC 639", "CSC 641", "CSC 642", "CSC 644", "CSC 645",
            "CSC 650", "CSC 651", "CSC 656", "CSC 658", "CSC 665", "CSC 667", "CSC 668",
            "CSC 675",
            "CSC 690",
            "CSC 697",
            "CSC 699",
            "MATH 400",
            // Graduate courses
            "CSC 720", "CSC 730", "CSC 737", "CSC 745", "CSC 746", "CSC 775", "CSC 780", "CSC 810",
            "CSC 815", "CSC 820", "CSC 821", "CSC 825", "CSC 830", "CSC 831", "CSC 835", "CSC 837",
            "CSC 840", "CSC 841", "CSC 842", "CSC 845", "CSC 846", "CSC 848", "CSC 849", "CSC 850",
            "CSC 856", "CSC 857", "CSC 858", "CSC 864", "CSC 865", "CSC 867", "CSC 868", "CSC 869",
            "CSC 870", "CSC 871", "CSC 872", "CSC 875", "CSC 878", "CSC 890", "CSC 893", "CSC 895",
            "CSC 897", "CSC 898", "CSC 899"       };

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
        return PASSING_GRADES.contains(grade);
    }
}
