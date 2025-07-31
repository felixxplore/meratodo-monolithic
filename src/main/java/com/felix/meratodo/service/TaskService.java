package com.felix.meratodo.service;

import com.felix.meratodo.dto.TaskAssignmentResponseDto;
import com.felix.meratodo.dto.TaskRequestDto;
import com.felix.meratodo.dto.TaskResponseDto;
import com.felix.meratodo.dto.UpdateTaskStatusDto;
import com.felix.meratodo.exception.ResourceNotFoundException;
import com.felix.meratodo.exception.TaskNotFoundException;
import com.felix.meratodo.mapper.TaskAssignmentMapper;
import com.felix.meratodo.mapper.TaskMapper;
import com.felix.meratodo.model.Task;
import com.felix.meratodo.model.TaskAssignment;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.ProjectRepository;
import com.felix.meratodo.repository.TaskAssignmentRepository;
import com.felix.meratodo.repository.TaskRepository;
import com.felix.meratodo.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskAssignmentRepository taskAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskAssignmentMapper taskAssignmentMapper;

    @Autowired
    private NotificationService notificationService;

    public TaskResponseDto createTask(TaskRequestDto dto) {
        return taskMapper.toDto(taskRepository.save(taskMapper.toEntity(dto)));
    }

    public TaskResponseDto getTaskById(Long id){
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("task not found."));
    return taskMapper.toDto(task);
    }


    public TaskResponseDto updateTaskById(Long id, TaskRequestDto dto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("task not found."));
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());

        return taskMapper.toDto(taskRepository.save(task));
    }

    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("task not found."));
        taskRepository.deleteById(id);
    }

    public List<TaskResponseDto> getAllTasks() {
       return taskMapper.toDtoList(taskRepository.findAll());
    }

    public void assignTaskToMember(Long taskId, Long userId) throws MessagingException {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("task not found."));
        User user=userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found "));
        
        task.getAssignees().add(user);
        Task updatedTask=taskRepository.save(task);

        notificationService.notifyAssigneesForTask(updatedTask);

   }

    public void unassignTaskToMember(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("task not found."));
        User user=userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found "));

        task.getAssignees().remove(user);
        Task updatedTask=taskRepository.save(task);
    }

    public void updateTaskStatus(Long id, UpdateTaskStatusDto dto) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("task not found."));
       task.setStatus(dto.getTaskStatus());
       taskRepository.save(task);
    }

    public List<TaskResponseDto> getMyTasks() {
        User currentUser = UserService.getCurrentUser();
        List<Task> tasks = taskRepository.findByAssigneesId(currentUser.getId());
        return taskMapper.toDtoList(tasks);
    }
}
