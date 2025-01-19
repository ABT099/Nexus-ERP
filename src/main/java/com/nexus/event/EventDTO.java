package com.nexus.event;

import java.time.ZonedDateTime;
import java.util.Objects;

public class EventDTO implements Comparable<EventDTO> {
    private  Integer eventId;
    private  String eventName;
    private ZonedDateTime date;
    private  boolean urgent;

    public EventDTO(Integer eventId, String EventName, ZonedDateTime date, boolean urgent) {
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

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
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

    @Override
    public int compareTo(EventDTO other) {
        int urgentComparison = Boolean.compare(other.isUrgent(), this.isUrgent());
        if (urgentComparison != 0) {
            return urgentComparison;
        }
        return this.date.compareTo(other.getDate());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventDTO that = (EventDTO) obj;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(eventId);
    }
}
