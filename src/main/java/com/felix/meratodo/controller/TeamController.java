package com.felix.meratodo.controller;

import com.felix.meratodo.dto.TeamCreateDto;
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
    public ResponseEntity<Team> createTeam(@RequestBody TeamCreateDto dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeamById(@PathVariable Long id, @RequestBody TeamCreateDto dto){
        return ResponseEntity.ok(teamService.updateTeamById(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeamById(@PathVariable Long id){
        teamService.deleteTeamById(id);
        return ResponseEntity.ok("Delete Team Successfully.");
    }

    @GetMapping("/owner-teams")
    public ResponseEntity<List<Team>>  getMyTeams(){
        return ResponseEntity.status(HttpStatus.FOUND).body(teamService.getMyTeams());
    }

    @GetMapping
     public ResponseEntity<List<Team>> getAllTeams(){
        return ResponseEntity.status(HttpStatus.FOUND).body(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeamById(@PathVariable Long teamId){
        return ResponseEntity.status(HttpStatus.FOUND).body(teamService.getTeamById(teamId));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<TeamMembership>> getTeamMembersByTeamId(Long teamId){
        return ResponseEntity.status(HttpStatus.FOUND).body(teamService.getTeamMembersByTeamId(teamId));
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<List<Project>> getTeamProjects(Long teamId){
        return ResponseEntity.status(HttpStatus.FOUND).body(teamService.getTeamProjects(teamId));
    }


}
