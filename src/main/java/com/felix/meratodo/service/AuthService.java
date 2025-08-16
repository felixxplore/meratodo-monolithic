package com.felix.meratodo.service;


import com.felix.meratodo.dto.TokenResponse;
import com.felix.meratodo.dto.UserLoginDTO;
import com.felix.meratodo.dto.UserRegistrationDTO;
import com.felix.meratodo.enums.TokenType;
import com.felix.meratodo.enums.UserRole;
import com.felix.meratodo.exception.*;
import com.felix.meratodo.mapper.UserMapper;
import com.felix.meratodo.model.AuditLog;
import com.felix.meratodo.model.PasswordResetToken;
import com.felix.meratodo.model.Token;
import com.felix.meratodo.model.User;
import com.felix.meratodo.repository.AuditLogRepository;
import com.felix.meratodo.repository.PasswordResetTokenRepository;
import com.felix.meratodo.repository.TokenRepository;
import com.felix.meratodo.repository.UserRepository;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import io.jsonwebtoken.Jwts;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    // declare a static final logger instance
    private static final Logger logger= LoggerFactory.getLogger(AuthService.class);

    private final int MAX_LOGIN_ATTEMPTS=5;
    private final int LOCK_DURATION_MINUTES=15;
    private final int OTP_LENGTH=6;
    private final long OTP_EXPIRE_MINUTES=5;
    private final int OTP_MAX_ATTEMPTS=5;
    private final String OTP_PREFIX="mfa:otp:";


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
    private AuditLogRepository auditLogRepository;


    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // methods
     public void register(UserRegistrationDTO dto) {

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
         Token verificationToken = tokenRepository.findByTokenAndType(token, TokenType.VERIFICATION).orElseThrow(()-> new InvalidTokenException("Invalid Verification Token"));
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

         if(user.isTwoStepVerificationEnabled()){
             String otp= generateOtp();
             redisTemplate.opsForValue().set("otp:"+user.getEmail(), otp, OTP_EXPIRE_MINUTES, TimeUnit.MINUTES);
             try {
                 emailService.sendOtpEmail(user.getEmail(), otp);
             } catch (MessagingException e) {
                 throw new RuntimeException(e);
             }
             logAuditEvent(user.getEmail(),"OTP_SENT","OTP sent for 2-step verification");
             return new TokenResponse("OTP_SENT");
         }


         List<Token> tokenList = tokenRepository.findByEmailAndType(user.getEmail(), TokenType.REFRESH);
         tokenList.forEach(t -> t.setRevoked(true));
         tokenRepository.saveAll(tokenList);


         String refreshToken = jwtService.generateRefreshToken(user);
         Token token=new Token();
         token.setType(TokenType.REFRESH);
         token.setToken(refreshToken);
         token.setEmail(user.getEmail());
         tokenRepository.save(token);

         logAuditEvent(user.getEmail(), "LOGIN_SUCCESS","User logged in successfully");
         return new TokenResponse(jwtService.generateAccessToken(user), refreshToken);

     }



      private String generateOtp(){
          SecureRandom secureRandom=new SecureRandom();
          StringBuilder stringBuilder=new StringBuilder();

          for(int i=0;i<OTP_LENGTH;i++){
              stringBuilder.append(secureRandom.nextInt(10));
          }

          return stringBuilder.toString();
      }


     public void requestPasswordReset(String email) throws MessagingException {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken=new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        passwordResetTokenRepository.save(passwordResetToken);
        emailService.sendPasswordResetEmail(email, token);

        logAuditEvent(email,"PASSWORD_RESET_LINK_SENT", "Sent Password reset link");
    }

     public void resetPassword(String token, String newPassword) {

         PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(()-> new InvalidTokenException("Invalid Token"));

         User user = userRepository.findByEmail(resetToken.getUser().getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
         PasswordValidator.validate(newPassword);

         if(resetToken.isExpired()){
             throw new RuntimeException("Token expired");
         }

         user.setFailedLoginAttempts(0);
         user.setLockedUntil(null);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(resetToken);

        logAuditEvent(user.getEmail(),"PASSWORD_RESET_SUCCESS", "Password reset successfully");

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

    public TokenResponse refreshToken(String refreshToken) {
        Token token = tokenRepository.findByTokenAndType(refreshToken ,TokenType.REFRESH).orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if(token.isRevoked() || !jwtService.validateToken(refreshToken)){
            logAuditEvent(token.getEmail(),"REFRESH_FAILED","Invalid or Revoke refresh token");
            throw new InvalidTokenException("Invalid or Revoke refresh token");
        }

        User user = userRepository.findByEmail(token.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        token.setRevoked(true);
        tokenRepository.save(token);

        String newRefreshToken = jwtService.generateRefreshToken(user);
        Token newToken=new Token();
        newToken.setToken(newRefreshToken);
        newToken.setEmail(user.getEmail());
        newToken.setType(TokenType.REFRESH);
        tokenRepository.save(newToken);

        logAuditEvent(user.getEmail(), "REFRESH_SUCCESS","Token Refresh Successfully");

        return new TokenResponse(jwtService.generateAccessToken(user),newRefreshToken);

    }

    public User getUserFromToken(String token){
        String email = jwtService.getUsernameFromToken(token);
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));

    }

    public void twoStepVerificationEnable(String token) {
        User user = getUserFromToken(token);

        if(user.isTwoStepVerificationEnabled()) throw new InvalidTokenException("2-step verification already enabled");

        user.setTwoStepVerificationEnabled(true);
        userRepository.save(user);

        logAuditEvent(user.getEmail(),"2_STEP_VERIFICATION_ENABLED","2-step verification enabled successfully");
    }

    public void sendOtp(String email, String password) throws AccountLockedException {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email,password));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found"));

        if(user.getLockedUntil()!=null && user.getLockedUntil() > Instant.now().toEpochMilli()){
            throw new AccountLockedException("Account is locked until "+  Instant.ofEpochMilli(user.getLockedUntil()));
        }

        if(!user.isEmailVarified()){
            throw new EmailNotVerifiedException(user.getEmail() + " not verified");
        }

        if(!user.isTwoStepVerificationEnabled()){
            throw new UserMfaNotEnabledException(user.getEmail()+ " not enabled two step verification");
        }



        String key=OTP_PREFIX + user.getEmail();



    }
    public TokenResponse verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " not found"));

        String storedOtp = redisTemplate.opsForValue().get("otp:" + email);
        if(storedOtp== null || !storedOtp.equals(otp)){
            logAuditEvent(user.getEmail(),"OTP_VERIFICATION_FAILED","Invalid OTP");
            throw new InvalidOtpException("Invalid OTP");
        }

        redisTemplate.delete("otp:"+email);

        List<Token> existingTokens = tokenRepository.findByEmailAndType(user.getEmail(), TokenType.REFRESH);
        existingTokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(existingTokens);

        String refreshToken = jwtService.generateRefreshToken(user);
        Token token = new Token();
        token.setToken(refreshToken);
        token.setEmail(user.getEmail());
        token.setType(TokenType.REFRESH);
        tokenRepository.save(token);

        logAuditEvent(user.getEmail(), "OTP_VERIFY_SUCCESS", "OTP verified successfully");
        return new TokenResponse(
                jwtService.generateAccessToken(user),
                refreshToken
        );

    }

    public void disableTwoStep(String token) {

        User user = getUserFromToken(token);
        if(!user.isTwoStepVerificationEnabled()){
            throw new InvalidTokenException("2-step verification already disable");
        }

        user.setTwoStepVerificationEnabled(false);
        userRepository.save(user);
        logAuditEvent(user.getEmail(),"2_STEP_VERIFICATION_DISABLED","2 step verification disable successfully");
    }

    public TokenResponse handleOAuth2Login(String email,  Map<String, Object> attributes) {

        User user = userRepository.findByEmail(email).orElseGet(() ->
        {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setRole(UserRole.USER);
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

    public void logout(String accessToken) {
        User user = getUserFromToken(accessToken);

        if(!jwtService.validateToken(accessToken)){
            logAuditEvent(user.getEmail(),"LOGOUT_FAILED","logout failed.");
        }

        long expiry=parseTokenExpiration(accessToken);
        redisTemplate.opsForValue().set("blacklist:access:"+accessToken, "revoked", expiry, TimeUnit.MILLISECONDS);


        List<Token> tokenList = tokenRepository.findByEmailAndType(user.getEmail(), TokenType.REFRESH);
        tokenList.forEach(t-> {t.setRevoked(true);

            redisTemplate.opsForValue().set("blacklist:refresh:"+t.getToken(),"revoked", parseTokenExpiration(t.getToken()),TimeUnit.MILLISECONDS);

        });
        tokenRepository.saveAll(tokenList);

        logAuditEvent(user.getEmail(),"LOGOUT_SUCCESS","UserLogged Out successfully");


    }

    public long parseTokenExpiration(String  token){

         return Jwts.parser()
                 .verifyWith(jwtService.getKeyPair().getPublic())
                 .build()
                 .parseSignedClaims(token)
                 .getPayload()
                 .getExpiration()
                 .getTime()-System.currentTimeMillis();

    }
}
