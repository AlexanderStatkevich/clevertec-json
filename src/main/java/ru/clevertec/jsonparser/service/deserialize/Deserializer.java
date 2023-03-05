package ru.clevertec.jsonparser.service.deserialize;

import java.util.Map;

public class Deserializer {
    public static <T> T deserialize(String json, Class<T> clazz) {
        Map<String, String> stringStringMap = Parser.parseJsonIntoMap(json);
        return FieldSetter.deserialize(stringStringMap, clazz);
    }
}
