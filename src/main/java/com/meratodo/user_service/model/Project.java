package com.meratodo.user_service.model;

import com.meratodo.user_service.enums.ProjectPermission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private ProjectPermission permission=ProjectPermission.PRIVATE;

    private boolean isArchived=false;

    @ManyToOne
    @JoinColumn(name = "owner_id",nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name="team_id")
    private Team team;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private Set<Task> tasks;

    @Column(updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();

    private LocalDateTime updatedAt=LocalDateTime.now();
}
