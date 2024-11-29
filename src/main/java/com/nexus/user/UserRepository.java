package com.nexus.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("""
        select u.id
        from User u
        where u.username = :username
    """)
    String findUserIdByUsername(String username);
}
