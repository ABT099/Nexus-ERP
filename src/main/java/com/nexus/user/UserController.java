package com.nexus.user;

import com.nexus.abstraction.UserContext;
import com.nexus.exception.NoUpdateException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController extends UserContext {

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("username")
    public void changeUsername(@RequestBody String username) {
        User user = userService.findById(getUserId());

        if (user.getUsername().equals(username)) {
            throw new NoUpdateException("username is the same");
        }

        user.setUsername(username);
        userService.update(user);
    }

    @PatchMapping("password")
    public void changePassword(@RequestBody String password) {
        User user = userService.findById(getUserId());

        if (encoder.matches(password, user.getPassword())) {
            throw new NoUpdateException("password is the same");
        }

        user.setPassword(encoder.encode(password));
        userService.update(user);
    }
}
