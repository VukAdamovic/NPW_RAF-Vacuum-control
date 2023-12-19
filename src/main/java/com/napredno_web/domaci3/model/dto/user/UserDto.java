package com.napredno_web.domaci3.model.dto.user;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private boolean canCreateUsers;

    private boolean canReadUsers;

    private boolean canUpdateUsers;

    private boolean canDeleteUsers;

    private boolean canSearchVacuum;

    private boolean canStartVacuum;

    private boolean canStopVacuum;

    private boolean canDischargeVacuum;

    private boolean canAddVacuum;

    private boolean canRemoveVacuum;
}
