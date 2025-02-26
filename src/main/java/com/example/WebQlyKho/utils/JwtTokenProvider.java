package com.example.WebQlyKho.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String signalKey = "V+V39BUErFvGF9X4+zG3nomg7BYf9JmaqlGsR2j9icqMtcCQXzp9Q/ETibGiJ3ON";
    private final long jwtExpiration = 1000000;

    // Generate token
    public String generateTokenByUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(new Date().getTime() + jwtExpiration))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS512, signalKey)
                .compact();
    }

    // Get username from token
    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(signalKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Validate token
    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(signalKey).parseClaimsJws(token);
            return true;
        } catch (Exception e){
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
}
