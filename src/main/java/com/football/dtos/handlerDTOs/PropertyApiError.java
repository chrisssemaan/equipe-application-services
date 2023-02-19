package com.football.dtos.handlerDTOs;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * The type Property api error.
 */
@Getter
@Setter
@SuperBuilder
public class PropertyApiError extends ApiError {
    private String property;

    public PropertyApiError(int status, String error, String message, String property)
    {
        super(status, error, message);
        this.property = property;
    }
}
