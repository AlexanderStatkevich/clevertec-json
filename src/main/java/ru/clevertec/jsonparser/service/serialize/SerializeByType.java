package ru.clevertec.jsonparser.service.serialize;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.clevertec.jsonparser.service.serialize.Serialize.serialize;
import static ru.clevertec.jsonparser.util.ParserUtil.isAssignableFrom;

public class SerializeByType {

    public static final String LEFT_FIGURE_BRACKET = "{";
    public static final String RIGHT_FIGURE_BRACKET = "}";
    public static final String COLON = ":";
    public static final String LEFT_STRAIGHT_BRACKET = "[";
    public static final String RIGHT_STRAIGHT_BRACKET = "]";

    protected static String serializeByType(String name, Class<?> type, Object value) {
        try {
            if (isAssignableFrom(type, boolean.class, Boolean.class)) {
                return serializeBoolean(name, value);
            }
            if (isAssignableFrom(type, byte.class, short.class, int.class, long.class, float.class, double.class, Number.class)) {
                return serializeNumber(name, value);
            }
            if (isAssignableFrom(type, String.class, char.class, Character.class)) {
                return serializeCharSequence(name, value);
            }
            if (isAssignableFrom(type, Collection.class)) {
                return serializeCollection(name, value);
            }
            if (isAssignableFrom(type, Map.class)) {
                return serializeMap(name, value);
            }
            return serializeObject(name, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    private static String serializeBoolean(String name, Object value) throws IllegalAccessException {
        return name == null
                ? value.toString()
                : "\"" + name + "\"" + COLON + value.toString();
    }

    private static String serializeNumber(String name, Object value) throws IllegalAccessException {
        return name == null
                ? value.toString()
                : "\"" + name + "\"" + COLON + value.toString();
    }

    private static String serializeCharSequence(String name, Object value) throws IllegalAccessException {
        return name == null
                ? "\"" + value.toString() + "\""
                : "\"" + name + "\"" + COLON + "\"" + value.toString() + "\"";
    }

    private static String serializeCollection(String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Collection<?> fieldValue = (Collection<?>) value;
        String serializedCollection = fieldValue.stream()
                .map(SerializeByType::serializeCollectionInternal)
                .collect(Collectors.joining(","));

        return "\"" + name + "\"" + COLON + LEFT_STRAIGHT_BRACKET + serializedCollection + RIGHT_STRAIGHT_BRACKET;
    }

    private static String serializeCollectionInternal(Object element) {
        Class<?> clazz = element.getClass();
        return serializeByType(null, clazz, element);
    }

    private static String serializeMap(String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Map<?, ?> fieldValue = (Map<?, ?>) value;
        String serializedMap = fieldValue.entrySet().stream()
                .map(element -> {
                    Object entryKey = element.getKey();
                    Object entryValue = element.getValue();
                    Class<?> clazz = entryValue.getClass();
                    return serializeByType(entryKey.toString(), clazz, entryValue);
                })
                .collect(Collectors.joining(","));

        return "\"" + name + "\"" + COLON + LEFT_FIGURE_BRACKET + serializedMap + RIGHT_FIGURE_BRACKET;
    }

    private static String serializeObject(String name, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String serialized = serialize(value);
        return name == null
                ? serialized
                : "\"" + name + "\"" + COLON + serialized;
    }
}
