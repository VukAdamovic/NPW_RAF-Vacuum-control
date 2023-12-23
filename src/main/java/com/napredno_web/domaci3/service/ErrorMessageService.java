package com.napredno_web.domaci3.service;

import com.napredno_web.domaci3.model.dto.errorMessage.ErrorMessageCreateDto;
import com.napredno_web.domaci3.model.dto.errorMessage.ErrorMessageDto;

import java.util.List;

public interface ErrorMessageService {
    List<ErrorMessageDto> findAllErrorByUserId(Long userId);

    ErrorMessageDto addError(ErrorMessageCreateDto errorMessageCreateDto);

}
