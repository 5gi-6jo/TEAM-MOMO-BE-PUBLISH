package com.sparta.team6.momo.exception;

import com.sparta.team6.momo.exception.custom.NeedLoginException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.sparta.team6.momo.exception.ErrorCode.DUPLICATE_RESOURCE;
import static com.sparta.team6.momo.exception.ErrorCode.FILE_SIZE_EXCEED;

//@ControllerAdvice 는 프로젝트 전역에서 발생하는 모든 예외를 잡아줍니다.
//@ExceptionHandler 는 발생한 특정 예외를 잡아서 하나의 메소드에서 공통 처리해줄 수 있게 해줍니다.
//둘을 같이 사용하면 모든 예외를 잡은 후에 Exception 종류별로 메소드를 공통 처리할 수 있습니다.

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {MissingRequestCookieException.class})
    protected ResponseEntity<ErrorResponse> handleMissingCookieException(MissingRequestCookieException e) {
        final String missingCookie = "필요한 쿠키가 존재하지 않습니다: " + e.getCookieName();
        log.error("handleMissingCookieException throw Exception : {}", e.toString());
        return ErrorResponse.toResponseEntity(DefaultException.fromException(HttpStatus.BAD_REQUEST, missingCookie, e));
    }

    @ExceptionHandler(value = {BadCredentialsException.class})
    protected ResponseEntity<ErrorResponse> handleAuthenticationException(BadCredentialsException e) {
        final String badCredentials = "아이디와 비밀번호를 확인하십시오";
        return ErrorResponse.toResponseEntity(DefaultException.fromException(HttpStatus.BAD_REQUEST, badCredentials, e));
    }

    @ExceptionHandler(value = {NeedLoginException.class})
    protected ResponseEntity<ErrorResponse> handleNeedLoginException(NeedLoginException e) {
        return ErrorResponse.toResponseEntity(e);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class, DataIntegrityViolationException.class})
    protected ResponseEntity<ErrorResponse> handleDataException() {
        log.error("handleDataException throw Exception : {}", DUPLICATE_RESOURCE);
        return ErrorResponse.toResponseEntity(DUPLICATE_RESOURCE);
    }

    @ExceptionHandler(value = {MaxUploadSizeExceededException.class})
    protected ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException() {
        log.error("handleMaxUploadSizeExceededException throw Exception : {}", FILE_SIZE_EXCEED);
        return ErrorResponse.toResponseEntity(FILE_SIZE_EXCEED);
    }

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {} {}", e.getErrorCode(), e.getErrorCode().getDetail());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(value = {DefaultException.class})
    protected ResponseEntity<ErrorResponse> handleDefaultException(DefaultException e) {
        log.error("handleDefaultException throw DefaultException : {} {}", e.getHttpStatus(), e.getMessage());
        return ErrorResponse.toResponseEntity(e);
    }

}
