package ru.clevertec.jsonparser.exception;

public class JsonDeserializingException extends RuntimeException {

    public JsonDeserializingException(String message) {
        super(message);
    }

    public JsonDeserializingException(String message, Throwable cause) {
        super(message, cause);
    }
}
