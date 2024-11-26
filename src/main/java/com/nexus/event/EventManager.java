package com.nexus.event;

import com.nexus.notification.NotificationManager;
import com.nexus.notification.NotificationHolderDto;
import com.nexus.notification.NotificationType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class EventManager {
    private static final Map<Long, ConcurrentSkipListSet<EventHolderDto>> adminEvents = new ConcurrentHashMap<>();

    private static final Comparator<EventHolderDto> EVENT_COMPARATOR =
            Comparator.comparing(EventHolderDto::isUrgent).reversed()
                    .thenComparing(EventHolderDto::getDate);

    private final NotificationManager notificationManager;
    private final EventRepository eventRepository;

    public EventManager(NotificationManager notificationManager, EventRepository eventRepository) {
        this.notificationManager = notificationManager;
        this.eventRepository = eventRepository;
    }

    public static Map<Long, ConcurrentSkipListSet<EventHolderDto>> getAdminEvents() {
        return adminEvents;
    }

    @Async
    @Scheduled(fixedRate = 3600000)
    public void scheduledUrgentCheck() {
        Instant now = Instant.now();
        for (Map.Entry<Long, ConcurrentSkipListSet<EventHolderDto>> entry : adminEvents.entrySet()) {
            ConcurrentSkipListSet<EventHolderDto> events = entry.getValue();
            updateUrgentStatus(events, now);
        }
    }

    @Async
    public void addEvent(Set<Long> adminIds, EventHolderDto event) {
        Instant now = Instant.now();

        for (Long adminId : adminIds) {
            adminEvents.computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>(EVENT_COMPARATOR))
                    .add(event);

            ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);

            updateUrgentStatus(events, now);
            sendReminder(adminId, events);
        }
    }

    @Async
    public void addEvent(Long adminId, EventHolderDto event) {
        Instant now = Instant.now();

        adminEvents.computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>(EVENT_COMPARATOR))
                .add(event);

        ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);

        updateUrgentStatus(events, now);
        sendReminder(adminId, events);
    }

    @Async
    public void removeEvent(Set<Long> adminIds, EventHolderDto event) {
        for (Long adminId : adminIds) {
            ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
            if (events != null) {
                events.remove(event);
                if (events.isEmpty()) {
                    adminEvents.remove(adminId);
                }
            }
        }
    }

    @Async
    public void removeEvent(Long adminId, EventHolderDto event) {
        ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
        if (events != null) {
            events.remove(event);
            if (events.isEmpty()) {
                adminEvents.remove(adminId);
            }
        }
    }

    private void updateUrgentStatus(ConcurrentSkipListSet<EventHolderDto> events, Instant now) {
        if (events == null) return;

        Set<Integer> eventIdsToUpdate = new HashSet<>();

        Iterator<EventHolderDto> iterator = events.iterator();
        while (iterator.hasNext()) {
            EventHolderDto event = iterator.next();
            long hoursDifference = ChronoUnit.HOURS.between(event.getDate().toInstant(), now);

            if (hoursDifference > 0 && hoursDifference < 24 && !event.isUrgent()) {
                event.setUrgent(true);
                eventIdsToUpdate.add(event.getEventId());
            } else if (hoursDifference < 0) {
                iterator.remove();
            }
        }

        eventRepository.updateUrgentToTrue(eventIdsToUpdate);
    }

    private void sendReminder(Long adminId, ConcurrentSkipListSet<EventHolderDto> events) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        for (EventHolderDto event : events) {
            LocalDate eventDay = event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime eventTime = event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

            if ((eventDay.equals(today) || eventDay.equals(tomorrow))) {
                String dayDescription = eventDay.equals(today) ? "today" : "tomorrow";
                NotificationHolderDto notification = new NotificationHolderDto(
                        adminId,
                        "Event Reminder",
                        "Reminder for the event: " + event.getEventName() + " scheduled for: " + dayDescription + " at " + eventTime.toString(),
                        NotificationType.REMINDER
                );
                notificationManager.addNotification(notification);
            }
        }

        notificationManager.flush();
    }
}
