package com.napredno_web.domaci3.model.token;

import lombok.Data;

@Data
public class TokenResponseDto {

    private String token;
    public TokenResponseDto() {
    }

    public TokenResponseDto(String token) {
        this.token = token;
    }
}
