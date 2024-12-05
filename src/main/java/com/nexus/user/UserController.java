package com.nexus.user;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("username")
    public void changeUsername(@RequestBody String username) {
        userService.changeUsername(username);
    }

    @PatchMapping("password")
    public void changePassword(@RequestBody String password) {
        userService.changePassword(password);
    }
}
