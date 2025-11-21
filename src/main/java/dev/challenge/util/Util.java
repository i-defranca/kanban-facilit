package dev.challenge.util;

import java.time.LocalDate;

public class Util {
    private Util() {}

    public static boolean empty(LocalDate date) {
        return date == null;
    }

    public static boolean filled(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean filled(LocalDate date) {
        return date != null;
    }

    public static boolean future(LocalDate date) {
        return date != null && date.isAfter(LocalDate.now());
    }

    public static boolean past(LocalDate date) {
        return date != null && date.isBefore(LocalDate.now());
    }

}
