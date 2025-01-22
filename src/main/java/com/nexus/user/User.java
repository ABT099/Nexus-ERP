package com.nexus.user;

import com.nexus.abstraction.TenantAware;
import com.nexus.notification.Notification;
import com.nexus.payment.Payment;
import com.nexus.project.Project;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "_user")
public class User extends TenantAware {

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
    private boolean archived;

    @OneToMany(
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            mappedBy = "user"
    )
    private List<Notification> notifications;

    @OneToMany(
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            mappedBy = "payer"
    )
    private List<Payment> payments;

    @OneToMany(
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            mappedBy = "owner"
    )
    private List<Project> projects;

    public User(String username, String password, UserType userType, String tenantId) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        archived = false;
        setTenantId(tenantId);
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isArchived() {
        return archived;
    }
}
