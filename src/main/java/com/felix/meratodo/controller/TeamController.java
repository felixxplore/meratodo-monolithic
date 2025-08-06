package com.felix.meratodo.controller;

import com.felix.meratodo.dto.*;
import com.felix.meratodo.model.TeamInvitation;
import com.felix.meratodo.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
@Tag(name = "Team APIs", description = "Team Management")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @PostMapping
    @Operation(summary = "create new team")
    public ResponseEntity<TeamResponseDto> createTeam(@RequestBody TeamCreateDto dto){
        return ResponseEntity.ok(teamService.createTeam(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "update team by id")
    public ResponseEntity<TeamResponseDto> updateTeamById(@PathVariable Long id, @RequestBody TeamCreateDto dto){
        return ResponseEntity.ok(teamService.updateTeamById(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete team by id")
    public ResponseEntity<String> deleteTeamById(@PathVariable Long id){
        teamService.deleteTeamById(id);
        return ResponseEntity.ok("Delete Team Successfully.");
    }

    @GetMapping("/owner-teams")
    @Operation(summary = "get my teams")
    public ResponseEntity<List<TeamResponseDto>>  getMyTeams(){
        return ResponseEntity.ok(teamService.getMyTeams());
    }

    @GetMapping
    @Operation(summary = "get all teams")
     public ResponseEntity<List<TeamResponseDto>> getAllTeams(){
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    @Operation(summary = "get team by id")
    public ResponseEntity<TeamResponseDto> getTeamById(@PathVariable Long id){
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "get team members by team id")
    public ResponseEntity<List<TeamMembershipResponseDto>> getTeamMembersByTeamId(Long id){
        return ResponseEntity.ok(teamService.getTeamMembersByTeamId(id));
    }

    @GetMapping("/{id}/projects")
    @Operation(summary = "get team projects")
    public ResponseEntity<List<ProjectResponseDto>> getTeamProjects(Long id){
        return ResponseEntity.ok(teamService.getTeamProjects(id));
    }

    @PostMapping("/invite")
    @Operation(summary = "send team invitation request")
    public ResponseEntity<?> inviteMembers(@RequestBody TeamInvitationRequest request) throws MessagingException {
        teamService.sendInvitation(request);
        return ResponseEntity.ok("Invitation Sent.");
    }

    @PostMapping("/invite/accept")
    @Operation(summary = "accept team invitation")
    public ResponseEntity<?> acceptInvitation(@RequestBody AcceptInvitationRequest request){
        teamService.acceptInvitation(request.getToken());
        return ResponseEntity.ok("Invitation Accepted.");
    }

    @PostMapping("/invite/reject")
    @Operation(summary = "reject team invitation")
    public ResponseEntity<?> rejectInvitation(@RequestBody AcceptInvitationRequest request){
        teamService.rejectInvitation(request.getToken());
        return ResponseEntity.ok("Invitation Rejected");
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "update member role in team")
    public ResponseEntity<?> updateMemberRole(@PathVariable  Long id, UpdateMemberRoleRequest request){
        teamService.updateMemberRole(id,request);
        return ResponseEntity.ok("Role Updated.");
    }

    @DeleteMapping("/{id}/member/{userId}")
    @Operation(summary = "remove member from team")
    public ResponseEntity<?> removeMember(@PathVariable Long id, @PathVariable Long userId){
        teamService.removeMember(id,userId);
        return ResponseEntity.ok("Member removed");
    }

    @GetMapping("/{id}/invitations")
    @Operation(summary = "get pending invitations, not provide mail confirmation from member side")
    public ResponseEntity<List<TeamInvitation>> getPendingInvitations(@PathVariable Long id){
       return ResponseEntity.ok(teamService.getPendingInvitations(id));
    }
}
