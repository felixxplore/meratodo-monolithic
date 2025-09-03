package com.felix.meratodo.model;


import com.felix.meratodo.enums.TeamRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_invitations")
@Getter
@Setter
public class TeamInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(name = "team_role", nullable = false)
    private TeamRole teamRole;

    private LocalDateTime expiryDate;

    private boolean accepted;

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
