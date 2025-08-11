package com.felix.meratodo.service;


import com.felix.meratodo.dto.TokenResponse;
import com.felix.meratodo.dto.UserLoginDTO;
import com.felix.meratodo.dto.UserRegistrationDTO;
import com.felix.meratodo.dto.UserResponseDto;
import com.felix.meratodo.enums.TokenType;
import com.felix.meratodo.exception.InvalidTokenException;
import com.felix.meratodo.exception.UserAlreadyExistsException;
import com.felix.meratodo.mapper.UserMapper;
import com.felix.meratodo.model.AuditLog;
import com.felix.meratodo.model.PasswordResetToken;
import com.felix.meratodo.model.Token;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.AuditLogRepository;
import com.felix.meratodo.repository.TokenRepository;
import com.felix.meratodo.repository.UserRepository;
import dev.samstevens.totp.code.CodeVerifier;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AuthService {

    // declare a static final logger instance
    private static final Logger logger= LoggerFactory.getLogger(AuthService.class);

    private final int MAX_LOGIN_ATTEMPTS=5;
    private final int LOCK_DURATION_MINUTES=15;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private  AuthenticationManager authenticationManager;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder(12);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuditLog auditLog;
    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private CodeVerifier codeVerifier;


    // methods
     public TokenResponse register(UserRegistrationDTO dto) {

         PasswordValidator.validate(dto.getPassword()); // handle password

        if(userRepository.existsByEmail(dto.getEmail())){ // check user already exists or not
            throw  new UserAlreadyExistsException(dto.getEmail());
        }
        User newUser=new User();
        newUser.setName(dto.getName());
        newUser.setEmail(dto.getEmail());
        newUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        userRepository.save(newUser);

        String verificationToken= UUID.randomUUID().toString();
        Token token=new Token();
        token.setEmail(dto.getEmail());
        token.setToken(verificationToken);
        token.setType(TokenType.VERIFICATION);
        tokenRepository.save(token);

         try {
             emailService.sendVerificationEmail(dto.getEmail(), verificationToken);
         } catch (MessagingException e) {
             throw new RuntimeException(e);
         }

         logAuditEvent(dto.getEmail(),"REGISTRATION", "User Register Successfully");

//         return new TokenResponse(jwtService.generateAccessToken(newUser),jwtService.generateRefreshToken(newUser));

     }

     public void verifyEmail(String token){
         Token verificationToken = tokenRepository.findByTokenAndType(token, String.valueOf(TokenType.VERIFICATION)).orElseThrow(()-> new InvalidTokenException("Invalid Verification Token"));
         User user = userRepository.findByEmail(verificationToken.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
         user.setEmailVarified(true);
         userRepository.save(user);
         tokenRepository.delete(verificationToken);
         logAuditEvent(user.getEmail(),"EMAIL_VARIFIED", "Email verified Successfully");

     }


     public TokenResponse login(UserLoginDTO dto)   {

         User user = userRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

         if(user.getLockedUntil() != null && user.getLockedUntil() > Instant.now().toEpochMilli()){
             try {
                 throw new AccountLockedException("Account is locked until "+Instant.ofEpochMilli(user.getLockedUntil()));
             } catch (AccountLockedException e) {

              }
         }

         try{
             authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(),dto.getPassword()));
             user.setFailedLoginAttempts(0);
         }catch(Exception e){
            user.setFailedLoginAttempts(user.getFailedLoginAttempts()+1);
            if(user.getFailedLoginAttempts() >= MAX_LOGIN_ATTEMPTS){
                user.setLockedUntil(Instant.now().plusSeconds(LOCK_DURATION_MINUTES * 60).toEpochMilli());
                userRepository.save(user);
                logAuditEvent(dto.getEmail(),"LOGIN_FAILED","Account locked for "+MAX_LOGIN_ATTEMPTS+" after attempts");
                try {
                    throw new AccountLockedException("Account locked for "+ LOCK_DURATION_MINUTES+" minutes");
                } catch (AccountLockedException ex) {
                 }
            }
            userRepository.save(user);
            logAuditEvent(dto.getEmail(),"LOGIN_FAILED","Invalid Credentials");
         }

         if(!user.isEmailVarified()) throw new InvalidTokenException("Email not verified");

         if(user.isMfaEnabled()){
             if(dto.getTotp()== null || !codeVerifier.isValidCode( user.getTotpSecret(), dto.getTotp())){
                 logAuditEvent(dto.getEmail(), "MFA_FAILED","Invalid OTP TOTP code");
                 throw new InvalidTokenException("Invalid TOTP code");
             }
         }


         String refreshToken = jwtService.generateRefreshToken(user);
         Token token=new Token();
         token.setType(TokenType.REFRESH);
         token.setToken(refreshToken);
         token.setEmail(user.getEmail());
         tokenRepository.save(token);

         logAuditEvent(user.getEmail(), "LOGIN_SUCCESS","User logged in successfully");
         return new TokenResponse(jwtService.generateAccessToken(user), refreshToken);

     }

     public void requestPasswordReset(String email) throws MessagingException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken=new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(passwordResetToken);

         System.out.println("request password reset....");

        emailService.sendPasswordResetEmail(email, token);
    }

     public void resetPassword(String token, String newPassword) {

         PasswordResetToken resetToken = tokenRepository.findByToken(token).orElseThrow(()-> new InvalidTokenException("Invalid Token"));

         if(resetToken.isExpired()){
             throw new RuntimeException("Token expired");
         }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }


    private void logAuditEvent(String email, String action, String details){
        AuditLog auditLog = new AuditLog();
        auditLog.setEmail(email);
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLog.setTimestamp(Instant.now());
        auditLogRepository.save(auditLog);

        logger.info("Audit: {} - {} - {}", email,action,details);

    }
}
