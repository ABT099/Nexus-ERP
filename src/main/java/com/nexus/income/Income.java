package com.nexus.income;

import com.nexus.abstraction.AbstractPayment;
import com.nexus.project.Project;
import com.nexus.user.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Income extends AbstractPayment {

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @Column(columnDefinition = "text")
    private String chargeId;

    @Column(columnDefinition = "text")
    private String source;

    public Income(long amount, String currency, Instant paymentDate, Project project, User payer, UUID tenantId) {
        super(amount, paymentDate, project, currency);
        this.payer = payer;
        setTenantId(tenantId);
    }

    public Income(long amount, String currency, Instant paymentDate, User payer, UUID tenantId) {
        super(amount, paymentDate, currency);
        this.payer = payer;
        setTenantId(tenantId);
    }

    public Income() {
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }
}
