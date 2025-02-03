package com.nexus.event;

import java.time.Instant;
import java.util.Objects;

public class EventDTO implements Comparable<EventDTO> {
    private final Long eventId;
    private final String eventName;
    private final Instant date;
    private boolean urgent;

    public EventDTO(Long eventId, String EventName, Instant date, boolean urgent) {
        this.eventId = eventId;
        this.eventName = EventName;
        this.date = date;
        this.urgent = urgent;
    }
    public Long getEventId() {
        return eventId;
    }

    public Instant getDate() {
        return date;
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

    @Override
    public int compareTo(EventDTO other) {
        // Compare by urgent status (urgent first)
        if (this.urgent != other.urgent) {
            return Boolean.compare(other.urgent, this.urgent); // Reversed for urgent first
        }

        // Compare by date (nulls last)
        if (this.date == null && other.date == null) {
            // Both dates are null, move to next comparison
        } else if (this.date == null) {
            return 1; // this.date is null, so it comes after
        } else if (other.date == null) {
            return -1; // other.date is null, so this comes first
        } else {
            int dateComparison = this.date.compareTo(other.date);
            if (dateComparison != 0) {
                return dateComparison;
            }
        }

        // Compare by eventId
        return this.eventId.compareTo(other.eventId);
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
        return Long.hashCode(eventId);
    }
}
