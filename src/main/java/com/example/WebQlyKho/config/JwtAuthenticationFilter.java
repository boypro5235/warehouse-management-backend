package com.example.WebQlyKho.config;

import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.entity.User;
import com.example.WebQlyKho.exception.CustomException;
import com.example.WebQlyKho.exception.ERROR_CODE;
import com.example.WebQlyKho.repository.UserRepository;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader("Authorization");

        if(jwt == null || !jwt.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        String token = jwt.substring(7);

        try {
            jwtTokenProvider.validateToken(request);
            User user = userRepository.findByUsername(jwtTokenProvider.getUsernameFromToken(token));
            if(user.getUsername() != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(user.getUsername());
                if(jwtTokenProvider.validateToken(token)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }}
        } catch (CustomException exception){
            sendJsonResponse(response, APIResponse.responseBuilder(null, exception.getErrorCode().getMessage(), exception.getErrorCode().getCode()));
        } catch (Exception e) {
            sendJsonResponse(response, APIResponse.responseBuilder(null, "Invalid token", HttpStatus.BAD_REQUEST));
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendJsonResponse(HttpServletResponse response, ResponseEntity<Object> apiResponse) throws IOException {
        response.setContentType("application/json");
        response.setStatus(apiResponse.getStatusCodeValue());
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse.getBody()));
        response.getWriter().flush();
    }

}
