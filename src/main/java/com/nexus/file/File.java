package com.nexus.file;

import com.nexus.abstraction.AuditableTenantAware;
import com.nexus.project.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class File extends AuditableTenantAware<Integer> {

    @Column(
            columnDefinition = "text",
            nullable = false
    )
    private String name;

    @Column(
            columnDefinition = "text",
            nullable = false
    )
    private String description;

    @Column(
            columnDefinition = "text",
            nullable = false
    )
    private String type;

    @Column(
            columnDefinition = "text",
            nullable = false
    )
    private String url;

    @ManyToMany(
            fetch = FetchType.LAZY,
            mappedBy = "files"
    )
    private Set<Project> projects = new HashSet<>();

    @Column(nullable = false)
    private boolean archived = false;

    public File(String name, String description, String type, String url, UUID tenantId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.url = url;
        setTenantId(tenantId);
    }

    public File() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}
