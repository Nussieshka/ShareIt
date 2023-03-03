package com.nussia.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<UserDTO> getUsers();
    Optional<UserDTO> getUser(Long userId);
    Optional<UserDTO> addUser(UserDTO userDTO);
    Optional<UserDTO> editUser(UserDTO userDTO, Long userId);
    Optional<UserDTO> deleteUser(Long userId);
}
