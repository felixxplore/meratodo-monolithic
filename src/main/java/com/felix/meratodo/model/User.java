package com.felix.meratodo.model;


import com.felix.meratodo.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String avatarUrl;

    //    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Project> ownedProjects;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<TeamMembership> teamMemberships;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<TaskAssignment> taskAssignments;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();


    private String resetToken;

    private Long resetTokenExpiry;

    private boolean emailVarified = false;
    private boolean twoStepVerificationEnabled = false;
    private String otpSecret;
    private int failedLoginAttempts = 0;
    private Long lockedUntil;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" +  getRole().toString()));
    }

    @Override
    public String getUsername() {
        return email;
    }

}
