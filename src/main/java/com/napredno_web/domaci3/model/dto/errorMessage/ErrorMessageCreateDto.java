package com.napredno_web.domaci3.model.dto.errorMessage;

import lombok.Data;

@Data
public class ErrorMessageCreateDto {

    private Long vacuumId;

    private String bookedOperation;

    private String error;
}

