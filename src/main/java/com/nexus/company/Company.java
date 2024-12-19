package com.nexus.company;

import com.nexus.abstraction.AbstractAppUser;
import com.nexus.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Company extends AbstractAppUser {

    @Column(columnDefinition = "text")
    private String companyName;

    public Company(User user, String companyName) {
        super(user);
        this.companyName = companyName;
    }

    public Company() {}

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
