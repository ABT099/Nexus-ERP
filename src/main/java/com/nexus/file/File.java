package com.nexus.file;

import com.nexus.project.Project;
import com.nexus.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.util.HashSet;
import java.util.Set;

@Entity
public class File extends AbstractAuditable<User, Integer> {
    private String name;
    private String description;
    private String type;
    private String url;
    @ManyToMany(
            fetch = FetchType.LAZY,
            mappedBy = "files"
    )
    private Set<Project> projects = new HashSet<>();

    public File(String name, String description, String type, String url) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.url = url;
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
}
