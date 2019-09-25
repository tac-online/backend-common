package de.johanneswirth.tac.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface IStatus<T> {

    @JsonProperty("error")
    boolean isError();
}
