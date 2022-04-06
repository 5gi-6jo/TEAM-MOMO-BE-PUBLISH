package com.sparta.team6.momo.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.team6.momo.exception.custom.NeedLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FilterException {

    public void unauthorizedException(HttpServletResponse response, NeedLoginException e) throws IOException {
        setDefaultHeader(response, HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Go to login page and login");

        setErrorBody(response, HttpStatus.UNAUTHORIZED, e, e.getMessage());
    }

    public void jwtException(HttpServletResponse response, DefaultException e) throws IOException {
        setDefaultHeader(response, HttpServletResponse.SC_BAD_REQUEST);
        setErrorBody(response, HttpStatus.BAD_REQUEST, e.getException(), e.getMessage());
    }


    private void setDefaultHeader(HttpServletResponse response, int httpServletResponse) {
        response.setStatus(httpServletResponse);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
    }


    private void setErrorBody(HttpServletResponse response, HttpStatus httpStatus, Exception e, String message) throws IOException {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(httpStatus.value())
                .error(httpStatus.name())
                .code(e.getClass().getSimpleName())
                .message(message)
                .build();
        ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
        String body = om.writeValueAsString(errorResponse);
        response.getWriter().write(body);
    }

}
