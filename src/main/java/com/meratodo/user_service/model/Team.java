package com.meratodo.user_service.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "teams")
@Getter
@Setter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<Project> projects;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private Set<TeamMembership> memberships;

    @Column(updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

    private  LocalDateTime updatedAt=LocalDateTime.now();

}
