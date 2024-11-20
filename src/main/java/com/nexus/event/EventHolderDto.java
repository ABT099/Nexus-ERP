package com.nexus.event;

import java.util.Date;

public class EventHolderDto {
    private  Integer eventId;
    private  String eventName;
    private Date date;
    private  boolean urgent;

    public EventHolderDto(Integer eventId, String EventName, Date date, boolean urgent) {
        this.eventId = eventId;
        this.eventName = EventName;
        this.date = date;
        this.urgent = urgent;
    }
    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
