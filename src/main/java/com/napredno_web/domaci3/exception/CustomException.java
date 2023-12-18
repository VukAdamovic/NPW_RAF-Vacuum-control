package com.napredno_web.domaci3.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends RuntimeException {
    private ErrorCode errorCode;
    private HttpStatus httpStatus;

    public CustomException(String message, ErrorCode errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
}
