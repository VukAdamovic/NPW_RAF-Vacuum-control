package com.napredno_web.domaci3.controller;

import com.napredno_web.domaci3.model.dto.errorMessage.ErrorMessageDto;
import com.napredno_web.domaci3.security.CheckSecurity;
import com.napredno_web.domaci3.service.ErrorMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/errorMessages")
public class ErrorMessageController {

    private ErrorMessageService errorMessageService;


    public ErrorMessageController(ErrorMessageService errorMessageService) {
        this.errorMessageService = errorMessageService;
    }

    @GetMapping("/{id}")
    @CheckSecurity(permissions={"READ"})
    public ResponseEntity<List<ErrorMessageDto>> getAllErrors(@RequestHeader("authorization") String authorization, @PathVariable("id") Long id){
        return new ResponseEntity<>(errorMessageService.findErrorsByVacuumId(id), HttpStatus.OK);
    }
}
