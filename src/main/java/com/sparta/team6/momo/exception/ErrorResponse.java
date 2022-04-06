package com.sparta.team6.momo.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.team6.momo.exception.custom.NeedLoginException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(errorCode.getDetail())
                        .build()
                );
    }

    // 401 에러
    public static ResponseEntity<ErrorResponse> toResponseEntity(NeedLoginException e) {
        String wwwAuthenticateValue = "Go to login page and login";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", wwwAuthenticateValue)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error(HttpStatus.UNAUTHORIZED.name())
                        .code(e.getClass().getSimpleName())
                        .message(e.getMessage())
                        .build()
                );
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(DefaultException e) {
        if (e.getFieldError() != null)
            return fromFieldError(e);
        else if (e.getException() != null)
            return fromException(e);
        else
            return fromOnlyMessageError(e);
    }

    private static ResponseEntity<ErrorResponse> fromException(DefaultException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(e.getHttpStatus().value())
                        .error(e.getHttpStatus().name())
                        .code(e.getException().getClass().getSimpleName())
                        .message(e.getMessage())
                        .build()
                );
    }

    private static ResponseEntity<ErrorResponse> fromOnlyMessageError(DefaultException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(e.getHttpStatus().value())
                        .error(e.getHttpStatus().name())
                        .code(e.getClass().getSimpleName())
                        .message(e.getMessage())
                        .build()
                );
    }

    private static ResponseEntity<ErrorResponse> fromFieldError(DefaultException e) {
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(e.getHttpStatus().value())
                        .error(e.getHttpStatus().name())
                        .code(e.getFieldError().getField())
                        .message(e.getFieldError().getDefaultMessage())
                        .build()
                );
    }

}
