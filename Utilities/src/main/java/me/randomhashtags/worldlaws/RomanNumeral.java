package me.randomhashtags.worldlaws;

import java.util.TreeMap;

public final class RomanNumeral {
    private final static TreeMap<Integer, String> INTEGERS;
    private final static TreeMap<String, Integer> STRINGS;
    static {
        INTEGERS = new TreeMap<>();
        INTEGERS.put(1000, "M");
        INTEGERS.put(900, "CM");
        INTEGERS.put(500, "D");
        INTEGERS.put(400, "CD");
        INTEGERS.put(100, "C");
        INTEGERS.put(90, "XC");
        INTEGERS.put(50, "L");
        INTEGERS.put(40, "XL");
        INTEGERS.put(10, "X");
        INTEGERS.put(9, "IX");
        INTEGERS.put(5, "V");
        INTEGERS.put(4, "IV");
        INTEGERS.put(1, "I");

        STRINGS = new TreeMap<>();
        STRINGS.put("CM", 900);
        STRINGS.put("M", 1000);
        STRINGS.put("CD", 400);
        STRINGS.put("D", 500);
        STRINGS.put("XC", 90);
        STRINGS.put("C", 100);
        STRINGS.put("XL", 40);
        STRINGS.put("L", 50);
        STRINGS.put("IX", 9);
        STRINGS.put("X", 10);
        STRINGS.put("IV", 4);
        STRINGS.put("V", 5);
        STRINGS.put("I", 1);
    }
    public static String toRoman(int number) {
        final int floored = INTEGERS.floorKey(number);
        return number == floored ? INTEGERS.get(number) : INTEGERS.get(floored) + toRoman(number-floored);
    }
    public static int fromRoman(String input) {
        int result = 0;
        for(String string : STRINGS.keySet()) {
            final int stringLength = string.length();
            while(input.contains(string)) {
                final int index = input.indexOf(string), value = STRINGS.get(string);
                input = input.substring(0, index) + input.substring(index + stringLength);
                result += value;
                if(input.isEmpty()) {
                    break;
                }
            }
        }
        return result;
    }
}
