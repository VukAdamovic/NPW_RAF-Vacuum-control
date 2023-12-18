package com.napredno_web.domaci3.service.impl;

import com.napredno_web.domaci3.exception.NotFoundException;
import com.napredno_web.domaci3.mapper.UserMapper;
import com.napredno_web.domaci3.model.dto.user.UserCreateDto;
import com.napredno_web.domaci3.model.dto.user.UserDto;
import com.napredno_web.domaci3.model.entity.UserEntity;
import com.napredno_web.domaci3.repository.UserRepository;
import com.napredno_web.domaci3.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserDto> findAll() {
        List<UserDto> users = new ArrayList<>();
        userRepository.findAll()
                .forEach(userEntity -> {
                    users.add(userMapper.userEntityToUserDto(userEntity));
                });
        return users;
    }

    @Override
    public UserDto findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::userEntityToUserDto)
                .orElseThrow(()->  new NotFoundException(String.format("User with id: %d does not exists.", id)));
    }

    @Override
    public UserDto create(UserCreateDto userCreateDto) {
        UserEntity userEntity = userMapper.userCreateDtoToUserEntity(userCreateDto);
        userRepository.save(userEntity);
        return userMapper.userEntityToUserDto(userEntity);
    }

    @Override
    public UserDto update(UserDto userDto) {
        UserEntity userEntity = userRepository.findById(userDto.getId())
                .orElseThrow(() ->new NotFoundException(String.format("User with id: %d does not exists.", userDto.getId())));

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setCanReadUsers(userDto.isCanReadUsers());
        userEntity.setCanCreateUsers(userDto.isCanCreateUsers());
        userEntity.setCanUpdateUsers(userDto.isCanUpdateUsers());
        userEntity.setCanDeleteUsers(userDto.isCanDeleteUsers());

        userRepository.save(userEntity);

        return userMapper.userEntityToUserDto(userEntity);
    }

    @Override
    public boolean delete(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() ->new NotFoundException(String.format("User with id: %d does not exists.", id)));

        userRepository.delete(userEntity);
        return true;
    }
}
