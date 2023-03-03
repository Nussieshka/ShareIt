package com.nussia.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService USER_SERVICE;

    @PostMapping()
    public ResponseEntity<UserDTO> postUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(USER_SERVICE.createUser(userDTO));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDTO> patchUser(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        return ResponseEntity.ok(USER_SERVICE.editUser(userDTO, userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(USER_SERVICE.getUser(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(USER_SERVICE.deleteUser(userId));
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        return ResponseEntity.ok(USER_SERVICE.getUsers());
    }
}
