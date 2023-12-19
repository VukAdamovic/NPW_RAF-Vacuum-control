package com.napredno_web.domaci3.controller;

import com.napredno_web.domaci3.exception.NotFoundException;
import com.napredno_web.domaci3.model.dto.vacuum.SearchVacuum;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumCreateDto;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumDto;
import com.napredno_web.domaci3.security.CheckSecurity;
import com.napredno_web.domaci3.service.VacuumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacuums")
public class VacuumController {

    private VacuumService vacuumService;

    public VacuumController(VacuumService vacuumService) {
        this.vacuumService = vacuumService;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create")
    @CheckSecurity(permissions={"ADD"})
    public ResponseEntity<VacuumDto> addVacuum(@RequestHeader("authorization") String authorization, @RequestBody VacuumCreateDto vacuumCreateDto){
        return new ResponseEntity<>(vacuumService.addVacuum(vacuumCreateDto), HttpStatus.OK);
    }

    @PutMapping
    @CheckSecurity(permissions={"SEARCH"})
    public ResponseEntity<List<VacuumDto>> getAllVacuums(@RequestHeader("authorization") String authorization,  @RequestBody SearchVacuum searchVacuum){
        return new ResponseEntity<>(vacuumService.searchVacuums(searchVacuum), HttpStatus.OK);
    }

    @PutMapping("/deactivcate/{id}")
    @CheckSecurity(permissions={"REMOVE"})
    public ResponseEntity<VacuumDto> removeVacuumFromSystem(@RequestHeader("authorization") String authorization, @PathVariable("id") Long id){
        return new ResponseEntity<>(vacuumService.removeVacuum(id), HttpStatus.OK);
    }



}
