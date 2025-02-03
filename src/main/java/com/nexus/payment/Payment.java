package com.nexus.payment;

import com.nexus.abstraction.AbstractFinancial;
import com.nexus.project.Project;
import com.nexus.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Payment extends AbstractFinancial {

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    public Payment(double amount, Instant paymentDate, Project project, User payer, UUID tenantId) {
        super(amount, paymentDate, project);
        this.payer = payer;
        setTenantId(tenantId);
    }

    public Payment(double amount, Instant paymentDate, User payer, UUID tenantId) {
        super(amount, paymentDate);
        this.payer = payer;
        setTenantId(tenantId);
    }

    public Payment() {
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }
}
