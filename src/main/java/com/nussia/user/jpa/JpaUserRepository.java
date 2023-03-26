package com.nussia.user.jpa;

import com.nussia.user.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    boolean existsById(@NonNull Long id);
}
