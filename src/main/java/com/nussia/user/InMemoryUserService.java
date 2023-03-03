package com.nussia.user;

import com.nussia.exception.BadRequestException;
import com.nussia.exception.ObjectNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
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

        return userRepository.addUser(userDTO).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public UserDTO editUser(UserDTO userDTO, Long userId) {
        if (userId == null || userDTO == null) {
            throw new BadRequestException();
        }
        return userRepository.editUser(userDTO, userId).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public UserDTO getUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException();
        }
        return userRepository.getUser(userId).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public UserDTO deleteUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException();
        }
        return userRepository.deleteUser(userId).orElseThrow(ObjectNotFoundException::new);
    }

    @Override
    public List<UserDTO> getUsers() {
        return userRepository.getUsers();
    }
}
