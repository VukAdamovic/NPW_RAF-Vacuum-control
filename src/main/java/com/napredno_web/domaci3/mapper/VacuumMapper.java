package com.napredno_web.domaci3.mapper;

import com.napredno_web.domaci3.model.Status;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumCreateDto;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumDto;
import com.napredno_web.domaci3.model.entity.UserEntity;
import com.napredno_web.domaci3.model.entity.VacuumEntity;
import com.napredno_web.domaci3.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class VacuumMapper {

    private UserRepository userRepository;

    public VacuumMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public VacuumEntity vacuumCreateDtoToVacuumEntity(VacuumCreateDto vacuumCreateDto){
        VacuumEntity vacuumEntity = new VacuumEntity();

        vacuumEntity.setStatus(Status.OFF); // stanje stop
        vacuumEntity.setActive(true); // aktivan u sistemu
        vacuumEntity.setName(vacuumCreateDto.getName());
        vacuumEntity.setDateCreate(Instant.now().getEpochSecond()); // cuvam sekunde


        Optional<UserEntity> userOptional = userRepository.findById(vacuumCreateDto.getUserId());

        if (userOptional.isPresent()) {
            UserEntity userEntity = userOptional.get();
            vacuumEntity.setAddedBy(userEntity);
        }

        return vacuumEntity;
    }

    public VacuumDto vacuumEntityToVacuumDto(VacuumEntity vacuumEntity){
        VacuumDto vacuumDto = new VacuumDto();

        vacuumDto.setId(vacuumEntity.getId());
        vacuumDto.setStatus(vacuumEntity.getStatus());
        vacuumDto.setAddedBy(vacuumEntity.getAddedBy().getId());
        vacuumDto.setActive(vacuumEntity.isActive());
        vacuumDto.setName(vacuumEntity.getName());

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(vacuumEntity.getDateCreate()), ZoneOffset.UTC);
        vacuumDto.setDateCreate(localDateTime.format(DateTimeFormatter.ofPattern("YYYY-MM-DD")));

        return  vacuumDto;
    }

}
