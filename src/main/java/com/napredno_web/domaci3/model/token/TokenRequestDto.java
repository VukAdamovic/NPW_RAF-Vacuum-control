package com.napredno_web.domaci3.model.token;


import lombok.Data;

@Data
public class TokenRequestDto {

    private String email;
    private String password;
    public TokenRequestDto() {
    }
    public TokenRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
