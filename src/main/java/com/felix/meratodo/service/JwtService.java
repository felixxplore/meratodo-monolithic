package com.felix.meratodo.service;

import com.felix.meratodo.model.User;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final Dotenv dotenv=Dotenv.load();

    private final String accessTokenExpiry=dotenv.get("APP_JWT_ACCESS_TOKEN_EXPIRY");

    private final String refreshTokenExpiry=dotenv.get("APP_JWT_REFRESH_TOKEN_EXPIRY");


    private PrivateKey privateKey; // to store the private key

    @Getter
    private PublicKey publicKey; // to store the public key

    @Getter
    private final String keyId=UUID.randomUUID().toString(); // unique id for key pair (for rotation)

    @PostConstruct
    public void init(){
        try {
            // Load from classpath
            String privateKeyPem = new String(new ClassPathResource("keys/private_key.pem").getInputStream().readAllBytes())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            String publicKeyPem = new String(new ClassPathResource("keys/public_key.pem").getInputStream().readAllBytes())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyPem)));
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyPem)));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load keys", ex);
        }
    }

    public String generateAccessToken(User user){
        long expiry = parseDuration(accessTokenExpiry);
        return Jwts.builder()
                .header().add("kid", keyId).and()
                .subject(user.getEmail())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String generateRefreshToken(User user){
        long expiry=parseDuration(refreshTokenExpiry);
        return Jwts.builder()
                .header().add("kid",keyId).and()
                .subject(user.getEmail())
                .claim("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ expiry))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    public String getUsernameFromToken(String token){

        return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token).getPayload().getSubject();

    }

    public boolean validateToken(String token){

      try {Jwts.parser()
                .verifyWith(publicKey)
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
