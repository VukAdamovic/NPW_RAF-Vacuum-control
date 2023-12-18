package com.napredno_web.domaci3.controller;

import com.napredno_web.domaci3.exception.NotFoundException;
import com.napredno_web.domaci3.model.dto.user.UserCreateDto;
import com.napredno_web.domaci3.model.dto.user.UserDto;
import com.napredno_web.domaci3.model.token.TokenRequestDto;
import com.napredno_web.domaci3.model.token.TokenResponseDto;
import com.napredno_web.domaci3.security.CheckSecurity;
import com.napredno_web.domaci3.service.LoginService;
import com.napredno_web.domaci3.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    private LoginService loginService;

    public UserController(UserService userService, LoginService loginService) {
        this.userService = userService;
        this.loginService = loginService;
    }


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody TokenRequestDto tokenRequestDto) {
        return new ResponseEntity<>(loginService.login(tokenRequestDto), HttpStatus.OK);
    }

    @GetMapping
    @CheckSecurity(permissions={"READ"})
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestHeader("authorization") String authorization){
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @CheckSecurity(permissions={"CREATE","READ","DELETE","UPDATE"})
    public ResponseEntity<UserDto> getUserById(@RequestHeader("authorization") String authorization, @PathVariable("id") Long id){
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @PostMapping
    @CheckSecurity(permissions={"CREATE"})
    public ResponseEntity<UserDto> createUser(@RequestHeader("authorization") String authorization, @RequestBody UserCreateDto userCreateDto) {
        return new ResponseEntity<>(userService.create(userCreateDto), HttpStatus.CREATED);
    }

    @PutMapping
    @CheckSecurity(permissions={"UPDATE"})
    public ResponseEntity<UserDto> updateUser(@RequestHeader("authorization") String authorization, @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.update(userDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @CheckSecurity(permissions={"DELETE"})
    public ResponseEntity<Boolean> deleteUser(@RequestHeader("authorization") String authorization, @PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.delete(id), HttpStatus.OK);
    }

}
