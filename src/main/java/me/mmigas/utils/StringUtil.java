package me.mmigas.utils;

public final class StringUtil {

    private StringUtil() {
        // Should be empty.
    }

    public static String snakeCaseToReadable(String string) {
        StringBuilder converted = new StringBuilder();
        boolean wasLastCharSpace = true;
        for (int i = 0; i < string.length(); i++) {
            char currentChar = string.charAt(i);
            if (currentChar == ' ' || currentChar == '_') {
                wasLastCharSpace = true;
                currentChar = ' '; // In case it's '_'
            } else {
                if (wasLastCharSpace) {
                    currentChar = Character.toUpperCase(currentChar);
                } else {
                    currentChar = Character.toLowerCase(currentChar);
                }

                wasLastCharSpace = false;
            }

            converted.append(currentChar);
        }

        return converted.toString();
    }

    public static String withoutExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        }

        return fileName.substring(0, index);
    }
}
