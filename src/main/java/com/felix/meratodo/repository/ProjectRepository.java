package com.felix.meratodo.repository;

import com.felix.meratodo.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {

    List<Project> findByTeamId(Long id);
}
