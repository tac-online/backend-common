package de.johanneswirth.tac.common;

import java.util.List;

public class Status<T> {
    private boolean error;
    private T value;
    private String message;

    private Status(boolean error) {
        this.error = error;
    }

    private Status(T value) {
        this(false);
        this.value = value;
    }

    private Status(String message) {
        this(true);
        this.message = message;
    }

    private Status(String message, T value) {
        this(true);
        this.message = message;
        this.value = value;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static Status OK() {
        return new Status(null);
    }

    public static <T> Status<T> OK(T value) {
        return new Status<T>(value);
    }

    public static <T> Status<T[]> OK(T[] value) {
        return new Status<T[]>(value);
    }

    public static final Status MESSAGES_FAILED(List<String> recipients) {
        return new Status("MESSAGES_FAILED", recipients);
    }

    public static final Status AUTHENTICATION_ERROR = new Status("AUTHENTICATION_ERROR");
    public static final Status USER_EXISTS = new Status("USER_EXISTS");

    public static final Status UNKOWN_GAME = new Status("UNKNOWN_GAME");

    public static final Status NO_ACCESS = new Status("NO_ACCESS");

    public static final Status MOVE_NOT_ALLOWED = new Status("MOVE_NOT_ALLOWED");

    public static final Status ILLEGAL_PARAMETERS = new Status("STATUS_ILLEGAL_PARAMETERS");
}

