package com.napredno_web.domaci3.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Data
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean canCreateUsers;

    @Column(nullable = false)
    private boolean canReadUsers;

    @Column(nullable = false)
    private boolean canUpdateUsers;

    @Column(nullable = false)
    private boolean canDeleteUsers;

    // dodate permisije

    @Column(nullable = false)
    private boolean canSearchVacuum;

    @Column(nullable = false)
    private boolean canStartVacuum;

    @Column(nullable = false)
    private boolean canStopVacuum;

    @Column(nullable = false)
    private boolean canDischargeVacuum;

    @Column(nullable = false)
    private boolean canAddVacuum;

    @Column(nullable = false)
    private boolean canRemoveVacuum;

    public void setHashPassword(String plainTextPassword) {
        this.password = new BCryptPasswordEncoder().encode(plainTextPassword);
    }
}
