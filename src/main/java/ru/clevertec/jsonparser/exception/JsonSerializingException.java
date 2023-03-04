package ru.clevertec.jsonparser.exception;

public class JsonSerializingException extends RuntimeException {
    public JsonSerializingException(String message, Throwable cause) {
        super(message, cause);
    }
}
