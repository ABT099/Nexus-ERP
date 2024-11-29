package com.nexus.user;

import com.nexus.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("user with id " + id + " not found")
                );
    }

    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("username " + username + " not found")
                );
    }

    public List<User> findAllById(Iterable<Long> ids) {
        return userRepository.findAllById(ids);
    }

    public String findUserIdByUsername(String username) {
        return userRepository.findUserIdByUsername(username);
    }
}
