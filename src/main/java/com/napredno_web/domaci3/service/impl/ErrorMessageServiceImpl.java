package com.napredno_web.domaci3.service.impl;

import com.napredno_web.domaci3.mapper.ErrorMessageMapper;
import com.napredno_web.domaci3.mapper.VacuumMapper;
import com.napredno_web.domaci3.model.dto.errorMessage.ErrorMessageCreateDto;
import com.napredno_web.domaci3.model.dto.errorMessage.ErrorMessageDto;
import com.napredno_web.domaci3.model.entity.ErrorMessageEntity;
import com.napredno_web.domaci3.repository.ErrorMessageRepository;
import com.napredno_web.domaci3.repository.VacuumRepository;
import com.napredno_web.domaci3.service.ErrorMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ErrorMessageServiceImpl implements ErrorMessageService {

    private ErrorMessageRepository errorMessageRepository;

    private ErrorMessageMapper errorMessageMapper;


    public ErrorMessageServiceImpl(ErrorMessageRepository errorMessageRepository, ErrorMessageMapper errorMessageMapper) {
        this.errorMessageRepository = errorMessageRepository;
        this.errorMessageMapper = errorMessageMapper;
    }

    @Override
    public List<ErrorMessageDto> findErrorsByVacuumId(Long vacuumId) {
        List<ErrorMessageDto> errors = new ArrayList<>();

        errorMessageRepository.findAllByVacuumEntity_Id(vacuumId).forEach(errorMessageEntity -> {
            errors.add(errorMessageMapper.errorMessageEntityToErrorMessageDto(errorMessageEntity));
        });

        return errors;
    }

    @Override
    public ErrorMessageDto addError(ErrorMessageCreateDto errorMessageCreateDto) {
        ErrorMessageEntity errorMessageEntity = errorMessageMapper.errorMessageCreateDtoToErrorMessageEntity(errorMessageCreateDto);
        errorMessageRepository.save(errorMessageEntity);
        return errorMessageMapper.errorMessageEntityToErrorMessageDto(errorMessageEntity);
    }
}
