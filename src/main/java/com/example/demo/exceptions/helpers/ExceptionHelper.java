package com.example.demo.exceptions.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExceptionHelper {
    public static String extractDuplicateKeyDetails(String rootMsg) {
        // Regex to find "Key (column)=(value)"
        Pattern pattern = Pattern.compile("Key \\((.*?)\\)=\\((.*?)\\)");
        Matcher matcher = pattern.matcher(rootMsg);

        if (matcher.find()) {
            String column = matcher.group(1);
            String value = matcher.group(2);
            return "The " + column + " '" + value + "' already exists. Please use a different one.";
        }
        return "Duplicate entry detected.";
    }
}
