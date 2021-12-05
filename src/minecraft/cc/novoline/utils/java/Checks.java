package cc.novoline.utils.java;


import java.util.Collection;

/**
 * @author xDelsy
 */
public final class Checks {

    private Checks() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void check(final boolean expression, final String message) {
        if (!expression) throw new IllegalArgumentException(message);
    }

    public static void check(final boolean expression, final String message, final Object... args) {
        if (!expression) throw new IllegalArgumentException(String.format(message, args));
    }

    public static void check(final boolean expression, final String message, final Object arg) {
        if (!expression) throw new IllegalArgumentException(String.format(message, arg));
    }

    public static void notNull(final Object argument, final String name) {
        if (argument == null) throw new IllegalArgumentException(name + " may not be null");
    }

    public static void notEmpty(final CharSequence argument, final String name) {
        notNull(argument, name);
        if (Helpers.isEmpty(argument)) throw new IllegalArgumentException(name + " may not be empty");
    }

    public static String notBlank(final String argument, final String name) {
        notNull(argument, name);
        if (Helpers.isBlank(argument)) throw new IllegalArgumentException(name + " may not be blank");

        return argument;
    }

    public static void noWhitespace(final CharSequence argument, final String name) {
        notNull(argument, name);
        if (Helpers.containsWhitespace(argument)) throw new IllegalArgumentException(name + " may not contain blanks");
    }

    public static void notEmpty(final Collection<?> argument, final String name) {
        notNull(argument, name);
        if (argument.isEmpty()) throw new IllegalArgumentException(name + " may not be empty");
    }

    public static void notEmpty(final Object[] argument, final String name) {
        notNull(argument, name);
        if (argument.length == 0) throw new IllegalArgumentException(name + " may not be empty");
    }

    public static void noneNull(final Collection<?> argument, final String name) {
        notNull(argument, name);
        argument.forEach(it -> notNull(it, name));
    }

    public static void noneNull(final Object[] argument, final String name) {
        notNull(argument, name);
        for (Object it : argument) {
            notNull(it, name);
        }
    }

    public static <T extends CharSequence> void noneEmpty(final Collection<T> argument, final String name) {
        notNull(argument, name);
        argument.forEach(it -> notEmpty(it, name));
    }

    public static void noneBlank(final Collection<String> argument, final String name) {
        notNull(argument, name);
        argument.forEach(it -> notBlank(it, name));
    }

    public static <T extends CharSequence> void noneContainBlanks(final Collection<T> argument, final String name) {
        notNull(argument, name);
        argument.forEach(it -> noWhitespace(it, name));
    }

    public static void positive(final int n, final String name) {
        if (n <= 0) throw new IllegalArgumentException(name + " may not be negative or zero");
    }

    public static void positive(final long n, final String name) {
        if (n <= 0) throw new IllegalArgumentException(name + " may not be negative or zero");
    }

    public static void notNegative(final int n, final String name) {
        if (n < 0) throw new IllegalArgumentException(name + " may not be negative");
    }

    public static void notNegative(final long n, final String name) {
        if (n < 0) throw new IllegalArgumentException(name + " may not be negative");
    }

}