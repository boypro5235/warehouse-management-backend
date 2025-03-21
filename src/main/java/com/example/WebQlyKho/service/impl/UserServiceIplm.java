package com.example.WebQlyKho.service.impl;

import com.example.WebQlyKho.dto.request.ChangePasswordRequestDto;
import com.example.WebQlyKho.dto.request.LoginRequestDto;
import com.example.WebQlyKho.dto.request.RegisterRequestDto;
import com.example.WebQlyKho.dto.request.UpdateUserRequestDto;
import com.example.WebQlyKho.dto.response.LoginResponseDto;
import com.example.WebQlyKho.entity.User;
import com.example.WebQlyKho.exception.CustomException;
import com.example.WebQlyKho.exception.ERROR_CODE;
import com.example.WebQlyKho.repository.UserRepository;
import com.example.WebQlyKho.service.UserService;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import com.example.WebQlyKho.utils.Mapper.detailsMapper.UserRegisterRequestMapper;
import com.example.WebQlyKho.utils.Mapper.detailsMapper.UserUpdateMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Autowired
    private UserUpdateMapper userUpdateMapper;

    @Override
    public LoginResponseDto Login(LoginRequestDto request) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()));
        } catch (Exception e){
            throw new RuntimeException(e.getLocalizedMessage());
        }
        User user = userRepository.findByUsername(request.getUsername());
        if(!user.isStatus()){
            throw new CustomException(ERROR_CODE.INACTIVE_ACCOUNT);
        }
        String token = jwtTokenProvider.generateTokenByUsername(request.getUsername(), user.getUserId());;
        return LoginResponseDto.builder()
                .token(token)
                .role(user.getRole())
                .userId(user.getUserId())
                .build();
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
        user.setRole("USER");
        user.setStatus(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        return 1;
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public List<User> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream().filter(User::isStatus)
                .filter(user -> !user.getUsername().equals("admin"))
                .toList();
    }

    @Override
    public void deleteUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(false);
        userRepository.save(user);
    }

    @Override
    public void updateUser(UpdateUserRequestDto request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        userUpdateMapper.updateUserFromDto(request, user);
       user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequestDto request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())){
            throw new CustomException(ERROR_CODE.WRONG_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public User getUserInfo(HttpServletRequest request) {
        try{
            jwtTokenProvider.validateToken(request);
            String token = request.getHeader("Authorization").substring(7);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
}
