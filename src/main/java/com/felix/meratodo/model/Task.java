package com.felix.meratodo.model;

import com.felix.meratodo.enums.TaskPriority;
import com.felix.meratodo.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    private String description;

    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority=TaskPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status=TaskStatus.TODO;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany
    @JoinTable(
            name = "task_assignments",
            joinColumns = @JoinColumn(name="task_id"),
            inverseJoinColumns = @JoinColumn(name= "user_id")
    )
    private Set<User> assignees;

    private boolean isDeleted=false;

    @Column(updatable = false)
    private LocalDateTime createdAt= LocalDateTime.now();

    private LocalDateTime updatedAt=LocalDateTime.now();


}
