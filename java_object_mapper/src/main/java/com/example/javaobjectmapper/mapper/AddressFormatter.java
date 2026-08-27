package com.example.javaobjectmapper.mapper;

public final class AddressFormatter {

    private AddressFormatter() {
    }

    public static String format(String city, String street, String postalCode) {
        return city + ", " + street + " (" + postalCode + ")";
    }
}
