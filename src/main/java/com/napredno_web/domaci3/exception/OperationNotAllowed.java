package com.napredno_web.domaci3.exception;

import org.springframework.http.HttpStatus;

public class OperationNotAllowed extends CustomException{

    public OperationNotAllowed(String message) {
        super(message, ErrorCode.OPERATION_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
