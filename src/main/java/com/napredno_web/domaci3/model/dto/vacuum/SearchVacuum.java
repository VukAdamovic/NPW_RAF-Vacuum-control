package com.napredno_web.domaci3.model.dto.vacuum;

import com.napredno_web.domaci3.model.Status;
import lombok.Data;

@Data
public class SearchVacuum {

    private Long userId;

    private String name;

    private Status status;

    private String dateFrom;

    private String dateTo;
}
