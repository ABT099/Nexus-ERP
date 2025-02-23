package com.nexus.user;

import com.nexus.abstraction.UserContext;
import com.nexus.exception.NoUpdateException;
import com.nexus.s3.S3Service;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("users")
public class UserController extends UserContext {

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserService userService;
    private final S3Service s3Service;

    public UserController(UserService userService, S3Service s3Service) {
        this.userService = userService;
        this.s3Service = s3Service;
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

    @PatchMapping("{id}/avatar-image/add")
    public void changeAvatarImage(@RequestParam MultipartFile avatarImage) {
        try {
            String contentType = avatarImage.getContentType();

            if (
                avatarImage.isEmpty() ||
                avatarImage == null ||
                contentType == null ||
                !contentType.startsWith("image/")) {
                throw new NoUpdateException("Invalid image file");
            }
    
            User user = userService.findById(getUserId());
    
            byte[] fileContent = avatarImage.getBytes();
            var bucketName = UUID.randomUUID().toString();
    
            String imageUrl =  s3Service.uploadFile(bucketName, "avatar-images", fileContent);
    
            user.setAvatarUrl(imageUrl);
            userService.update(user);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading avatar image", e);
        }
    }

    @PatchMapping("{id}/avatar-image/delete")
    public void deleteAvatarImage() {
        User user = userService.findById(getUserId());

        if (user.getAvatarUrl() == null) {
            throw new NoUpdateException("avatar image is empty");
        }

        try {
            s3Service.deleteFile(user.getAvatarUrl());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting avatar image", e);
        }

        user.setAvatarUrl(null);
        userService.update(user);
    }
}
