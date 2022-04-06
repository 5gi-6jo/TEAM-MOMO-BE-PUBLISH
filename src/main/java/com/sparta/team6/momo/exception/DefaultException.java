package com.sparta.team6.momo.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

@Getter
@NoArgsConstructor
public class DefaultException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private HttpStatus httpStatus;
    private String message;
    private FieldError fieldError;
    private Exception exception;

    @Builder
    public DefaultException(HttpStatus httpStatus, String message, FieldError fieldError, Exception exception) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.fieldError = fieldError;
        this.exception = exception;
    }

    public static DefaultException fromFieldError(HttpStatus httpStatus, FieldError fieldError) {
        return DefaultException.builder()
                .httpStatus(httpStatus)
                .fieldError(fieldError)
                .build();
    }

    public static DefaultException fromException(HttpStatus httpStatus, String message, Exception e) {
        return DefaultException.builder()
                .httpStatus(httpStatus)
                .message(message)
                .exception(e)
                .build();
    }
}
