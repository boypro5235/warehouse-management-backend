package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.LoginRequestDto;
import com.example.WebQlyKho.dto.request.RegisterRequestDto;
import com.example.WebQlyKho.entity.User;
import com.example.WebQlyKho.exception.CustomException;
import com.example.WebQlyKho.exception.ERROR_CODE;
import com.example.WebQlyKho.repository.UserRepository;
import com.example.WebQlyKho.service.UserService;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import com.example.WebQlyKho.utils.Mapper.detailsMapper.UserRegisterRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class UserServiceIplm implements UserService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRegisterRequestMapper userRegisterRequestMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

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
    public int register(RegisterRequestDto request) {
        if(request.getUsername() != null && userRepository.existsByUsername(request.getUsername())){
            throw new CustomException(ERROR_CODE.USERNAME_EXISTED);
        }
        if(request.getEmail() != null && userRepository.existsByEmail(request.getEmail())){
            throw new CustomException(ERROR_CODE.EMAIL_EXISTED);
        }
        if(request.getCccd() != null && userRepository.existsByCccd(request.getCccd())){
            throw new CustomException(ERROR_CODE.CCCD_EXISTED);
        }

        User user = userRegisterRequestMapper.toEntity(request);
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return 1;
    }
}
