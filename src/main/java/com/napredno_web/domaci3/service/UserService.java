package com.napredno_web.domaci3.service;

import com.napredno_web.domaci3.model.dto.user.UserCreateDto;
import com.napredno_web.domaci3.model.dto.user.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(Long id);

    UserDto create(UserCreateDto userCreateDto);

    UserDto update(UserDto userDto);

    boolean delete(Long id);

}
