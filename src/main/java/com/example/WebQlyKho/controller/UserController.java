package com.example.WebQlyKho.controller;

import com.example.WebQlyKho.dto.request.LoginRequestDto;
import com.example.WebQlyKho.dto.request.RegisterRequestDto;
import com.example.WebQlyKho.dto.request.UpdateUserRequestDto;
import com.example.WebQlyKho.dto.response.APIResponse;
import com.example.WebQlyKho.dto.response.LoginResponseDto;
import com.example.WebQlyKho.service.UserService;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/getUsernameFromToken")
    public ResponseEntity<Object> getUsernameFromToken(HttpServletRequest request) {
        String username = jwtTokenProvider.getUsernameFromToken(request.getHeader("Authorization").substring(7));
        return APIResponse.responseBuilder(
                username,
                "Get username from token successfully",
                HttpStatus.OK);
    }

    @GetMapping("/getAllUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getAllUser() {
        return APIResponse.responseBuilder(
                userService.getAllUser(),
                "Get all user successfully",
                HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginRequestDto request) {
        LoginResponseDto responseDto = userService.Login(request);
        return APIResponse.responseBuilder(
                responseDto,
                "Login successfully",
                HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterRequestDto request) {
        userService.register(request);
        return APIResponse.responseBuilder(
                null,
                "Register successfully",
                HttpStatus.OK);
    }

    @PostMapping("/deleteUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return APIResponse.responseBuilder(
                null,
                "Delete user successfully",
                HttpStatus.OK);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UpdateUserRequestDto requestDto){
        userService.updateUser(requestDto);
        return APIResponse.responseBuilder(
                null,
                "Update user Succesfully",
                HttpStatus.OK
        );
    }

    @GetMapping("/getUserInfo")
    public ResponseEntity<Object> getUserInfo(HttpServletRequest request){
        return APIResponse.responseBuilder(
                userService.getUserInfo(request),
                "Get user info successfully",
                HttpStatus.OK
        );
    }
}
