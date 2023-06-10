package com.nussia.user.dto;

import com.nussia.user.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDTO(User user);

    User toUserEntity(UserDTO userDTO);

    @IterableMapping(elementTargetType = UserDTO.class)
    List<UserDTO> toUserDTO(Iterable<User> users);
}
