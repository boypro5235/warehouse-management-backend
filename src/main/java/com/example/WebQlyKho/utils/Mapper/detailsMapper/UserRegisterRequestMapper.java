package com.example.WebQlyKho.utils.Mapper.detailsMapper;

import com.example.WebQlyKho.dto.request.RegisterRequestDto;
import com.example.WebQlyKho.entity.User;
import com.example.WebQlyKho.utils.Mapper.CustomMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegisterRequestMapper extends CustomMapper<User, RegisterRequestDto> {
}
