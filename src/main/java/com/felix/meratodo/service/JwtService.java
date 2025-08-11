package com.felix.meratodo.service;

import com.felix.meratodo.model.User;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Dotenv dotenv=Dotenv.load();

    private final String accessTokenExpiry=dotenv.get("APP_JWT_ACCESS_TOKEN_EXPIRY");

    private final String refreshTokenExpiry=dotenv.get("APP_JWT_REFRESH_TOKEN_EXPIRY");

    @Getter
    private KeyPair keyPair;

    @PostConstruct
    public void init(){
        try{
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048); // 2048 bit key
            this.keyPair=keyPairGen.generateKeyPair();
        }catch (NoSuchAlgorithmException ex){
            throw new RuntimeException(ex);
        }
    }

    public String generateAccessToken(User user){
        long expiry = parseDuration(accessTokenExpiry);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(User user){
        long expiry=parseDuration(refreshTokenExpiry);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ expiry))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token){

        return Jwts.parser().verifyWith(keyPair.getPublic()).build().parseSignedClaims(token).getPayload().getSubject();

    }

    public boolean validateToken(String token){

      try {Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build()
                .parseSignedClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    private long parseDuration(String duration){
        if(duration.endsWith("m")){
            return Long.parseLong(duration.replace("m",""))*60*1000;
        }else if(duration.endsWith("d")){
            return Long.parseLong(duration.replace("d","")) * 24 *60 * 60 * 1000;
        }
        throw new IllegalArgumentException("Invalid Duration format");
    }


//    public String generateToken(String username) {
//
//        Map<String, Object> claims=new HashMap<>();
//
//        // 1 day in milliseconds
//        int expiration = 86400000;
//        return Jwts.builder()
//                .claims()
//                .add(claims)
//                .subject(username)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + expiration))
//                .and()
//                .signWith(getKey())
//                .compact();
//    }
//
//    private SecretKey getKey(){
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }


//    public String extractUserName(String token) {
//    return  extractClaim(token, Claims::getSubject);
//
//    }
//
//    private <T> T extractClaim(String token, Function<Claims,T> claimResolver) {
//       final Claims claims =extractAllClaims(token);
//        return claimResolver.apply(claims);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//
//    public boolean validateToken(String token, UserDetails userDetails) {
//        String userName = extractUserName(token);
//        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token,Claims::getExpiration);
//    }


}
