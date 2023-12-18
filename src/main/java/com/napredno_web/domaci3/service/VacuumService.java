package com.napredno_web.domaci3.service;


import com.napredno_web.domaci3.model.dto.vacuum.SearchVacuum;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumCreateDto;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumDto;

import java.util.List;


public interface VacuumService {

    List<VacuumDto> searchVacuums(SearchVacuum searchVacuum); //Svaki usisivač pripada korisniku koji ga je dodao u sistem i korisnik ima uvid samo u usisivače koje je on dodao u sistem.

    VacuumDto addVacuum(VacuumCreateDto vacuumCreateDto);

    VacuumDto removeVacuum(Long id); //ne brises ga iz baze vec stavljas active polje na false




}
