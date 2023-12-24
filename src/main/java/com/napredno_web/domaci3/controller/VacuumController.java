package com.napredno_web.domaci3.controller;

import com.napredno_web.domaci3.exception.NotFoundException;
import com.napredno_web.domaci3.exception.OperationNotAllowed;
import com.napredno_web.domaci3.model.dto.vacuum.BookOperation;
import com.napredno_web.domaci3.model.dto.vacuum.SearchVacuum;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumCreateDto;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumDto;
import com.napredno_web.domaci3.security.CheckSecurity;
import com.napredno_web.domaci3.service.VacuumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;


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
    @ExceptionHandler(HttpClientErrorException.MethodNotAllowed.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<String> handleOperationNotAllowedException(HttpClientErrorException.MethodNotAllowed ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
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

    @PutMapping("/start/{id}")
    @CheckSecurity(permissions={"START"})
    public ResponseEntity<Boolean> startVacuum(@RequestHeader("authorization") String authorization, @PathVariable("id") Long id) {
        return new ResponseEntity<>(vacuumService.startVacuum(id), HttpStatus.OK);
    }

    @PutMapping("/stop/{id}")
    @CheckSecurity(permissions={"STOP"})
    public ResponseEntity<Boolean> stopVacuum(@RequestHeader("authorization") String authorization, @PathVariable("id") Long id) {
        return new ResponseEntity<>(vacuumService.stopVacuum(id), HttpStatus.OK);
    }

    @PutMapping("/discharge/{id}")
    @CheckSecurity(permissions={"DISCHARGE"})
    public ResponseEntity<Boolean> dischargeVacuum(@RequestHeader("authorization") String authorization, @PathVariable("id") Long id) {
        return new ResponseEntity<>(vacuumService.dischargeVacuum(id), HttpStatus.OK);
    }

    @PutMapping("/bookStartOperation")
    @CheckSecurity(permissions={"START"})
    public ResponseEntity<Boolean> bookStartVacuum(@RequestHeader("authorization") String authorization, @RequestBody BookOperation bookOperation) {
        return new ResponseEntity<>(vacuumService.bookStartOperation(bookOperation), HttpStatus.OK);
    }

    @PutMapping("/bookStopOperation")
    @CheckSecurity(permissions={"STOP"})
    public ResponseEntity<Boolean> bookStopVacuum(@RequestHeader("authorization") String authorization, @RequestBody BookOperation bookOperation) {
        return new ResponseEntity<>(vacuumService.bookStopOperation(bookOperation), HttpStatus.OK);
    }

    @PutMapping("/bookDischargeOperation")
    @CheckSecurity(permissions={"DISCHARGE"})
    public ResponseEntity<Boolean> bookDischargeVacuum(@RequestHeader("authorization") String authorization, @RequestBody BookOperation bookOperation) {
        return new ResponseEntity<>(vacuumService.bookDischargeOperation(bookOperation), HttpStatus.OK);
    }
}
