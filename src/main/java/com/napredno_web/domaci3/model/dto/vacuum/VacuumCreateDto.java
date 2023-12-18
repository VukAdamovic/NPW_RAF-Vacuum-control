package com.napredno_web.domaci3.model.dto.vacuum;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VacuumCreateDto {

    private Long userId;

    @NotBlank(message = "Vacuum name is required")
    private String name;

}
