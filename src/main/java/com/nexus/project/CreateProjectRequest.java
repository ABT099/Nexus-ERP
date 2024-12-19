package com.nexus.project;

import com.nexus.abstraction.CreateWorkItemRequest;

import java.time.ZonedDateTime;

public final class CreateProjectRequest extends CreateWorkItemRequest {

    private final Long ownerId;
    private final double price;

    public CreateProjectRequest(String name, String description, ZonedDateTime startDate, ZonedDateTime expectedEndDate, Long ownerId, double price) {
        super(name, description, startDate, expectedEndDate);
        this.ownerId = ownerId;
        this.price = price;
    }

    public Long ownerId() {
        return ownerId;
    }

    public double price() {
        return price;
    }
}
