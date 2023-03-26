package com.nussia.user.jpa;

import com.nussia.Util;
import com.nussia.exception.BadRequestException;
import com.nussia.exception.ConflictException;
import com.nussia.exception.ObjectNotFoundException;
import com.nussia.user.User;
import com.nussia.user.UserDTO;
import com.nussia.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service("JpaUserService")
@RequiredArgsConstructor
@Slf4j
public class JpaUserService implements UserService {

    private final JpaUserRepository repository;

    @Transactional
    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO == null) {
            throw new BadRequestException("Invalid parameter: userDTO is null");
        } else if (userDTO.getId() != null) {
            throw new BadRequestException("Cannot add user with userId");
        }

        Util.validateUserDTO(userDTO);

        try {
            return repository.save(Util.getUserFromUserDTO(userDTO)).toUserDTO();
        } catch (DataIntegrityViolationException e) {
            log.info(e.getMessage());
            throw new ConflictException("User with same email address exists");
        }
    }

    @Transactional
    @Override
    public UserDTO editUser(UserDTO userDTO, Long userId) {
        if (userId == null || userDTO == null) {
            throw new BadRequestException("Invalid parameters: userId, or userDTO is null");
        }

        String email = userDTO.getEmail();
        User user = this.repository.getById(userId);

        String name = userDTO.getName();
        if (name != null) {
            user.setName(name);
        }

        if (email != null) {
            user.setEmail(email);
        }

        return repository.save(user).toUserDTO();
    }

    @Transactional
    @Override
    public UserDTO getUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        try {
            return repository.getById(userId).toUserDTO();
        } catch (EntityNotFoundException e) {
            throw new ObjectNotFoundException("User", userId);
        }
    }

    @Transactional
    @Override
    public UserDTO deleteUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        User user = repository.getById(userId);
        try {
            repository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("User", userId);
        }
        return user.toUserDTO();
    }

    @Override
    public List<UserDTO> getUsers() {
        return Util.getUserDTOFromUser(repository.findAll());
    }

    @Override
    public Boolean isUserExists(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        return repository.existsById(userId);
    }
}
