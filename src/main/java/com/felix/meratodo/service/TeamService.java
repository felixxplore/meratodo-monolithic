package com.felix.meratodo.service;

import com.felix.meratodo.dto.*;
import com.felix.meratodo.enums.TeamRole;
import com.felix.meratodo.exception.TeamNotFoundException;
import com.felix.meratodo.mapper.ProjectMapper;
import com.felix.meratodo.mapper.TeamMapper;
import com.felix.meratodo.mapper.TeamMembershipMapper;
import com.felix.meratodo.model.*;
import com.felix.meratodo.repository.*;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.constant.Constable;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamMembershipRepository teamMembershipRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private TeamMembershipMapper teamMembershipMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private TeamInvitationRepository teamInvitationRepository;

    @Autowired
    private EmailService emailService;

    public TeamResponseDto createTeam(TeamCreateDto dto) {
        User currentUser = getCurrentUser();
        Team team=new Team();
        team.setOwner(currentUser);
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());
        team=teamRepository.save(team);

        TeamMembership teamMembership=new TeamMembership();
        teamMembership.setUser(currentUser);
        teamMembership.setTeam(team);
        teamMembership.setTeamRole(TeamRole.OWNER);
        teamMembershipRepository.save(teamMembership);

        return teamMapper.toDto(team);
    }



    public TeamResponseDto updateTeamById(Long id, TeamCreateDto dto) {
        Team team=teamRepository.findById(id).orElseThrow(()-> new TeamNotFoundException("Team not found."));

        team.setName(dto.getName());
//        team.setOwner(dto.getOwner());
        team.setDescription(dto.getDescription());
        team.setUpdatedAt(LocalDateTime.now());
         Team updatedTeam= teamRepository.save(team);

         return teamMapper.toDto(team);
    }

    public void deleteTeamById(Long id) {

        if(!teamRepository.existsById(id)) throw new TeamNotFoundException("Team not found.");
        teamRepository.deleteById(id);

    }

    public List<TeamResponseDto> getMyTeams(){
        User currentUser = getCurrentUser();
        List<Team> teams=teamMembershipRepository.findByUserId(currentUser.getId()).stream().map(TeamMembership::getTeam).toList();

        return teamMapper.toDto(teams);
    }

    public List<TeamResponseDto> getAllTeams(){
       return teamMapper.toDto(teamRepository.findAll());
    }

    public TeamResponseDto getTeamById(Long id){
        Team team=teamRepository.findById(id).orElseThrow(()-> new TeamNotFoundException("Team not found."));
        return teamMapper.toDto(team);
    }

    public List<TeamMembershipResponseDto> getTeamMembersByTeamId(Long teamId){
        return teamMembershipMapper.toDto(teamMembershipRepository.findByTeamId(teamId));
    }

    public List<ProjectResponseDto> getTeamProjects(Long id){
       return projectMapper.toDto(projectRepository.findByTeamId(id));
    }

    public User getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found."));

    }

    public void sendInvitation(TeamInvitationRequest request) throws MessagingException {
        // check team exist or not
        Team team = teamRepository.findById(request.getTeamId()).orElseThrow(() -> new TeamNotFoundException("Team not found."));

        String roleStr = request.getTeamRole() != null ? request.getTeamRole() : "VIEWER";
        TeamRole role = TeamRole.valueOf(roleStr.toUpperCase());

        if(role == TeamRole.OWNER){
            throw new RuntimeException("Cannot Invite as Owner");
        }

        String token= UUID.randomUUID().toString();
        TeamInvitation teamInvitation=new TeamInvitation();
        teamInvitation.setEmail(request.getEmail());
        teamInvitation.setTeam(team);
        teamInvitation.setToken(token);
        teamInvitation.setTeamRole(role);
        teamInvitation.setExpiryDate(LocalDateTime.now().plusHours(24));
        teamInvitation.setAccepted(false);

        teamInvitationRepository.save(teamInvitation);

        String inviteUrl="http://localhost:8080/invite?token="+token;

        emailService.sendTeamInvitationEmail(request.getEmail(),inviteUrl,team.getName(), role.name());
    }

    public TeamInvitation verifyToken(String token){
        TeamInvitation teamInvitation = teamInvitationRepository.findByToken(token);
        if(teamInvitation== null){
            throw new RuntimeException("Invalid or Expired token.");
        }

        if(teamInvitation.isExpired() || teamInvitation.isAccepted()){
            throw new RuntimeException("Invalid or Expired token.");
        }

        return teamInvitation;
    }

    public void acceptInvitation(String token){
        // first verify the token is valid or not
        TeamInvitation teamInvitation = verifyToken(token);

        // invite member register or not
        User user= userRepository.findByEmail(teamInvitation.getEmail()).orElseThrow(()-> new UsernameNotFoundException("User not found."));

        // if this true that means user already in team
        if(teamMembershipRepository.existsByTeamIdAndUserId(teamInvitation.getTeam().getId(),user.getId())){
            throw new RuntimeException("User already exists in team.");
        }

        TeamMembership teamMembership=new TeamMembership();
        teamMembership.setTeam(teamInvitation.getTeam());
        teamMembership.setTeamRole(teamInvitation.getTeamRole());
        teamMembership.setUser(user);

        teamMembershipRepository.save(teamMembership);

        teamInvitation.setAccepted(true);
        teamInvitationRepository.save(teamInvitation);
    }


    public void rejectInvitation(String token){
        TeamInvitation teamInvitation = teamInvitationRepository.findByToken(token);
        if(teamInvitation==null){
            throw new RuntimeException("invalid or expired token.");
        }

        teamInvitation.setAccepted(true);
        teamInvitationRepository.save(teamInvitation);
    }


    public void updateMemberRole(Long teamId,  UpdateMemberRoleRequest request){
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("Team not found."));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new UsernameNotFoundException("User not found."));
        TeamMembership membership=teamMembershipRepository.findByTeamIdAndUserId(team.getId(),user.getId());
        if(membership==null){
            throw new RuntimeException("User not in team.");
        }

        String teamRole = request.getTeamRole().toUpperCase();
        TeamRole newRole = TeamRole.valueOf(teamRole);
        if(  newRole == TeamRole.OWNER){
            throw new RuntimeException("Cannot assign OWNER role");
        }
        membership.setTeamRole(newRole);
        teamMembershipRepository.save(membership);
    }

    public void removeMember(Long teamId, Long userId){
           TeamMembership teamMembership = teamMembershipRepository.findByTeamIdAndUserId(teamId, userId);
           if(teamMembership==null){
               throw new RuntimeException("User not in team.");
           }

           if(teamMembership.getTeamRole()== TeamRole.OWNER){
               throw new RuntimeException("Cannot remove team owner");
           }

           teamMembershipRepository.delete(teamMembership);
    }


    public List<TeamInvitation> getPendingInvitations(Long teamId) {

        return teamInvitationRepository.findByTeamIdAndAcceptedFalse(teamId);
    }
}
