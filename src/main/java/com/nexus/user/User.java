package com.nexus.user;

import com.nexus.abstraction.TenantAware;
import com.nexus.income.Income;
import com.nexus.notification.Notification;
import com.nexus.project.Project;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "_user")
public class User extends TenantAware {

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
    private List<Income> incomes;

    @OneToMany(
            orphanRemoval = true,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            mappedBy = "owner"
    )
    private List<Project> projects;

    public User(String username, String password, UserType userType, UUID tenantId) {
        this.username = username;
        this.password = password;
        this.userType = userType;
        archived = false;
        setTenantId(tenantId);
    }

    public User() {}

    public Long getId() {
        return super.getId();
    }

    public void setId(Long id) {
        super.setId(id);
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
