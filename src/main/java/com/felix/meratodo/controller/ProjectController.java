package com.felix.meratodo.controller;

import com.felix.meratodo.dto.ProjectRequestDto;
import com.felix.meratodo.dto.ProjectResponseDto;
import com.felix.meratodo.dto.TaskResponseDto;
import com.felix.meratodo.model.User;
import com.felix.meratodo.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.Response;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
@Tag(name = "Project APIs", description = "Project Management")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    @Operation(summary = "create new project")
    @PreAuthorize("hasPermission(null, 'PROJECT', 'CREATE')")
    public ResponseEntity<ProjectResponseDto> createProject(@RequestBody ProjectRequestDto dto, Authentication authentication){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(projectService.createProject(dto, user.getId()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "update project by id")
    @PreAuthorize("hasPermission(#id, 'PROJECT', 'UPDATE')")
    public ResponseEntity<ProjectResponseDto> updateProjectById(@PathVariable Long id, @RequestBody ProjectRequestDto dto){
        return ResponseEntity.ok(projectService.updateProjectById(id,dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete project by id")
    public ResponseEntity<?> deleteProjectById(@PathVariable Long id){
        projectService.deleteProjectById(id);
        return ResponseEntity.ok("Project Deleted Successfully.");

    }

    @GetMapping
    @Operation(summary = "get all projects")
    public ResponseEntity<?> getAllProjects(){
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    @Operation(summary = "get project by id")
    public ResponseEntity<?> getProjectById(@PathVariable Long id){
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping("/{projectId}/assign-team/{teamId}")
    @Operation(summary = "assign project to team")
    public ResponseEntity<?> assignProjectToTeam(@PathVariable Long projectId, @PathVariable Long teamId){
        projectService.assignProjectToTeam(projectId,teamId);
        return ResponseEntity.ok("Project Assign to team.");
    }

    @PutMapping("/{id}/remove-team")
    @Operation(summary = "remove team from project")
    public ResponseEntity<?> removeTeamFromProject(@PathVariable Long id){
        projectService.removeTeamFromProject(id);
        return ResponseEntity.ok("Removed Project from Team.");
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "move project to archive")
    public  ResponseEntity<?> movePrjoectToArchive(@PathVariable Long id){
        projectService.moveProjectToArchive(id);
        return ResponseEntity.ok("Project Move to Archive");
    }

    @GetMapping("/my-projects")
    @Operation(summary = "get my projects")
    public ResponseEntity<List<ProjectResponseDto>> getMyProjects(){
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    @GetMapping("/{id}/tasks")
    @Operation(summary = "get project tasks")
    public ResponseEntity<List<TaskResponseDto>> getProjectTasks(@PathVariable Long id){
        return ResponseEntity.ok(projectService.getProjectTasks(id));
    }
}
