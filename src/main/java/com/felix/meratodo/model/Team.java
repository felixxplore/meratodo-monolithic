package com.felix.meratodo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
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

    @ManyToOne
    @JoinColumn(name = "owner_id",nullable = false)
    private User owner;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Project> projects=new HashSet<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TeamMembership> memberships=new HashSet<>();

    @Column(updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

    private  LocalDateTime updatedAt=LocalDateTime.now();

    @PreUpdate
    public void preUpdate(){
        this.updatedAt=LocalDateTime.now();
    }
}
