package com.felix.meratodo.repository;

import com.felix.meratodo.model.PasswordResetToken;
import com.felix.meratodo.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

  Optional<Token>  findByTokenAndType(String toke, String type);
  List<Token> findByEmailAndType(String email, String type);

}
