package com.felix.meratodo.repository;

import com.felix.meratodo.model.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {

    List<TeamMembership> findByUserId(Long userId);

    List<TeamMembership> findByTeamId(Long id);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    TeamMembership findByTeamIdAndUserId(Long teamId,Long userId);
}