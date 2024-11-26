package com.nexus.user;

import com.nexus.chat.Chat;
import com.nexus.notification.Notification;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "_user")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    @Column(
            unique = true,
            nullable = false,
            columnDefinition = "text"
    )
    private String username;
    @Column(
            nullable = false,
            columnDefinition = "text"
    )
    private String password;
    @Column(
            nullable = false,
            updatable = false,
            columnDefinition = "text"
    )
    @Enumerated(EnumType.STRING)
    private UserType userType;
    @Column(columnDefinition = "text")
    private String avatarUrl;
    @Column(nullable = false)
    private boolean archived = false;

    @OneToMany(
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            mappedBy = "user"
    )
    private List<Notification> notifications;

    public User(String username, String password, UserType userType) {
        this.username = username;
        this.password = password;
        this.userType = userType;
    }

    public User() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
