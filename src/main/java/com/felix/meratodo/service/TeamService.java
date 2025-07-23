package com.felix.meratodo.service;

import com.felix.meratodo.dto.TeamCreateDto;
import com.felix.meratodo.exception.TeamNotFoundException;
import com.felix.meratodo.model.Team;
import com.felix.meratodo.repository.TeamRepository;
import com.felix.meratodo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;

    public Team createTeam(TeamCreateDto dto) {
        Team team=new Team();
        team.setName(dto.getName());
        team.setDescription(dto.getDescription());
        team.setProjects(dto.getProjects());
        team.setMemberships(dto.getMemberships());
        return teamRepository.save(team);
    }

    public Team updateTeamById(Long id, TeamCreateDto dto) {
        Team team=teamRepository.findById(id).orElseThrow(()-> new TeamNotFoundException("Team not found."));

        team.setName(dto.getName());
        team.setDescription(dto.getDescription());
        team.setProjects(dto.getProjects());
        team.setMemberships(dto.getMemberships());
        team.setUpdatedAt(LocalDateTime.now());
        return teamRepository.save(team);
    }

    public void deleteTeamById(Long id) {

        if(!userRepository.existsById(id)) throw new TeamNotFoundException("Team not found.");

        userRepository.deleteById(id);

    }

    public Object getAllTeamInfoByUserId(Long id) {
        Team team=teamRepository.findById(id).orElseThrow(()-> new TeamNotFoundException("Team not found."));
        return team;
    }
}
