package com.napredno_web.domaci3.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "errors")
public class ErrorMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vacuum_id", nullable = false)
    private VacuumEntity vacuumEntity;

    private String bookedOperation;

    private String error;
}
