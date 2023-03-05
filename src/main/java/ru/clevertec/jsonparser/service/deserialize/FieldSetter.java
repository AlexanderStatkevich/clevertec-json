package ru.clevertec.jsonparser.service.deserialize;

import ru.clevertec.jsonparser.exception.JsonDeserializingException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static ru.clevertec.jsonparser.service.deserialize.Converter.convert;
import static ru.clevertec.jsonparser.util.ParserUtil.capitalize;
import static ru.clevertec.jsonparser.util.ParserUtil.isPrimitiveOrString;

class FieldSetter {
    public static <T> T deserialize(Map<String, String> parsedMap, Class<T> clazz) {
        return deserialize(parsedMap, clazz, null);
    }

    private static <T> T deserialize(Map<String, String> parsedMap, Class<T> clazz, String prefix) {
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
            throw new JsonDeserializingException("Error during object instantiation", e);
        }
    }

    private static <T> T parseField(Map<String, String> parsedMap, Class<?> type, String fieldName, String prefix, Type genericType) {
        if (isPrimitiveOrString(type)) {
            return parsePrimitiveField(fieldName, type, parsedMap, prefix);
        } else if (type.isAssignableFrom(List.class)) {
            return parseCollectionField(parsedMap, type, fieldName, prefix, genericType);
        } else if (type.isAssignableFrom(Map.class)) {
            return parseMapField(parsedMap, type, fieldName, prefix, genericType);
        } else {
            String updatedPrefix = getUpdatedPrefix(fieldName, prefix);
            return (T) deserialize(parsedMap, type, updatedPrefix);
        }
    }

    private static <T> void setField(Object value, Field field, T root, Class<T> clazz) {
        try {
            Class<?> type = field.getType();
            String fieldName = field.getName();
            String setterName = "set" + capitalize(fieldName);
            Method method = clazz.getMethod(setterName, type);
            method.invoke(root, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new JsonDeserializingException("Error during setting field", e);
        }
    }

    private static <T> T parsePrimitiveField(String fieldName, Class<?> type, Map<String, String> parsedMap, String prefix) {
        String parameter = prefix == null
                ? getParameter(parsedMap, fieldName)
                : getParameter(parsedMap, prefix + "." + fieldName);
        return (T) convert(parameter, type);
    }

    private static <T> T parseCollectionField(Map<String, String> parsedMap, Class<?> type, String fieldName, String prefix, Type genericType) {
        try {
            Collection collection = getCollection(type);
            String updatedPrefix = getUpdatedPrefix(fieldName, prefix);
            ParameterizedType generic = (ParameterizedType) genericType;
            Class<?> internalType = (Class<?>) generic.getActualTypeArguments()[0];
            int i = 0;
            while (true) {
                String internalFieldName = "[" + i + "]";
                boolean isKeyAbsent = isKeyAbsent(parsedMap, updatedPrefix, internalFieldName);
                if (isKeyAbsent) {
                    break;
                }
                Object value = parseField(parsedMap, internalType, internalFieldName, updatedPrefix, null);
                collection.add(value);
                i++;
            }
            return (T) collection;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 JsonDeserializingException e) {
            throw new JsonDeserializingException("Error during collection instantiation. Field name: " + fieldName, e);
        }
    }

    private static Collection getCollection(Class<?> type) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (type.isInterface()) {
            if (type.isAssignableFrom(List.class)) {
                return new ArrayList();
            }
            if (type.isAssignableFrom(Set.class)) {
                return new HashSet();
            }
            if (type.isAssignableFrom(Queue.class)) {
                return new ArrayDeque();
            }
            throw new JsonDeserializingException("Unknown collection type: " + type);
        } else {
            return (Collection) type.getDeclaredConstructor().newInstance();
        }
    }

    private static <T> T parseMapField(Map<String, String> parsedMap, Class<?> type, String fieldName, String prefix, Type genericType) {
        try {
            Map map = getMap(type);

            String updatedPrefix = getUpdatedPrefix(fieldName, prefix);
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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new JsonDeserializingException("Error during map instantiation. Field name: " + fieldName, e);
        }
    }

    private static Map getMap(Class<?> type) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (type.isInterface()) {
            return new HashMap();
        } else {
            return (Map) type.getDeclaredConstructor().newInstance();
        }
    }


    private static String getUpdatedPrefix(String fieldName, String prefix) {
        return prefix == null
                ? fieldName
                : prefix + "." + fieldName;
    }

    private static String getParameter(Map<String, String> parsedMap, String key) {
        return parsedMap.get(key);
    }

    private static boolean isKeyAbsent(Map<String, String> parsedMap, String prefix, String internalFieldName) {
        return parsedMap.keySet().stream()
                .noneMatch(key -> key.startsWith(prefix + "." + internalFieldName));
    }
}
