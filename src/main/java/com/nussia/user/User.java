package com.nussia.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private Long userId;
    private String name;
    private String email;

    public UserDTO toUserDTO() {
        return new UserDTO(userId, name, email);
    }
}
