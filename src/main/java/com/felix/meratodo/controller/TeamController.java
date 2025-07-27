package com.felix.meratodo.controller;

import com.felix.meratodo.dto.ProjectResponseDto;
import com.felix.meratodo.dto.TeamCreateDto;
import com.felix.meratodo.dto.TeamMembershipResponseDto;
import com.felix.meratodo.dto.TeamResponseDto;
import com.felix.meratodo.model.Project;
import com.felix.meratodo.model.Team;
import com.felix.meratodo.model.TeamMembership;
import com.felix.meratodo.service.TeamService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamResponseDto> createTeam(@RequestBody TeamCreateDto dto){
        return ResponseEntity.ok(teamService.createTeam(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponseDto> updateTeamById(@PathVariable Long id, @RequestBody TeamCreateDto dto){
        return ResponseEntity.ok(teamService.updateTeamById(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeamById(@PathVariable Long id){
        teamService.deleteTeamById(id);
        return ResponseEntity.ok("Delete Team Successfully.");
    }

    @GetMapping("/owner-teams")
    public ResponseEntity<List<TeamResponseDto>>  getMyTeams(){
        return ResponseEntity.ok(teamService.getMyTeams());
    }

    @GetMapping
     public ResponseEntity<List<TeamResponseDto>> getAllTeams(){
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponseDto> getTeamById(@PathVariable Long id){
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<TeamMembershipResponseDto>> getTeamMembersByTeamId(Long id){
        return ResponseEntity.ok(teamService.getTeamMembersByTeamId(id));
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<ProjectResponseDto>> getTeamProjects(Long id){
        return ResponseEntity.ok(teamService.getTeamProjects(id));
    }


}
