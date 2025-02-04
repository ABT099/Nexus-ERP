package com.nexus.event;

import org.springframework.stereotype.Component;

@Component
public class EventMapper {
    public BasicEventResponse toBasicEventResponse(Event event) {
        return new BasicEventResponse(
            event.getName(),
            event.getType().name(),
            event.getStatus().name(),
            event.getDate().toString(),
            event.isUrgent()
        );
    }

    public EventResponse toEventResponse(Event event) {
        return new EventResponse(
                event.getName(),
                event.getDescription(),
                event.getType().name(),
                event.getStatus().name(),
                event.getDate().toString(),
                event.isUrgent(),
                event.isArchived()
        );
    }
}
