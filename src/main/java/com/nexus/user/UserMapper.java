package com.nexus.user;

import com.nexus.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserMapper {

    public UserInfoDTO toUserInfo(Optional<User> source) {
        if (source.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = source.get();

        return new UserInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getAvatarUrl()
        );
    }

    public UserInfoDTO toUserInfo(User source) {
        return new UserInfoDTO(
                source.getId(),
                source.getUsername(),
                source.getAvatarUrl()
        );
    }
}
