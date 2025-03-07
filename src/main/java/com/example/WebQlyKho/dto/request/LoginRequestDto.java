package com.example.WebQlyKho.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    @NotBlank
    public String username;
    @NotBlank
    public String password;
}
