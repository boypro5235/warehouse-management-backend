package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.request.ChangePasswordRequestDto;
import com.example.WebQlyKho.dto.request.LoginRequestDto;
import com.example.WebQlyKho.dto.request.RegisterRequestDto;
import com.example.WebQlyKho.dto.request.UpdateUserRequestDto;
import com.example.WebQlyKho.dto.response.LoginResponseDto;
import com.example.WebQlyKho.entity.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {
    LoginResponseDto Login(LoginRequestDto request);
    int register(RegisterRequestDto request);
    void logout();
    List<User> getAllUser();
    void deleteUser(int id);
    void updateUser(UpdateUserRequestDto request);
    void changePassword(ChangePasswordRequestDto request);
    User getUserInfo(HttpServletRequest request);
}
