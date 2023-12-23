package com.napredno_web.domaci3.mapper;

import com.napredno_web.domaci3.model.dto.errorMessage.ErrorMessageCreateDto;
import com.napredno_web.domaci3.model.dto.errorMessage.ErrorMessageDto;
import com.napredno_web.domaci3.model.entity.ErrorMessageEntity;
import com.napredno_web.domaci3.model.entity.VacuumEntity;
import com.napredno_web.domaci3.repository.VacuumRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ErrorMessageMapper {

    private VacuumRepository vacuumRepository;

    public ErrorMessageMapper(VacuumRepository vacuumRepository) {
        this.vacuumRepository = vacuumRepository;
    }

    public ErrorMessageEntity errorMessageCreateDtoToErrorMessageEntity(ErrorMessageCreateDto errorMessageCreateDto){
        ErrorMessageEntity errorMessageEntity = new ErrorMessageEntity();

        errorMessageEntity.setBookedOperation(errorMessageCreateDto.getBookedOperation());
        errorMessageEntity.setError(errorMessageCreateDto.getError());

        Optional<VacuumEntity> vacuumOptional = vacuumRepository.findById(errorMessageCreateDto.getVacuumId());

        if(vacuumOptional.isPresent()){
            VacuumEntity vacuumEntity = vacuumOptional.get();
            errorMessageEntity.setVacuumEntity(vacuumEntity);
        }

        return errorMessageEntity;
    }

    public ErrorMessageDto errorMessageEntityToErrorMessageDto(ErrorMessageEntity errorMessageEntity){
        ErrorMessageDto errorMessageDto = new ErrorMessageDto();

        errorMessageDto.setId(errorMessageEntity.getId());
        errorMessageDto.setVacuumId(errorMessageEntity.getVacuumEntity().getId());
        errorMessageDto.setBookedOperation(errorMessageEntity.getBookedOperation());
        errorMessageDto.setError(errorMessageEntity.getError());

        return errorMessageDto;
    }

}
