package com.nussia.user;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<UserDTO> createUser(UserDTO userDTO);

    Optional<UserDTO> editUser(UserDTO userDTO, Long userId);

    Optional<UserDTO> getUser(Long userId);

    Optional<UserDTO> deleteUser(Long userId);

    List<UserDTO> getUsers();
}
