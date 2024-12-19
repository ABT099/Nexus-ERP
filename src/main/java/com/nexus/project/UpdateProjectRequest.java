package com.nexus.project;

import com.nexus.abstraction.UpdateWorkItemRequest;

import java.time.ZonedDateTime;

public final class UpdateProjectRequest extends UpdateWorkItemRequest {
    private final double price;
    public UpdateProjectRequest(int id, String name, String description, double price, ZonedDateTime StartDate, ZonedDateTime ExpectedEndDate, ZonedDateTime actualEndDate) {
        super(id, name, description, StartDate, ExpectedEndDate, actualEndDate);
        this.price = price;
    }

    public double price() {
        return price;
    }
}
