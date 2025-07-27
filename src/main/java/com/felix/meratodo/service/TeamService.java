package com.felix.meratodo.service;

import com.felix.meratodo.dto.TeamCreateDto;
import com.felix.meratodo.enums.TeamRole;
import com.felix.meratodo.exception.TeamNotFoundException;
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

    public Team createTeam(TeamCreateDto dto) {
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

        return team;

    }



    public Team updateTeamById(Long id, TeamCreateDto dto) {
        Team team=teamRepository.findById(id).orElseThrow(()-> new TeamNotFoundException("Team not found."));

        team.setName(dto.getName());
//        team.setOwner(dto.getOwner());
        team.setDescription(dto.getDescription());
        team.setProjects(dto.getProjects());
        team.setMemberships(dto.getMemberships());
        team.setUpdatedAt(LocalDateTime.now());
        return teamRepository.save(team);
    }

    public void deleteTeamById(Long id) {

        if(!teamRepository.existsById(id)) throw new TeamNotFoundException("Team not found.");
        teamRepository.deleteById(id);

    }

    public List<Team> getMyTeams(){
        User currentUser = getCurrentUser();
        return teamMembershipRepository.findByUserId(currentUser.getId()).stream().map(TeamMembership::getTeam).toList();
    }

    public List<Team> getAllTeams(){
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id){
        return teamRepository.findById(id).orElseThrow(()-> new TeamNotFoundException("Team not found."));
    }

    public List<TeamMembership> getTeamMembersByTeamId(Long teamId){
        return teamMembershipRepository.findByTeamId(teamId);
    }

    public List<Project> getTeamProjects(Long id){
       return projectRepository.findByTeamId(id);
    }



    public User getCurrentUser(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found."));

    }

}
