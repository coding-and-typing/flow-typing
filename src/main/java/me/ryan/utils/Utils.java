package me.ryan.utils;

public class Utils {
    public static String[] codepoint2Strings(int[] codepoints) {
        String[] strings = new String[codepoints.length];

        for (int i = 0; i < codepoints.length; i++) {
            String codepoint = new String(Character.toChars(codepoints[i]));
            strings[i] = codepoint;
        }

        return strings;
    }
}
