package com.felix.meratodo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @OneToOne
    private User user;
    private LocalDateTime expiryDate;

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiryDate);
    }

}
