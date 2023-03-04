package ru.clevertec.jsonparser.util;

import java.util.Arrays;
import java.util.Set;

public class ParserUtil {

    private static final Set<Class<?>> primitiveAndStringClasses = Set.of(
            String.class,
            char.class, Character.class,
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            boolean.class, Boolean.class
    );

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static boolean isPrimitiveOrString(Class<?> clazz) {
        return primitiveAndStringClasses.contains(clazz);
    }

    public static boolean isAssignableFrom(Class<?> clazz, Class<?>... classes) {
        return Arrays.stream(classes)
                .anyMatch(cls -> cls.isAssignableFrom(clazz));
    }
}
