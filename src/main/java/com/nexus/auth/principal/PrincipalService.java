package com.nexus.auth.principal;

import com.nexus.user.User;
import com.nexus.user.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class PrincipalService implements UserDetailsService {
    private final UserService userService;

    public PrincipalService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userService.findByUsername(username);

        return new Principal(user);
    }
}
