package com.example.WebQlyKho.service;

import com.example.WebQlyKho.dto.request.LoginRequestDto;
import com.example.WebQlyKho.dto.request.RegisterRequestDto;

public interface UserService {
    String Login(LoginRequestDto request);
    void register(RegisterRequestDto request);
}
