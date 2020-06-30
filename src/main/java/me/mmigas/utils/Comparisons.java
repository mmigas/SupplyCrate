package me.mmigas.utils;

public class Comparisons {
    /**
     * Private constructor to hide the implicit public one.
     */
    private Comparisons() {
        // Should be empty.
    }

    /**
     * Checks if an object equals one of other objects.
     *
     * @param object          the object to compare to the set of other objects.
     * @param possibleObjects the objects that the object can be.
     * @return true if the object equals one of the other objects.
     */
    public static boolean equalsOne(Object object, Object... possibleObjects) {
        for(Object possibleObject : possibleObjects) {
            if(object.equals(possibleObject)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a string starts with another string and ends with another.
     *
     * @param string the string to check.
     * @param start  the start to check.
     * @param end    the end to check.
     * @return true if the string starts and ends with the given strings.
     */
    public static boolean startsAndEnds(String string, String start, String end) {
        return string.startsWith(start) && string.endsWith(end);
    }
}
