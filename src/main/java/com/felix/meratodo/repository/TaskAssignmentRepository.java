package com.felix.meratodo.repository;

import com.felix.meratodo.model.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment,Long> {

        Boolean existsByTask_IdAndUser_Id(Long taskId, Long userId);
}
