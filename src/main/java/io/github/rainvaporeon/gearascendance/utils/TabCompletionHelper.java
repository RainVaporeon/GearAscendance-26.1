package io.github.rainvaporeon.gearascendance.utils;

import java.util.List;

public class TabCompletionHelper {
    public static List<String> provideStarting(String partialInput, String... candidates) {
        return provideStarting(partialInput, List.of(candidates));
    }

    public static List<String> provideStarting(String partialInput, List<String> candidates) {
        return candidates.stream().sorted().filter(s -> s.startsWith(partialInput)).toList();
    }
}
