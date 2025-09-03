package com.felix.meratodo.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.meratodo.dto.TokenResponse;
import com.felix.meratodo.enums.Role;
import com.felix.meratodo.enums.TokenType;
import com.felix.meratodo.model.AuditLog;
import com.felix.meratodo.model.Token;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.AuditLogRepository;
import com.felix.meratodo.repository.TokenRepository;
import com.felix.meratodo.repository.UserRepository;
import com.felix.meratodo.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Component
@Slf4j
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;


    @Autowired
    private TokenRepository tokenRepository;

    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder(12);

    @Autowired
    private AuditLogRepository auditLogRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();

        log.info("✅ CustomOAuth2SuccessHandler triggered for user: {}",attributes.get("email"));

        TokenResponse tokens= handleOAuth2Login( attributes);
//            response.sendRedirect("/api/auth/oauth2/success?accessToken="+tokens.getAccessToken()+"&refreshToken="+tokens.getRefreshToken());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), tokens);
    }

  private TokenResponse handleOAuth2Login(  Map<String, Object> attributes) {

        User user = userRepository.findByEmail((String) attributes.get("email")).orElseGet(() ->
        {
            User newUser = new User();
            newUser.setEmail((String) attributes.get("email"));
            newUser.setRole(Role.USER);
            newUser.setName((String) attributes.getOrDefault("name", "OAuth2 User"));
            newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            newUser.setEmailVarified(true);
            userRepository.save(newUser);
            return newUser;
        });

        List<Token> tokenList = tokenRepository.findByEmailAndType(user.getEmail(), TokenType.REFRESH);
        tokenList.forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(tokenList);

        String refreshToken = jwtService.generateRefreshToken(user);
        Token token=new Token();
        token.setType(TokenType.REFRESH);
        token.setEmail(user.getEmail());
        token.setToken(refreshToken);
        tokenRepository.save(token);
        logAuditEvent(user.getEmail(),"OAUTH2_LOGIN_SUCCESS","OAuth2 login successfully");
        return new TokenResponse(jwtService.generateAccessToken(user),refreshToken);

    }

    private void logAuditEvent(String email, String action, String details){
        AuditLog auditLog = new AuditLog();
        auditLog.setEmail(email);
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLog.setTimestamp(Instant.now());
        auditLogRepository.save(auditLog);

        log.info("Audit: {} - {} - {}", email,action,details);

    }

}
