package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.request.LoginRequestDto;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequestDto request) {
        String token = userService.Login(request);
        return APIResponse.responseBuilder(
                token,
                "Login successfully",
                HttpStatus.OK);
    }

//    @PostMapping("/register")
//    public ResponseEntity<Object> register(@RequestBody LoginRequestDto request) {
//        String token = userService.Register(request);
//        return APIResponse.responseBuilder(
//                token,
//                "Register successfully",
//                HttpStatus.OK);
//    }
}
