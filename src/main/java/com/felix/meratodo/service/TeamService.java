package com.felix.meratodo.service;

import com.felix.meratodo.dto.ProjectResponseDto;
import com.felix.meratodo.dto.TeamCreateDto;
import com.felix.meratodo.dto.TeamMembershipResponseDto;
import com.felix.meratodo.dto.TeamResponseDto;
import com.felix.meratodo.enums.TeamRole;
import com.felix.meratodo.exception.TeamNotFoundException;
import com.felix.meratodo.mapper.ProjectMapper;
import com.felix.meratodo.mapper.TeamMapper;
import com.felix.meratodo.mapper.TeamMembershipMapper;
import com.felix.meratodo.model.Project;
import com.felix.meratodo.model.Team;
import com.felix.meratodo.model.TeamMembership;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.ProjectRepository;
import com.felix.meratodo.repository.TeamMembershipRepository;
import com.felix.meratodo.repository.TeamRepository;
import com.felix.meratodo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

}
