package com.napredno_web.domaci3.repository;

import com.napredno_web.domaci3.model.entity.ErrorMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessageEntity, Long> {

    List<ErrorMessageEntity> findAllByVacuumEntity_Id(Long vacuumId);

}
