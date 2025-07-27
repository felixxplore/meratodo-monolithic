package com.felix.meratodo.repository;

import com.felix.meratodo.model.TeamInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamInvitationRepository extends JpaRepository<TeamInvitation,Long> {


    TeamInvitation findByToken(String token);

    List<TeamInvitation> findByTeamIdAndAcceptedFalse(Long teamId);
}
