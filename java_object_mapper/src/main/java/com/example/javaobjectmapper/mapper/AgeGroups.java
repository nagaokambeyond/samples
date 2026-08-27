package com.example.javaobjectmapper.mapper;

public final class AgeGroups {

    private AgeGroups() {
    }

    public static String from(Integer age) {
        if (age == null) {
            return "unknown";
        }
        if (age < 20) {
            return "teen";
        }
        if (age < 35) {
            return "young-adult";
        }
        if (age < 55) {
            return "adult";
        }
        return "senior";
    }
}
