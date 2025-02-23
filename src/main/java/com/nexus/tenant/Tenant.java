package com.nexus.tenant;

import com.nexus.stripe.SubscriptionStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
public class Tenant {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", updatable = false, nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT", unique = true)
    String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String phoneNumber;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus subscriptionStatus;

    @Column(unique = true)
    private String stripeCustomerId; // For subscriptions

    @Column(unique = true)
    private String stripeAccountId;

    public Tenant(String name, String email, String phoneNumber, String stripeCustomerId) {
        this.createdDate = Instant.now();
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.stripeCustomerId = stripeCustomerId;
        this.subscriptionStatus = SubscriptionStatus.PENDING;
    }

    public Tenant() {}

    public UUID getId() {
        return id;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public String getStripeCustomerId() {
        return stripeCustomerId;
    }

    public void setStripeCustomerId(String stripeCustomerId) {
        this.stripeCustomerId = stripeCustomerId;
    }

    public String getStripeAccountId() {
        return stripeAccountId;
    }

    public void setStripeAccountId(String stripeAccountId) {
        this.stripeAccountId = stripeAccountId;
    }
}
