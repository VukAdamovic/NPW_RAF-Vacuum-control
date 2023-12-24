package com.napredno_web.domaci3.model.dto.errorMessage;


import lombok.Data;

@Data
public class ErrorMessageDto {

    private Long id;

    private String dateCreate;

    private Long vacuumId;

    private String bookedOperation;

    private String error;

}
