package com.felix.meratodo.controller;

import com.felix.meratodo.dto.*;
import com.felix.meratodo.model.TeamInvitation;
import com.felix.meratodo.service.TeamService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/invite")
    public ResponseEntity<?> inviteMembers(@RequestBody TeamInvitationRequest request) throws MessagingException {
        teamService.sendInvitation(request);
        return ResponseEntity.ok("Invitation Sent.");
    }

    @PostMapping("/invite/accept")
    public ResponseEntity<?> acceptInvitation(@RequestBody AcceptInvitationRequest request){
        teamService.acceptInvitation(request.getToken());
        return ResponseEntity.ok("Invitation Accepted.");
    }

    @PostMapping("/invite/reject")
    public ResponseEntity<?> rejectInvitation(@RequestBody AcceptInvitationRequest request){
        teamService.rejectInvitation(request.getToken());
        return ResponseEntity.ok("Invitation Rejected");
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateMemberRole(@PathVariable  Long id, UpdateMemberRoleRequest request){
        teamService.updateMemberRole(id,request);
        return ResponseEntity.ok("Role Updated.");
    }

    @DeleteMapping("/{id}/member/{userId}")
    public ResponseEntity<?> removeMember(@PathVariable Long id, @PathVariable Long userId){
        teamService.removeMember(id,userId);
        return ResponseEntity.ok("Member removed");
    }

    @GetMapping("/{id}/invitations")
    public ResponseEntity<List<TeamInvitation>> getPendingInvitations(@PathVariable Long id){
       return ResponseEntity.ok(teamService.getPendingInvitations(id));
    }
}
