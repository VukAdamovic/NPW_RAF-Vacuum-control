package com.napredno_web.domaci3.service.impl;

import com.napredno_web.domaci3.mapper.UserMapper;
import com.napredno_web.domaci3.mapper.VacuumMapper;
import com.napredno_web.domaci3.model.Status;
import com.napredno_web.domaci3.model.dto.vacuum.SearchVacuum;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumCreateDto;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumDto;
import com.napredno_web.domaci3.repository.UserRepository;
import com.napredno_web.domaci3.repository.VacuumRepository;
import com.napredno_web.domaci3.service.VacuumService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class VacuumServiceImpl implements VacuumService {

    private UserRepository userRepository;

    private VacuumRepository vacuumRepository;

    private UserMapper userMapper;

    private VacuumMapper vacuumMapper;


    @Override
    public List<VacuumDto> searchVacuums(SearchVacuum searchVacuum) {
        List<VacuumDto> vacuums = new ArrayList<>();

        if(searchVacuum.getName() == null && searchVacuum.getStatus() == null && searchVacuum.getDateFrom() == null && searchVacuum.getDateTo() == null){
            vacuumRepository.findAll().
                    forEach(vacuumEntity -> {
                        if(vacuumEntity.getAddedBy().getId().equals(searchVacuum.getUserId()) && vacuumEntity.isActive()){
                            vacuums.add(vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity));
                        }
                    });
        }

        else if(searchVacuum.getName() != null && searchVacuum.getStatus() == null && searchVacuum.getDateFrom() == null && searchVacuum.getDateTo() == null){
            vacuumRepository.findAll().
                    forEach(vacuumEntity -> {
                        if(vacuumEntity.getAddedBy().getId().equals(searchVacuum.getUserId()) && vacuumEntity.isActive()
                                && vacuumEntity.getName().toLowerCase().contains(searchVacuum.getName().toLowerCase())){
                            vacuums.add(vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity));
                        }
                    });
        }

        else if(searchVacuum.getName() == null && searchVacuum.getStatus() != null && searchVacuum.getDateFrom() == null && searchVacuum.getDateTo() == null){
            if(searchVacuum.getStatus().equals(Status.ON)){
                vacuumRepository.findAll().
                        forEach(vacuumEntity -> {
                            if(vacuumEntity.getAddedBy().getId().equals(searchVacuum.getUserId()) && vacuumEntity.isActive()
                                    && vacuumEntity.getStatus().equals(Status.ON)){
                                vacuums.add(vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity));
                            }
                        });
            }
            else if(searchVacuum.getStatus().equals(Status.OFF)){
                vacuumRepository.findAll().
                        forEach(vacuumEntity -> {
                            if(vacuumEntity.getAddedBy().getId().equals(searchVacuum.getUserId()) && vacuumEntity.isActive()
                                    && vacuumEntity.getStatus().equals(Status.OFF)){
                                vacuums.add(vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity));
                            }
                        });
            }
            else if (searchVacuum.getStatus().equals(Status.DISCHARGING)){
                vacuumRepository.findAll().
                        forEach(vacuumEntity -> {
                            if(vacuumEntity.getAddedBy().getId().equals(searchVacuum.getUserId()) && vacuumEntity.isActive()
                                    && vacuumEntity.getStatus().equals(Status.DISCHARGING)){
                                vacuums.add(vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity));
                            }
                        });
            }
        }

        else if(searchVacuum.getName() == null && searchVacuum.getStatus() == null && searchVacuum.getDateFrom() != null && searchVacuum.getDateTo() == null){
            vacuumRepository.findAll().
                    forEach(vacuumEntity -> {
                        long datumSekunde = pretvoriStringUDatum(searchVacuum.getDateFrom());

                        if(vacuumEntity.getAddedBy().getId().equals(searchVacuum.getUserId()) && vacuumEntity.isActive()
                                && datumSekunde >= vacuumEntity.getDateCreate()){
                            vacuums.add(vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity));
                        }
                    });
        }

        else if(searchVacuum.getName() == null && searchVacuum.getStatus() == null && searchVacuum.getDateFrom() == null && searchVacuum.getDateTo() != null){
            vacuumRepository.findAll().
                    forEach(vacuumEntity -> {
                        long datumSekunde = pretvoriStringUDatum(searchVacuum.getDateTo());

                        if(vacuumEntity.getAddedBy().getId().equals(searchVacuum.getUserId()) && vacuumEntity.isActive()
                                && datumSekunde <= vacuumEntity.getDateCreate()){
                            vacuums.add(vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity));
                        }
                    });
        }


        return vacuums;
    }

    @Override
    public VacuumDto addVacuum(VacuumCreateDto vacuumCreateDto) {
        return null;
    }

    @Override
    public VacuumDto removeVacuum(Long id) {
        return null;
    }



    //
    private static long pretvoriStringUDatum(String stringDatum) {
        // Parsiranje string datuma u LocalDate
        LocalDate localDate = LocalDate.parse(stringDatum, DateTimeFormatter.ISO_DATE);

        // Pretvaranje u LocalDateTime
        LocalDateTime localDateTime = localDate.atStartOfDay();

        // Pretvaranje u Instant
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

        // Dobijanje broja sekundi
        return instant.getEpochSecond();
    }
}
