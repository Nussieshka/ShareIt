package com.nussia.user.memory;

import com.nussia.Util;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.user.UserDTO;
import com.nussia.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("InMemoryService")
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {

    private final InMemoryUserRepository userRepository;

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO == null) {
            throw new BadRequestException("Invalid parameter: userDTO is null");
        } else if (userDTO.getId() != null) {
            throw new BadRequestException("Cannot add user with userId");
        }

        Util.validateUserDTO(userDTO);

        return userRepository.addUser(userDTO).orElseThrow(() -> new ObjectNotFoundException("User", userDTO.getId()));
    }

    @Override
    public UserDTO editUser(UserDTO userDTO, Long userId) {
        if (userId == null || userDTO == null) {
            throw new BadRequestException("Invalid parameters: userId or userDTO is null");
        }
        return userRepository.editUser(userDTO, userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));
    }

    @Override
    public UserDTO getUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        return userRepository.getUser(userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));
    }

    @Override
    public UserDTO deleteUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        return userRepository.deleteUser(userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));
    }

    @Override
    public List<UserDTO> getUsers() {
        return userRepository.getUsers();
    }

    @Override
    public Boolean isUserExists(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        return userRepository.getUser(userId).isPresent();
    }
}
