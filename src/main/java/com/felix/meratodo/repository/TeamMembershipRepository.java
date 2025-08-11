package com.felix.meratodo.repository;

import com.felix.meratodo.model.TeamMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeamMembershipRepository extends JpaRepository<TeamMembership, Long> {

    List<TeamMembership> findByUserId(Long userId);

    List<TeamMembership> findByTeamId(Long id);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    TeamMembership findByTeamIdAndUserId(Long teamId,Long userId);


    @Query("SELECT tm FROM TeamMemberships tm WHERE tm.user.email= :email AND tm.team.id= :teamId")
    Optional<TeamMembership> findByUserEmailAndTeamId(String email, Long teamId);
}