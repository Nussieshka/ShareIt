package com.nussia.user;

import com.nussia.user.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO createUser(UserDTO userDTO);

    UserDTO editUser(UserDTO userDTO, Long userId);

    UserDTO getUser(Long userId);

    UserDTO deleteUser(Long userId);

    List<UserDTO> getUsers();

    Boolean isUserExists(Long userId);
}
