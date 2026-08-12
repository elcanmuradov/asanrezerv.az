package com.asanrezerv.authservice.mapper;

import com.asanrezerv.authservice.dto.UserDto;
import com.asanrezerv.authservice.entity.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")

public interface UserMapper {
    UserDto toDto(User user);

}
