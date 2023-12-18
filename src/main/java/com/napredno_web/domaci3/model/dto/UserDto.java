package com.napredno_web.domaci3.model.dto;

import lombok.Data;

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
}
