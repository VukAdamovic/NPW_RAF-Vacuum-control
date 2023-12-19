package com.napredno_web.domaci3.service.impl;

import com.napredno_web.domaci3.exception.NotFoundException;
import com.napredno_web.domaci3.mapper.UserMapper;
import com.napredno_web.domaci3.mapper.VacuumMapper;
import com.napredno_web.domaci3.model.Status;
import com.napredno_web.domaci3.model.dto.vacuum.SearchVacuum;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumCreateDto;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumDto;
import com.napredno_web.domaci3.model.entity.VacuumEntity;
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

    public VacuumServiceImpl(UserRepository userRepository, VacuumRepository vacuumRepository, UserMapper userMapper, VacuumMapper vacuumMapper) {
        this.userRepository = userRepository;
        this.vacuumRepository = vacuumRepository;
        this.userMapper = userMapper;
        this.vacuumMapper = vacuumMapper;
    }

    @Override
    public List<VacuumDto> searchVacuums(SearchVacuum searchVacuum) {
        List<VacuumDto> vacuums = this.noFilter(searchVacuum, new ArrayList<>()); // na samom pocetku odmah ubacujem sve usisivace koji su aktivini i koje je kreirao taj user


        if(searchVacuum.getName() != null){
          vacuums = this.filterByName(searchVacuum, vacuums);
        }

        if(searchVacuum.getStatus() != null){
            vacuums = this.filterByStatus(searchVacuum, vacuums);
        }

        if(searchVacuum.getDateFrom() != null){
            vacuums = this.filterByDateFrom(searchVacuum, vacuums);

        }

        if(searchVacuum.getDateTo() != null){
            vacuums = this.filterByDateTo(searchVacuum, vacuums);
        }


        return vacuums;
    }

    @Override
    public VacuumDto addVacuum(VacuumCreateDto vacuumCreateDto) {
        VacuumEntity vacuumEntity = vacuumMapper.vacuumCreateDtoToVacuumEntity(vacuumCreateDto);
        vacuumRepository.save(vacuumEntity);
        return vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity);
    }

    @Override
    public VacuumDto removeVacuum(Long id) {
        VacuumEntity vacuumEntity = vacuumRepository.findById(id)
                .orElseThrow(() ->new NotFoundException(String.format("Vacuum with id: %d does not exists.", id)));

        vacuumEntity.setActive(false); //iskljucen iz sistema

        vacuumRepository.save(vacuumEntity);

        return vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity);
    }



    //
    private long pretvoriStringUDatum(String stringDatum) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        LocalDate localDate = LocalDate.parse(stringDatum, formatter);

        LocalDateTime localDateTime = localDate.atStartOfDay();

        Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

        return instant.getEpochSecond();
    }


    private List<VacuumDto> noFilter(SearchVacuum searchVacuum, List<VacuumDto> vacuums){
        vacuumRepository.findAll().
                forEach(vacuumEntity -> {
                    if(vacuumEntity.getAddedBy().getId().equals(searchVacuum.getUserId()) && vacuumEntity.isActive()){
                        vacuums.add(vacuumMapper.vacuumEntityToVacuumDto(vacuumEntity));
                    }
                });

        return vacuums;
    }

    private  List<VacuumDto> filterByName(SearchVacuum searchVacuum, List<VacuumDto> vacuums) {
        List<VacuumDto> result = new ArrayList<>();
        for (VacuumDto vacuumDto : vacuums) {
            if (vacuumDto.getName().toLowerCase().contains(searchVacuum.getName().toLowerCase())) {
                result.add(vacuumDto);
            }
        }

        return result;
    }

    private List<VacuumDto> filterByStatus(SearchVacuum searchVacuum, List<VacuumDto> vacuums){
        List<VacuumDto> result = new ArrayList<>();

        if(searchVacuum.getStatus().equals(Status.ON)){
            for (VacuumDto vacuumDto : vacuums) {
                if (vacuumDto.getStatus().equals(Status.ON)) {
                    result.add(vacuumDto);
                }
            }
        }
        else if(searchVacuum.getStatus().equals(Status.OFF)){
            for (VacuumDto vacuumDto : vacuums) {
                if (vacuumDto.getStatus().equals(Status.OFF)) {
                    result.add(vacuumDto);
                }
            }
        }
        else if (searchVacuum.getStatus().equals(Status.DISCHARGING)){
            for (VacuumDto vacuumDto : vacuums) {
                if (vacuumDto.getStatus().equals(Status.DISCHARGING)) {
                    result.add(vacuumDto);
                }
            }
        }

        return result;
    }

    private List<VacuumDto> filterByDateFrom(SearchVacuum searchVacuum, List<VacuumDto> vacuums){
        List<VacuumDto> result = new ArrayList<>();
        long datumSekunde = pretvoriStringUDatum(searchVacuum.getDateFrom()); //trazeni datum od pretvaram u sekunde

        for (VacuumDto vacuumDto : vacuums) {
            long createSekunde = pretvoriStringUDatum(vacuumDto.getDateCreate());

            if (datumSekunde <= createSekunde) {
                result.add(vacuumDto);
            }
        }

        return result;
    }

    private List<VacuumDto> filterByDateTo(SearchVacuum searchVacuum, List<VacuumDto> vacuums){
        List<VacuumDto> result = new ArrayList<>();
        long datumSekunde = pretvoriStringUDatum(searchVacuum.getDateTo()); //trazeni datum do pretvaram u sekunde

        for (VacuumDto vacuumDto : vacuums) {
            long createSekunde = pretvoriStringUDatum(vacuumDto.getDateCreate());

            if (datumSekunde >= createSekunde) {
                result.add(vacuumDto);
            }
        }

        return result;
    }

}
