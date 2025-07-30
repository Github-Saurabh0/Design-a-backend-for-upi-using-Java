package com.upi.repository;

import com.upi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);
}