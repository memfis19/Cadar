package io.github.memfis19.cadar.internal.utils;

/**
 * Cadar
 * Created by memfis on 12/8/16.
 * Copyright © 2016 Applikator.
 */

public final class WordUtils {

    private WordUtils() {

    }

    public static String capitalize(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

}
