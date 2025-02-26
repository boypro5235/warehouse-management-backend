package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.LoginRequestDto;
import com.example.WebQlyKho.dto.request.RegisterRequestDto;
import com.example.WebQlyKho.service.UserService;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class UserServiceIplm implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String Login(LoginRequestDto request) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()));
        } catch (Exception e){
            throw new RuntimeException("Incorrect username or password");
        }

        String token = jwtTokenProvider.generateTokenByUsername(request.getUsername());

        return token;
    }

    @Override
    public void register(RegisterRequestDto request) {

    }
}
