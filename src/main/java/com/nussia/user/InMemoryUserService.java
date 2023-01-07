package com.nussia.user;

import com.nussia.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class InMemoryUserService implements UserService {

    private final UserRepository USER_REPOSITORY;

    @Override
    public Optional<UserDTO> createUser(UserDTO userDTO) {
        if (userDTO == null) {
            throw new BadRequestException();
        } else if (userDTO.getId() != null) {
            throw new BadRequestException("Cannot add user with userId");
        }

        String name = userDTO.getName();
        String email = userDTO.getEmail();
        if (name == null || email == null) {
            throw new BadRequestException("User should have these non-null fields: name, email");
        } else if (name.isBlank()) {
            throw new BadRequestException("Cannot add user with blank name");
        } else if (email.isBlank() || !email.matches("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b")) {
            throw new BadRequestException("Cannot add user with invalid email address");
        }

        return USER_REPOSITORY.addUser(userDTO);
    }

    @Override
    public Optional<UserDTO> editUser(UserDTO userDTO, Long userId) {
        if (userId == null || userDTO == null) {
            throw new BadRequestException();
        }
        return USER_REPOSITORY.editUser(userDTO, userId);
    }

    @Override
    public Optional<UserDTO> getUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException();
        }
        return USER_REPOSITORY.getUser(userId);
    }

    @Override
    public Optional<UserDTO> deleteUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException();
        }
        return USER_REPOSITORY.deleteUser(userId);
    }

    @Override
    public List<UserDTO> getUsers() {
        return USER_REPOSITORY.getUsers();
    }
}
