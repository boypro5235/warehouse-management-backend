package com.example.WebQlyKho.utils.Mapper.detailsMapper;

import com.example.WebQlyKho.dto.request.UpdateUserRequestDto;
import com.example.WebQlyKho.entity.User;
import com.example.WebQlyKho.utils.Mapper.CustomMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserUpdateMapper extends CustomMapper<User, UpdateUserRequestDto> {
    void updateUserFromDto(UpdateUserRequestDto dto, @MappingTarget User user);
}
