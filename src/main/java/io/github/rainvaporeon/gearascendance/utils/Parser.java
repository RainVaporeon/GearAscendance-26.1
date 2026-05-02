package io.github.rainvaporeon.gearascendance.utils;

import java.util.function.Supplier;

public class Parser {
    public static <T> T ignoring(Supplier<T> sup, T def) {
        try {
            return sup.get();
        } catch (Exception ignored) {
            return def;
        }
    }
}
