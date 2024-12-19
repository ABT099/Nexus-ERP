package com.nexus.employee;

import com.nexus.abstraction.AbstractPerson;
import com.nexus.project.Project;
import com.nexus.projectstep.ProjectStep;
import com.nexus.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;

import java.util.List;

@Entity
public class Employee extends AbstractPerson {
    @ManyToMany(
            mappedBy = "employees",
            fetch = FetchType.LAZY
    )
    private List<Project> assignedProjects;
    @ManyToMany(
            mappedBy = "employees",
            fetch = FetchType.LAZY
    )
    private List<ProjectStep> assignedSteps;

    public Employee(User user, String firstName, String lastName) {
        super(user, firstName, lastName);
    }

    public Employee() {}
}
