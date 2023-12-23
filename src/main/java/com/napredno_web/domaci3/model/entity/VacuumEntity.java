package com.napredno_web.domaci3.model.entity;

import com.napredno_web.domaci3.model.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "vacuums")
public class VacuumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity addedBy;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long dateCreate;

    @Version
    private Long version = 0L;

    @Column(nullable = false)
    private Integer cycle;

}
