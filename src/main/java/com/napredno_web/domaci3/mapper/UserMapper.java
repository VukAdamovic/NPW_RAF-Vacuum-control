package com.napredno_web.domaci3.mapper;

import com.napredno_web.domaci3.model.dto.user.UserCreateDto;
import com.napredno_web.domaci3.model.dto.user.UserDto;
import com.napredno_web.domaci3.model.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserMapper() {
    }

    // Insert new data into base
    public UserEntity userCreateDtoToUserEntity(UserCreateDto userCreateDto){
        UserEntity userEntity = new UserEntity();

        userEntity.setFirstName(userCreateDto.getFirstName());
        userEntity.setLastName(userCreateDto.getLastName());
        userEntity.setEmail(userCreateDto.getEmail());
        userEntity.setHashPassword(userCreateDto.getPassword()); // ovde se hashira sifra
        userEntity.setCanReadUsers(userCreateDto.isCanReadUsers());
        userEntity.setCanCreateUsers(userCreateDto.isCanCreateUsers());
        userEntity.setCanUpdateUsers(userCreateDto.isCanUpdateUsers());
        userEntity.setCanDeleteUsers(userCreateDto.isCanDeleteUsers());

        return userEntity;
    }

    // Taking existing data from base
    public UserDto userEntityToUserDto(UserEntity userEntity){
        UserDto userDto = new UserDto();

        userDto.setId(userEntity.getId());
        userDto.setFirstName(userEntity.getFirstName());
        userDto.setLastName(userEntity.getLastName());
        userDto.setEmail(userEntity.getEmail());
        userDto.setPassword(userEntity.getPassword());
        userDto.setCanReadUsers(userEntity.isCanReadUsers());
        userDto.setCanCreateUsers(userEntity.isCanCreateUsers());
        userDto.setCanUpdateUsers(userEntity.isCanUpdateUsers());
        userDto.setCanDeleteUsers(userEntity.isCanDeleteUsers());

        return userDto;
    }


}
