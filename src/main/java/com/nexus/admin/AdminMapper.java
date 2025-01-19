package com.nexus.admin;

import com.nexus.event.EventMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AdminMapper {

    private final EventMapper eventMapper;

    public AdminMapper(EventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    public BasicAdminResponse toBasicAdminResponse(Admin admin) {
        return new BasicAdminResponse(
                admin.getId(),
                admin.getUser().getId(),
                admin.getUser().getAvatarUrl(),
                admin.getFirstName(),
                admin.getFirstName());
    }

    public AdminResponse toAdminResponse(Admin admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getUser().getId(),
                admin.getUser().getAvatarUrl(),
                admin.getFirstName(),
                admin.getFirstName(),
                admin.getEvents().stream().map(eventMapper::toBasicEventResponse).collect(Collectors.toSet())
        );
    }
}
