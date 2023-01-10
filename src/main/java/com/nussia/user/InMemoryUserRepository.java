package com.nussia.user;

import com.nussia.exception.ConflictException;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private static final AtomicLong idCounter = new AtomicLong(1);

    private static final Map<Long, User> users = new HashMap<>();

    @Override
    public List<UserDTO> getUsers() {
        return users.values().stream().map(User::toUserDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUser(Long userId) {
        return Optional.ofNullable(users.get(userId)).map(User::toUserDTO);
    }

    @Override
    public Optional<UserDTO> addUser(UserDTO userDTO) {
        if (isUserWithThisEmailExists(userDTO.getEmail())) {
            throw new ConflictException("User with this email is already exists");
        }
        User user = getUserFromUserDTO(userDTO);
        return Optional.ofNullable(users.computeIfAbsent(user.getUserId(), x -> user).toUserDTO());
    }

    @Override
    public Optional<UserDTO> editUser(UserDTO userDTO, Long userId) {
        if (isUserWithThisEmailExists(userDTO.getEmail())) {
            throw new ConflictException("User with this email is already exists");
        }
        return Optional.ofNullable(users.computeIfPresent(userId, (id, currentUser) -> {

            String name = userDTO.getName();
            if (name != null) {
                currentUser.setName(name);
            }

            String email = userDTO.getEmail();
            if (email != null) {
                currentUser.setEmail(email);
            }

            return currentUser;

        })).flatMap(x -> Optional.ofNullable(x.toUserDTO()));
    }

    @Override
    public Optional<UserDTO> deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.remove(userId)).map(User::toUserDTO);
    }

    private User getUserFromUserDTO(UserDTO userDTO) {
        return getUserFromUserDTO(idCounter.getAndIncrement(), userDTO);
    }

    private User getUserFromUserDTO(Long id, UserDTO userDTO) {
        return new User(id, userDTO.getName(), userDTO.getEmail());
    }

    private boolean isUserWithThisEmailExists(String email) {
        return users.values().stream().anyMatch(x -> x.getEmail().equals(email));
    }
}
