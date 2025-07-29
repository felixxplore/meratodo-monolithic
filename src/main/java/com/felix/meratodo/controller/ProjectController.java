package com.felix.meratodo.controller;

import com.felix.meratodo.dto.ProjectRequestDto;
import com.felix.meratodo.dto.ProjectResponseDto;
import com.felix.meratodo.dto.TaskResponseDto;
import com.felix.meratodo.service.ProjectService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponseDto> createProject(@RequestBody ProjectRequestDto dto){
        return ResponseEntity.ok(projectService.createProject(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDto> updateProjectById(@PathVariable Long id, @RequestBody ProjectRequestDto dto){
        return ResponseEntity.ok(projectService.updateProjectById(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProjectById(@PathVariable Long id){
        projectService.deleteProjectById(id);
        return ResponseEntity.ok("Project Deleted Successfully.");

    }

    @GetMapping
    public ResponseEntity<?> getAllProjects(){
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Long id){
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PostMapping("/{projectId}/assign-team/{teamId}")
    public ResponseEntity<?> assignProjectToTeam(@PathVariable Long projectId, @PathVariable Long teamId){
        projectService.assignProjectToTeam(projectId,teamId);
        return ResponseEntity.ok("Project Assign to team.");
    }

    @PutMapping("/{id}/remove-team")
    public ResponseEntity<?> removeTeamFromProject(@PathVariable Long id){
        projectService.removeTeamFromProject(id);
        return ResponseEntity.ok("Removed Project from Team.");
    }

    @PutMapping("/{id}/archive")
    public  ResponseEntity<?> movePrjoectToArchive(@PathVariable Long id){
        projectService.moveProjectToArchive(id);
        return ResponseEntity.ok("Project Move to Archive");
    }

    @GetMapping("/my-projects")
    public ResponseEntity<List<ProjectResponseDto>> getMyProjects(){
        return ResponseEntity.ok(projectService.getMyProjects());
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<TaskResponseDto>> getProjectTasks(@PathVariable Long id){
        return ResponseEntity.ok(projectService.getProjectTasks(id));
    }
}
