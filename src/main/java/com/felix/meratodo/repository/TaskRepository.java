package com.felix.meratodo.repository;

import com.felix.meratodo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByAssigneesId(Long userId);
}
