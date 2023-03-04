package ru.clevertec.jsonparser.service.deserialize;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.clevertec.jsonparser.service.deserialize.Converter.convert;
import static ru.clevertec.jsonparser.util.ParserUtil.capitalize;
import static ru.clevertec.jsonparser.util.ParserUtil.isPrimitiveOrString;

public class Deserializer {

    public static <T> T deserialize(Map<String, String> parsedMap, Class<T> clazz, String prefix) {
        try {
            T root = clazz.getDeclaredConstructor().newInstance();
            Field[] declaredFields = clazz.getDeclaredFields();

            for (Field field : declaredFields) {
                Class<?> type = field.getType();
                String fieldName = field.getName();
                Type genericType = field.getGenericType();
                Object value = parseField(parsedMap, type, fieldName, prefix, genericType);
                setField(value, field, root, clazz);
            }
            return root;
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T parseField(Map<String, String> parsedMap, Class<?> type, String fieldName, String prefix, Type genericType)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        if (isPrimitiveOrString(type)) {
            return parsePrimitiveField(fieldName, type, parsedMap, prefix);
        } else if (type.isAssignableFrom(List.class)) {
            return parseCollectionField(parsedMap, type, fieldName, prefix, genericType);
        } else if (type.isAssignableFrom(Map.class)) {
            return parseMapField(parsedMap, type, fieldName, prefix, genericType);
        } else {
            prefix = prefix == null
                    ? fieldName
                    : prefix + "." + fieldName;
            return (T) deserialize(parsedMap, type, prefix);
        }
    }

    private static <T> void setField(Object value, Field field, T root, Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> type = field.getType();
        String fieldName = field.getName();
        String setterName = "set" + capitalize(fieldName);
        Method method = clazz.getMethod(setterName, type);
        method.invoke(root, value);
    }


    private static <T> T parsePrimitiveField(String fieldName, Class<?> type, Map<String, String> parsedMap, String prefix) {
        String parameter = prefix == null
                ? getParameter(parsedMap, fieldName)
                : getParameter(parsedMap, prefix + "." + fieldName);
        return (T) convert(parameter, type);
    }

    private static <T> T parseCollectionField(Map<String, String> parsedMap, Class<?> type, String fieldName, String prefix, Type genericType)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

//        type.isInterface()
        //todo smth

        Collection collection = new ArrayList();
        prefix = prefix == null
                ? fieldName
                : prefix + "." + fieldName;

        ParameterizedType generic = (ParameterizedType) genericType;
        Class<?> internalType = (Class<?>) generic.getActualTypeArguments()[0];

        for (int i = 0; ; i++) {
            String internalFieldName = "[" + i + "]";
            boolean isKeyAbsent = isKeyAbsent(parsedMap, prefix, internalFieldName);
            if (isKeyAbsent) {
                break;
            }
            Object o = parseField(parsedMap, internalType, internalFieldName, prefix, null);
            collection.add(o);
        }
        return (T) collection;
    }

    private static <T> T parseMapField(Map<String, String> parsedMap, Class<?> type, String fieldName, String prefix, Type genericType) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Map map = new HashMap();

        String updatedPrefix = prefix == null
                ? fieldName
                : prefix + "." + fieldName;

        ParameterizedType generic = (ParameterizedType) genericType;
        Class<?> keyType = (Class<?>) generic.getActualTypeArguments()[0];
        Class<?> valueType = (Class<?>) generic.getActualTypeArguments()[1];

        for (Map.Entry<String, String> entry : parsedMap.entrySet()) {
            String entryKey = entry.getKey();
            if (entryKey.startsWith(updatedPrefix)) {
                String internalFieldName = entryKey.substring(updatedPrefix.length() + 1);
                Object key = convert(internalFieldName, keyType);
                Object value = parseField(parsedMap, valueType, internalFieldName, updatedPrefix, null);
                map.put(key, value);
            }
        }
        return (T) map;
    }

    private static String getParameter(Map<String, String> parsedMap, String key) {
        return parsedMap.get(key);
    }

    private static boolean isKeyAbsent(Map<String, String> parsedMap, String prefix, String internalFieldName) {
        return parsedMap.keySet().stream()
                .noneMatch(key -> key.startsWith(prefix + "." + internalFieldName));
    }
}
