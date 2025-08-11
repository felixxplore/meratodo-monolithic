package com.felix.meratodo.model;


import com.felix.meratodo.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable=false, unique=true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private UserRole role=UserRole.USER;

    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL)
    private Set<Project> ownedProjects;

    @OneToMany(mappedBy = "user", cascade=CascadeType.ALL)
    private Set<TeamMembership> teamMemberships;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private  Set<TaskAssignment> taskAssignments;

    @Column(updatable=false)
    private LocalDateTime createdAt=LocalDateTime.now();

    private LocalDateTime updatedAt=LocalDateTime.now();


    private String resetToken;

    private Long resetTokenExpiry;

    private boolean emailVarified=false;
    private boolean mfaEnabled=false;
    private String totpSecret;
    private int failedLoginAttempts=0;
    private Long lockedUntil;





}
