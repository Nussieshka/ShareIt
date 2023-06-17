package com.nussia.shareit.user;

import com.nussia.shareit.Util;
import com.nussia.shareit.exception.BadRequestException;
import com.nussia.shareit.exception.ConflictException;
import com.nussia.shareit.exception.ObjectNotFoundException;
import com.nussia.shareit.user.dto.UserDTO;
import com.nussia.shareit.user.dto.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("JpaUserService")
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Transactional
    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO == null) {
            throw new BadRequestException("Invalid parameter: userDTO is null");
        } else if (userDTO.getId() != null) {
            throw new BadRequestException("Cannot add user with userId");
        }

//        Util.validateUserDTO(userDTO);

        try {
            return UserMapper.INSTANCE.toUserDTO(repository.save(UserMapper.INSTANCE.toUserEntity(userDTO)));
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

        User user = this.repository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));

        Util.updateUserEntityFromDTO(user, userDTO);

        return UserMapper.INSTANCE.toUserDTO(repository.save(user));
    }

    @Transactional
    @Override
    public UserDTO getUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }

        return repository.findById(userId).map(UserMapper.INSTANCE::toUserDTO)
                .orElseThrow(() -> new ObjectNotFoundException("User", userId));
    }

    @Transactional
    @Override
    public UserDTO deleteUser(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        User user = repository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User", userId));
        repository.deleteById(userId);
        return UserMapper.INSTANCE.toUserDTO(user);
    }

    @Override
    public List<UserDTO> getUsers() {
        return UserMapper.INSTANCE.toUserDTO(repository.findAll());
    }

    @Override
    public Boolean doesUserExist(Long userId) {
        if (userId == null) {
            throw new BadRequestException("Invalid parameter: userId is null");
        }
        return repository.existsById(userId);
    }
}
