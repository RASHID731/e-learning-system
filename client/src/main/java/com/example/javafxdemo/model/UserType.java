package com.example.javafxdemo.model;

public enum UserType {
    DOZENT,
    STUDENT;

    @Override
    public String toString() {

        switch (this) {
            case DOZENT:
                return "Dozent";
            case STUDENT:
                return "Student";
            default:
                return super.toString();
        }
    }
}
