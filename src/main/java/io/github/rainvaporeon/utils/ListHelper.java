package io.github.rainvaporeon.utils;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ListHelper {
    private static final Random R = new Random();

    public static <T> T randomElement(List<T> list, T bias, int biasFactor) {
        return pickRandom0(list, list.size(), bias, biasFactor, R);
    }

    private static <T> T pickRandom0(List<T> list, int size, T bias, int biasFactor, Random randInstance) {
        int idx = randInstance.nextInt(size);
        T obj = list.get(idx);
        if (bias == null || biasFactor <= 0) return obj;
        if (!Objects.equals(obj, bias)) {
            return pickRandom0(list, size, bias, biasFactor - 1, randInstance);
        }
        return obj;
    }
}
