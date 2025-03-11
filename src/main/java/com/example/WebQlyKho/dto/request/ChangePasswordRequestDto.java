package com.example.WebQlyKho.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequestDto {
    private Integer id;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
