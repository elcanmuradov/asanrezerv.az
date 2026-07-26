package com.rezervet.authservice.mapper;

import com.rezervet.authservice.dto.UserDto;
import com.rezervet.authservice.entity.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")

public interface UserMapper {
    UserDto toDto(User user);

}
