package de.johanneswirth.tac.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;

@JsonSerialize(using=ErrorSerializer.class)
public enum ErrorStatus implements IStatus {
    AUTHENTICATION_ERROR("AUTHENTICATION_ERROR"),
    USER_EXISTS("USER_EXISTS"),
    NO_ACCESS("STATUS_NO_ACCESS"),
    ILLEGAL_PARAMETERS("STATUS_ILLEGAL_PARAMETERS"),
    UNKOWN_GAME("UNKOWN_GAME"),

    MOVE_NOT_ALLOWED("MOVE_NOT_ALLOWED");

    @NotEmpty
    private String errorMessage;

    ErrorStatus(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    @JsonProperty("error")
    public boolean isError() {
        return true;
    }

    @JsonProperty("errorMessage")
    public String getErrorMessage() {
        return errorMessage;
    }
}
