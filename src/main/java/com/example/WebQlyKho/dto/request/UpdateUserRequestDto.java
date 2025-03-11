package com.example.WebQlyKho.dto.request;

import com.example.WebQlyKho.utils.StringRegex;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserRequestDto {
    private Integer id;
    private String fullname;
    private String password;
    @Pattern(regexp = StringRegex.EMAIL_PATTERN, message = "Email không đúng định dạng")
    private String email;
    private String phone;
    @Size(max = 12, min = 12, message = "CCCD phải có 12 kí tự")
    private String cccd;
    private String bankNumber;
}
