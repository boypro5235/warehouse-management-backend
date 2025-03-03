package com.example.WebQlyKho.dto.request;

import com.example.WebQlyKho.utils.StringRegex;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterRequestDto {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;
    private String fullname;
    @Pattern(regexp = StringRegex.EMAIL_PATTERN, message = "Email is invalid")
    private String email;
    @Size(min = 12, max = 12, message = "CCCD is invalid")
    private String cccd;
    private String bankNumber;
}
