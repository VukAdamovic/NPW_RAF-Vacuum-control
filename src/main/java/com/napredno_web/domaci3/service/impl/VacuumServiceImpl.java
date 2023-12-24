package com.napredno_web.domaci3.service.impl;

import com.napredno_web.domaci3.exception.NotFoundException;
import com.napredno_web.domaci3.exception.OperationNotAllowed;
import com.napredno_web.domaci3.mapper.UserMapper;
import com.napredno_web.domaci3.mapper.VacuumMapper;
import com.napredno_web.domaci3.model.Status;
import com.napredno_web.domaci3.model.dto.errorMessage.ErrorMessageCreateDto;
import com.napredno_web.domaci3.model.dto.vacuum.BookOperation;
import com.napredno_web.domaci3.model.dto.vacuum.SearchVacuum;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumCreateDto;
import com.napredno_web.domaci3.model.dto.vacuum.VacuumDto;
import com.napredno_web.domaci3.model.entity.VacuumEntity;
import com.napredno_web.domaci3.repository.UserRepository;
import com.napredno_web.domaci3.repository.VacuumRepository;
import com.napredno_web.domaci3.service.ErrorMessageService;
import com.napredno_web.domaci3.service.VacuumService;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class VacuumServiceImpl implements VacuumService {


    private VacuumRepository vacuumRepository;

    private VacuumMapper vacuumMapper;

    private ErrorMessageService errorMessageService;

    private final TaskScheduler taskScheduler;

    private final ConcurrentHashMap<Long, Boolean> pendingOperations = new ConcurrentHashMap<>();

    public VacuumServiceImpl(VacuumRepository vacuumRepository, VacuumMapper vacuumMapper, ErrorMessageService errorMessageService, TaskScheduler taskScheduler) {
        this.vacuumRepository = vacuumRepository;
        this.vacuumMapper = vacuumMapper;
        this.errorMessageService = errorMessageService;
        this.taskScheduler = taskScheduler;
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

    @Override
    public Boolean startVacuum(Long id) {
        VacuumEntity vacuum = vacuumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Vacuum with id: %d does not exist.", id)));

        if (vacuum.getStatus() != Status.OFF) {
            ErrorMessageCreateDto errorMessageCreateDto = new ErrorMessageCreateDto();
            errorMessageCreateDto.setVacuumId(vacuum.getId());
            errorMessageCreateDto.setBookedOperation("START OPERATION");
            errorMessageCreateDto.setError("VACUUM STATUS IS NOT 'OFF'");
            errorMessageService.addError(errorMessageCreateDto);

            return false;
        }

        if(pendingOperations.putIfAbsent(id, true) != null){

            ErrorMessageCreateDto errorMessageCreateDto = new ErrorMessageCreateDto();
            errorMessageCreateDto.setVacuumId(vacuum.getId());
            errorMessageCreateDto.setBookedOperation("START OPERATION");
            errorMessageCreateDto.setError("OTHER OPERATION IS ALREADY BEING PERFORMED");
            errorMessageService.addError(errorMessageCreateDto);

            return false;
        }

        Thread thread = new Thread(() -> operationsVacuumAsync(id, 15000, Status.ON));
        thread.start();

        return true;
    }

    @Override
    public Boolean stopVacuum(Long id) {
        VacuumEntity vacuum = vacuumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Vacuum with id: %d does not exist.", id)));

        if (vacuum.getStatus() != Status.ON) {
            ErrorMessageCreateDto errorMessageCreateDto = new ErrorMessageCreateDto();
            errorMessageCreateDto.setVacuumId(vacuum.getId());
            errorMessageCreateDto.setBookedOperation("STOP OPERATION");
            errorMessageCreateDto.setError("VACUUM STATUS IS NOT 'ON'");
            errorMessageService.addError(errorMessageCreateDto);

            return false;
        }

        if(pendingOperations.putIfAbsent(id, true) != null){
            ErrorMessageCreateDto errorMessageCreateDto = new ErrorMessageCreateDto();
            errorMessageCreateDto.setVacuumId(vacuum.getId());
            errorMessageCreateDto.setBookedOperation("STOP OPERATION");
            errorMessageCreateDto.setError("OTHER OPERATION IS ALREADY BEING PERFORMED");
            errorMessageService.addError(errorMessageCreateDto);

            return false;
        }

        Thread thread = new Thread(() -> operationsVacuumAsync(id, 15000, Status.OFF));
        thread.start();

        return true;
    }

    @Override
    public Boolean dischargeVacuum(Long id) {
        VacuumEntity vacuum = vacuumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Vacuum with id: %d does not exist.", id)));

        if (vacuum.getStatus() != Status.OFF) {
            ErrorMessageCreateDto errorMessageCreateDto = new ErrorMessageCreateDto();
            errorMessageCreateDto.setVacuumId(vacuum.getId());
            errorMessageCreateDto.setBookedOperation("DISCHARGE OPERATION");
            errorMessageCreateDto.setError("VACUUM STATUS IS NOT 'OFF'");
            errorMessageService.addError(errorMessageCreateDto);

            return false;
        }

        if(pendingOperations.putIfAbsent(id, true) != null){
            ErrorMessageCreateDto errorMessageCreateDto = new ErrorMessageCreateDto();
            errorMessageCreateDto.setVacuumId(vacuum.getId());
            errorMessageCreateDto.setBookedOperation("DISCHARGE OPERATION");
            errorMessageCreateDto.setError("OTHER OPERATION IS ALREADY BEING PERFORMED");
            errorMessageService.addError(errorMessageCreateDto);

            return false;
        }

        Thread thread = new Thread(() -> operationsVacuumAsync(id, 30000, Status.DISCHARGING));
        thread.start();

        return true;
    }

    @Scheduled(fixedDelay = 60000) // na svaki min proveri
    public void automaticDischargeVacuum() {
        System.out.println("Scheduled task 'automaticDischargeVacuum' started.");
        List<VacuumEntity> vacuums = new ArrayList<>();
        vacuumRepository.findAll().
                forEach(vacuumEntity -> {
                        vacuums.add(vacuumEntity);
                });

        for(VacuumEntity vacuumEntity : vacuums){
            if(vacuumEntity.getCycle() == 111){
                System.out.println("Found one");
                Thread thread = new Thread(() -> operationsVacuumAsync(vacuumEntity.getId(), 30000, Status.DISCHARGING));
                thread.start();
            }
        }
    }

    @Override
    public Boolean bookStartOperation(BookOperation bookOperation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {
            Date scheduledDate = dateFormat.parse(bookOperation.getDate());
            if (scheduledDate.after(new Date())) {
                String cronExpression = generateCronExpression(dateFormat.parse(bookOperation.getDate()));
                taskScheduler.schedule(() -> startVacuum(bookOperation.getVacuumId()), new CronTrigger(cronExpression));
                return true;
            }else {
                System.out.println("Zakazani datum je prošao ili je isti kao trenutni datum.");
                return false;
            }
        }catch (ParseException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean bookStopOperation(BookOperation bookOperation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {
            Date scheduledDate = dateFormat.parse(bookOperation.getDate());
            if (scheduledDate.after(new Date())) {
                String cronExpression = generateCronExpression(dateFormat.parse(bookOperation.getDate()));
                taskScheduler.schedule(() -> stopVacuum(bookOperation.getVacuumId()), new CronTrigger(cronExpression));
                return true;
            }else {
                System.out.println("Zakazani datum je prošao ili je isti kao trenutni datum.");
                return false;
            }
        }catch (ParseException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Boolean bookDischargeOperation(BookOperation bookOperation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        try {
            Date scheduledDate = dateFormat.parse(bookOperation.getDate());
            if (scheduledDate.after(new Date())) {
                String cronExpression = generateCronExpression(dateFormat.parse(bookOperation.getDate()));
                taskScheduler.schedule(() -> dischargeVacuum(bookOperation.getVacuumId()), new CronTrigger(cronExpression));
                return true;
            }else {
                System.out.println("Zakazani datum je prošao ili je isti kao trenutni datum.");
                return false;
            }
        }catch (ParseException e){
            e.printStackTrace();
            return false;
        }
    }


    /*-----------*/
    @Async
    public void operationsVacuumAsync(Long vacuumId, int delay, Status futureStatus) {
        try {
            if(futureStatus != Status.DISCHARGING){
                Thread.sleep(delay);
                updateStatus(vacuumId, futureStatus);
            } else {
                // Pola vremena u DISCHARGING stanju
                Thread.sleep(delay / 2);
                updateStatus(vacuumId, Status.DISCHARGING);

                // Druga polovina vremena u STOPPED stanju
                Thread.sleep(delay / 2);
                updateStatus(vacuumId, Status.OFF);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error during delay", e);
        }finally {
            pendingOperations.remove(vacuumId);
        }
    }

    @Transactional
    public void updateStatus(Long vacuumId, Status futureStatus) {
        VacuumEntity vacuum = vacuumRepository.findById(vacuumId)
                .orElseThrow(() -> new NotFoundException("Vacuum not found with id: " + vacuumId));

        vacuum.setStatus(futureStatus);



        if(futureStatus.equals(Status.OFF)){
            if(((vacuum.getCycle() / 100) % 10) == 0){
                vacuum.setCycle(vacuum.getCycle() + 100);
            }
            if(vacuum.getCycle() == 111){
                vacuum.setCycle(100);
            }
        } else if (futureStatus.equals(Status.ON)){
            if(((vacuum.getCycle() / 10) % 10) == 0){
                vacuum.setCycle(vacuum.getCycle() + 10);
            }

        } else if (futureStatus.equals(Status.DISCHARGING)){
            if((vacuum.getCycle() % 10) == 0){
                vacuum.setCycle(vacuum.getCycle() + 1);
            }
        }

        //vrv mi ne treba, ali neka ostane
        try {
            vacuumRepository.save(vacuum);
        } catch (OptimisticLockingFailureException e) {
            throw new OperationNotAllowed("Other process is already started");
        }
    }

    /*-----------*/

    private String generateCronExpression(Date executionDate) {
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");

        return String.format("* %s %s %s %s *",
                minuteFormat.format(executionDate),
                hourFormat.format(executionDate),
                dayFormat.format(executionDate),
                monthFormat.format(executionDate));
    }

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
