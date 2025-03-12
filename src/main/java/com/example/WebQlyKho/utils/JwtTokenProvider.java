package com.example.WebQlyKho.utils;

import com.example.WebQlyKho.exception.CustomException;
import com.example.WebQlyKho.exception.ERROR_CODE;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final String signalKey = "V+V39BUErFvGF9X4+zG3nomg7BYf9JmaqlGsR2j9icqMtcCQXzp9Q/ETibGiJ3ON";
    private final long jwtExpiration = 1000000000;

    // Generate token
    public String generateTokenByUsername(String username, int userId){
        Map<String, Object> claims = new HashMap<>();
        claims.put("user-id", userId);
        return Jwts.builder()
                .setClaims(claims)
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
    // Get username from token
    public Integer getUserIdFromToken(String token) {
        return (int)Jwts.parser().setSigningKey(signalKey)
                .parseClaimsJws(token)
                .getBody()
                .get("user-id");
    }
    // Get username from token
    public Integer getUserIdFromToken(HttpServletRequest request) {
        if(request.getHeader("Authorization") == null || !request.getHeader("Authorization").startsWith("Bearer ")){
            throw new RuntimeException("Token Invalid");
        }
        String token = request.getHeader("Authorization").substring(7);
        return (int)Jwts.parser().setSigningKey(signalKey)
                .parseClaimsJws(token)
                .getBody()
                .get("user-id");
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

    // Validate token
    public boolean validateToken(HttpServletRequest request) {
        try{
            if(request.getHeader("Authorization") == null || !request.getHeader("Authorization").startsWith("Bearer ")){
                throw new RuntimeException("Token Invalid");
            }
            String token = request.getHeader("Authorization").substring(7);
            Jwts.parser().setSigningKey(signalKey).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(ERROR_CODE.EXPIRED_TOKEN);
        } catch (SignatureException e) {
            throw new CustomException(ERROR_CODE.INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new CustomException(ERROR_CODE.INVALID_TOKEN);
        } catch (Exception e){
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
}
