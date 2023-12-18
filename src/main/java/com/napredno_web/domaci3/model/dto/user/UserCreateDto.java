package com.napredno_web.domaci3.model.dto.user;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserCreateDto {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 30, message = "Password must be between 8 and 30 characters")
    private String password;

    @NotNull
    private boolean canCreateUsers;

    @NotNull
    private boolean canReadUsers;

    @NotNull
    private boolean canUpdateUsers;

    @NotNull
    private boolean canDeleteUsers;

}
