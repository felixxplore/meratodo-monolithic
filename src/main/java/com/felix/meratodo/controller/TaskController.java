package com.felix.meratodo.controller;

import com.felix.meratodo.dto.TaskRequestDto;
import com.felix.meratodo.dto.TaskResponseDto;
import com.felix.meratodo.dto.UpdateTaskStatusDto;
import com.felix.meratodo.service.TaskService;
import jakarta.mail.MessagingException;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskRequestDto dto){
    return ResponseEntity.ok(taskService.createTask(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTaskById(@PathVariable Long id, @RequestBody TaskRequestDto dto){
        return ResponseEntity.ok(taskService.updateTaskById(id,dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(@PathVariable Long id){
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(@PathVariable Long id){
        taskService.deleteTaskById(id);
        return ResponseEntity.ok("Delete task successfully.");
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PutMapping("/{id}/assign/{id1}")
    public ResponseEntity<?> assignTaskToMember(@PathVariable Long id, @PathVariable Long id1) throws MessagingException {
        taskService.assignTaskToMember(id,id1);
        return ResponseEntity.ok("Assign task to member.");
    }

    @PutMapping("/{id}/unassign/{id1}")
    public ResponseEntity<?> unassignTaskToMember(@PathVariable Long id,@PathVariable Long id1){
        taskService.unassignTaskToMember(id,id1);
        return ResponseEntity.ok("Unassign task to member.");
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @RequestBody UpdateTaskStatusDto dto){
        taskService.updateTaskStatus(id, dto);
        return ResponseEntity.ok("Update Task Status Successfully.");
    }

    @GetMapping("/my-tasks")
    public ResponseEntity<List<TaskResponseDto>> getMyTasks(){
        return ResponseEntity.ok(taskService.getMyTasks());
    }

}
