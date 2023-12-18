package com.napredno_web.domaci3.model.dto.vacuum;

import com.napredno_web.domaci3.model.Status;
import lombok.Data;

@Data
public class VacuumDto {

    private Long id;

    private Status status;

    private Long addedBy;

    private boolean active;

    private String name;

    private String dateCreate;
}
