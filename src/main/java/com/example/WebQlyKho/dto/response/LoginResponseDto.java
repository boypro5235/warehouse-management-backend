package com.example.WebQlyKho.dto.response;

import com.example.WebQlyKho.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponseDto {
    private String token;
    private String fullname;
    private int userId;
    private String role;
}
