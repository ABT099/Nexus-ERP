package com.nexus.event;

import com.nexus.notification.NotificationManager;
import com.nexus.notification.NotificationHolderDto;
import com.nexus.notification.NotificationType;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.concurrent.Executor;

@Component
public class EventManager {
    private static final Map<Long, ConcurrentSkipListSet<EventHolderDto>> adminEvents = new ConcurrentHashMap<>();

    private static final Comparator<EventHolderDto> EVENT_COMPARATOR =
            Comparator.comparing(EventHolderDto::isUrgent).reversed()
                    .thenComparing(EventHolderDto::getDate, Comparator.nullsLast(Comparator.naturalOrder()));

    private final NotificationManager notificationManager;
    private final EventRepository eventRepository;
    private final Executor taskExecutor;

    public EventManager(NotificationManager notificationManager, EventRepository eventRepository, @Qualifier("taskExecutor") Executor taskExecutor) {
        this.notificationManager = notificationManager;
        this.eventRepository = eventRepository;
        this.taskExecutor = taskExecutor;
    }

    public static Map<Long, ConcurrentSkipListSet<EventHolderDto>> getAdminEvents() {
        return adminEvents;
    }

    @Scheduled(fixedRate = 3600000)
    public void scheduledUrgentCheck() {
        taskExecutor.execute(() -> {
            Instant now = Instant.now();
            adminEvents.forEach((adminId, events) -> updateUrgentStatus(events, now));
        });
    }

    public void addEvent(Set<Long> adminIds, EventHolderDto event) {
        taskExecutor.execute(() -> {
            Instant now = Instant.now();
            adminIds.forEach(adminId -> {
                adminEvents.computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>(EVENT_COMPARATOR)).add(event);
                ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
                updateUrgentStatus(events, now);
                sendReminder(adminId, events);
            });
        });
    }

    public void addEvent(Long adminId, EventHolderDto event) {
        taskExecutor.execute(() -> {
            Instant now = Instant.now();
            adminEvents.computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>(EVENT_COMPARATOR)).add(event);
            ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
            updateUrgentStatus(events, now);
            sendReminder(adminId, events);
        });
    }

    public void removeEvent(Set<Long> adminIds, EventHolderDto event) {
        taskExecutor.execute(() -> {
            adminIds.forEach(adminId -> {
                ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
                if (events != null) {
                    events.remove(event);
                    if (events.isEmpty()) {
                        adminEvents.remove(adminId);
                    }
                }
            });
        });
    }

    public void removeEvent(Long adminId, EventHolderDto event) {
        taskExecutor.execute(() -> {
            ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
            if (events != null) {
                events.remove(event);
                if (events.isEmpty()) {
                    adminEvents.remove(adminId);
                }
            }
        });
    }

    private void updateUrgentStatus(ConcurrentSkipListSet<EventHolderDto> events, Instant now) {
        if (events == null) return;

        Set<Integer> eventIdsToUpdate = new HashSet<>();
        events.removeIf(event -> {
            long hoursDifference = ChronoUnit.HOURS.between(event.getDate().toInstant(), now);
            if (hoursDifference < 0) {
                return true;
            } else if (hoursDifference > 0 && hoursDifference < 24 && !event.isUrgent()) {
                event.setUrgent(true);
                eventIdsToUpdate.add(event.getEventId());
            }
            return false;
        });

        if (!eventIdsToUpdate.isEmpty()) {
            eventRepository.updateUrgentToTrue(eventIdsToUpdate);
        }
    }

    private void sendReminder(Long adminId, ConcurrentSkipListSet<EventHolderDto> events) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<NotificationHolderDto> notifications = new ArrayList<>();
        for (EventHolderDto event : events) {
            LocalDate eventDay = event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (eventDay.equals(today) || eventDay.equals(tomorrow)) {
                LocalTime eventTime = event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                String dayDescription = eventDay.equals(today) ? "today" : "tomorrow";
                notifications.add(new NotificationHolderDto(
                        adminId,
                        "Event Reminder",
                        "Reminder for the event: " + event.getEventName() + " scheduled for: " + dayDescription + " at " + eventTime,
                        NotificationType.REMINDER
                ));
            }
        }

        if (!notifications.isEmpty()) {
            notifications.forEach(notificationManager::addNotification);
            notificationManager.flush();
        }
    }
}