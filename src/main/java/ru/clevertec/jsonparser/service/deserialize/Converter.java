package ru.clevertec.jsonparser.service.deserialize;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Converter {

    private static final Map<Class<?>, Function<String, ?>> FUNCTION_TO_CONVERT = new HashMap<>();

    static {
        FUNCTION_TO_CONVERT.put(String.class, Function.identity());
        FUNCTION_TO_CONVERT.put(char.class, s -> s.charAt(0));
        FUNCTION_TO_CONVERT.put(Character.class, s -> s.charAt(0));
        FUNCTION_TO_CONVERT.put(byte.class, Byte::parseByte);
        FUNCTION_TO_CONVERT.put(Byte.class, Byte::valueOf);
        FUNCTION_TO_CONVERT.put(short.class, Short::parseShort);
        FUNCTION_TO_CONVERT.put(Short.class, Short::valueOf);
        FUNCTION_TO_CONVERT.put(int.class, Integer::parseInt);
        FUNCTION_TO_CONVERT.put(Integer.class, Integer::valueOf);
        FUNCTION_TO_CONVERT.put(long.class, Long::parseLong);
        FUNCTION_TO_CONVERT.put(Long.class, Long::valueOf);
        FUNCTION_TO_CONVERT.put(float.class, Float::parseFloat);
        FUNCTION_TO_CONVERT.put(Float.class, Float::valueOf);
        FUNCTION_TO_CONVERT.put(double.class, Double::parseDouble);
        FUNCTION_TO_CONVERT.put(Double.class, Double::valueOf);
        FUNCTION_TO_CONVERT.put(boolean.class, Boolean::parseBoolean);
        FUNCTION_TO_CONVERT.put(Boolean.class, Boolean::valueOf);
    }


    public static <T> T convert(String parameter, Class<T> type) {
        if (parameter == null) {
            return null;
        }
        Function<String, ?> stringFunction = FUNCTION_TO_CONVERT.get(type);
        return (T) stringFunction.apply(parameter);
    }
}
