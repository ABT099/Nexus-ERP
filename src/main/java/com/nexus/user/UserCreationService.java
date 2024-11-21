package com.nexus.user;

import com.nexus.chat.Chat;
import com.nexus.chat.ChatRepository;
import com.nexus.exception.DuplicateResourceException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserCreationService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();


    public UserCreationService(UserRepository userRepository, ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    public User create(String username, String password, UserType userType) {
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(password);

        User user = new User(username, hashedPassword, userType);

        userRepository.save(user);
        chatRepository.save(new Chat(username));

        return user;
    }
}
