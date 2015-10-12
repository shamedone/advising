package edu.sfsu.db;

public class DummyStudent extends Student {
    public DummyStudent(String id) {
        super(id);
        name = "John Doe";
        email = "doe@dummy.org";
    }
}
