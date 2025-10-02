package com.example.javafxdemo.model;

public enum AbgabeStatus {
    ABGEGEBEN,
    NICHT_ABGEGEBEN;

    @Override
    public String toString() {
        switch (this) {
            case ABGEGEBEN:
                return "abgegeben";
            case NICHT_ABGEGEBEN:
                return "nicht abgegeben";
            default:
                return super.toString();
        }
    }
}
