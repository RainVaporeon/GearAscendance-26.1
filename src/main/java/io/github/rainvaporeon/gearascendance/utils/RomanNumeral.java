package io.github.rainvaporeon.gearascendance.utils;

import java.util.Arrays;
import java.util.Iterator;

public enum RomanNumeral {
    M(1000),
    CM(900),
    D(500),
    CD(400),
    C(100),
    XC(90),
    L(50),
    XL(40),
    X(10),
    IX(9),
    V(5),
    IV(4),
    I(1)
    ;

    private final int value;

    RomanNumeral(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return this.name();
    }

    public static String toRomanNumerals(int number) {
        // highest representable is 3999 as the largest unit is M (1000)
        if (number >= 4000 || number <= 0) return String.valueOf(number);
        StringBuilder builder = new StringBuilder();
        Iterator<RomanNumeral> it = Arrays.stream(RomanNumeral.values()).iterator();
        while (it.hasNext()) {
            RomanNumeral numeral = it.next();
            while (number >= numeral.value()) {
                builder.append(numeral);
                number -= numeral.value();
            }
        }
        return builder.toString();
    }
}
