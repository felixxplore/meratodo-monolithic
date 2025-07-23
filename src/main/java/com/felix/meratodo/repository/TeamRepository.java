package com.felix.meratodo.repository;

import com.felix.meratodo.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {

}
