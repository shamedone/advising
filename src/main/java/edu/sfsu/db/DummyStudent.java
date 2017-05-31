package edu.sfsu.db;

public class DummyStudent extends Student {
    public DummyStudent(String id) {
        super(id);
        firstName = "John";
        lastName = "Doe";
        email = "doe@dummy.org";
    }
}
