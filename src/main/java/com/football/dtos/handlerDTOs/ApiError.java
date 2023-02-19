package com.football.dtos.handlerDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * The type Api error.
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
public class ApiError {
    private int status;
    private String error;
    private String message;
}
