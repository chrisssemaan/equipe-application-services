package com.football.handler;

import com.football.dtos.handlerDTOs.ApiError;
import com.football.dtos.handlerDTOs.PropertyApiError;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Controller exception handler.
 */
@ControllerAdvice(basePackages = "com.football.controller")
public class ControllerExceptionHandler {

    /**
     * Not found exception handler error message.
     *
     * @param ex the ex
     * @return the error message
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    public ApiError entityNotFoundExceptionHandler(EntityNotFoundException ex)
    {
        return ApiError.builder().status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage()).build();
    }

    /**
     * Property Reference Exception handler error message.
     *
     * @param ex the ex
     * @return the error message
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseBody
    public PropertyApiError propertyReferenceException(PropertyReferenceException ex)
    {
        return PropertyApiError.builder().status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .property(ex.getPropertyName()).build();
    }

    /**
     * Handle validation exceptions list.
     *
     * @param ex the ex
     * @return the list of errors
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public List<String> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> listErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            listErrors.add(fieldName + ": " + errorMessage);
        });
        return listErrors;
    }
}
