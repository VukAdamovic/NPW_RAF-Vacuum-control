package com.napredno_web.domaci3.service;

import com.napredno_web.domaci3.model.token.TokenRequestDto;
import com.napredno_web.domaci3.model.token.TokenResponseDto;

public interface LoginService {
    TokenResponseDto login(TokenRequestDto tokenRequestDto);
}
