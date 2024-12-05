package com.nexus.user;

import com.nexus.common.abstraction.AbstractUserService;
import com.nexus.exception.NoUpdateException;
import com.nexus.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends AbstractUserService {
    private final UserRepository userRepository;
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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

    public void changeUsername(String newUsername) {
        User user = findById(getUserId());

        if (user.getUsername().equals(newUsername)) {
            throw new NoUpdateException("username is the same");
        }

        user.setUsername(newUsername);
        userRepository.save(user);
    }

    public void changePassword(String newPassword) {
        User user = findById(getUserId());

        if (encoder.matches(newPassword, user.getPassword())) {
            throw new NoUpdateException("password is the same");
        }

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }
}
