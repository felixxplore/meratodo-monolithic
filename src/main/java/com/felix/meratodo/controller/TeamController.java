package com.felix.meratodo.controller;

import com.felix.meratodo.dto.TeamCreateDto;
import com.felix.meratodo.model.Team;
import com.felix.meratodo.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Team> updateTeamById(@RequestParam Long id, @RequestBody TeamCreateDto dto){
        return ResponseEntity.ok(teamService.updateTeamById(id, dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeamById(@RequestParam Long id){
        teamService.deleteTeamById(id);
        return ResponseEntity.ok("Delete Team Successfully.");
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?>  getAllTeamInfoByUserId(@RequestParam Long id){
        return ResponseEntity.status(HttpStatus.FOUND).body(teamService.getAllTeamInfoByUserId(id));
    }

}
