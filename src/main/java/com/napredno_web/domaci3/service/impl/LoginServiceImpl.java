package com.napredno_web.domaci3.service.impl;

import com.napredno_web.domaci3.exception.NotFoundException;
import com.napredno_web.domaci3.model.entity.UserEntity;
import com.napredno_web.domaci3.model.token.TokenRequestDto;
import com.napredno_web.domaci3.model.token.TokenResponseDto;
import com.napredno_web.domaci3.repository.UserRepository;
import com.napredno_web.domaci3.security.service.TokenService;
import com.napredno_web.domaci3.service.LoginService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    private UserRepository userRepository;

    private TokenService tokenService;


    public LoginServiceImpl(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Override
    public TokenResponseDto login(TokenRequestDto tokenRequestDto) {
        UserEntity user = userRepository.findUserEntityByEmail(tokenRequestDto.getEmail())
                .orElseThrow(() -> new NotFoundException(String.format("User with email: %s not found.", tokenRequestDto.getEmail())));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(tokenRequestDto.getPassword(), user.getPassword())) {
            throw new NotFoundException(String.format("User with email: %s and provided password not found.", tokenRequestDto.getEmail()));
        }


        //Create token payload
        Claims claims = Jwts.claims();
        claims.put("id", user.getId());
        claims.put("read", user.isCanReadUsers());
        claims.put("create", user.isCanCreateUsers());
        claims.put("update", user.isCanUpdateUsers());
        claims.put("delete", user.isCanDeleteUsers());

        //Generate token
        return new TokenResponseDto(tokenService.generate(claims));
    }

}
