package ru.clevertec.jsonparser.service.serialize;

import ru.clevertec.jsonparser.exception.JsonSerializingException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import static ru.clevertec.jsonparser.service.serialize.SerializeByType.serializeByType;
import static ru.clevertec.jsonparser.util.ParserUtil.capitalize;

public class Serializer {

    public static final String LEFT_FIGURE_BRACKET = "{";
    public static final String RIGHT_FIGURE_BRACKET = "}";
    public static final String DELIMITER_COMMA = ",";
    public static final String BOOLEAN_METHOD_PREFIX = "is";
    public static final String DEFAULT_METHOD_PREFIX = "get";

    public static String serialize(Object object) {
        Class<?> clazz = object.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        String serializedObject = Arrays.stream(declaredFields)
                .map(field -> applySerialization(field,clazz,object))
                .collect(Collectors.joining(DELIMITER_COMMA));
        return LEFT_FIGURE_BRACKET + serializedObject + RIGHT_FIGURE_BRACKET;
    }

    private static String applySerialization(Field field, Class<?> clazz, Object object){
        try {
            String name = field.getName();
            Class<?> type = field.getType();
            String prefix = type.isAssignableFrom(boolean.class)
                    ? BOOLEAN_METHOD_PREFIX
                    : DEFAULT_METHOD_PREFIX;
            String methodName = prefix + capitalize(name);
            Method method = clazz.getMethod(methodName);
            Object value = method.invoke(object);
            return serializeByType(name, type, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonSerializingException("Serialization exception", e);
        }
    }
}
