package com.felix.meratodo.controller;


import com.felix.meratodo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class JwksController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getJwks() {
        RSAPublicKey publicKey = (RSAPublicKey) jwtService.getPublicKey();
        String modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getModulus().toByteArray());
        String exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getPublicExponent().toByteArray());

        Map<String, Object> key = new HashMap<>();
        key.put("kty", "RSA");
        key.put("n", modulus);
        key.put("e", exponent);
        key.put("alg", "RS256");
        key.put("use", "sig");
        key.put("kid", jwtService.getKeyId());

        return Collections.singletonMap("keys", Collections.singletonList(key));
    }
}
