package com.felix.meratodo.model;


import com.felix.meratodo.enums.TokenType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String token;
    private String email;
    private TokenType type; // VERIFICATION, RESET, REFRESH
    private boolean revoked=false;

}
