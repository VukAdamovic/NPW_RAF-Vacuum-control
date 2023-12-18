package com.napredno_web.domaci3.repository;

import com.napredno_web.domaci3.model.entity.VacuumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacuumRepository extends JpaRepository<VacuumEntity, Long> {
}
