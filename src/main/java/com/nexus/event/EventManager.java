package com.nexus.event;

import com.nexus.notification.NotificationManager;
import com.nexus.notification.NotificationHolderDto;
import com.nexus.notification.NotificationType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Component
public class EventManager {
    private static final Map<Long, ConcurrentSkipListSet<EventHolderDto>> adminEvents = new ConcurrentHashMap<>();

    private static final Comparator<EventHolderDto> EVENT_COMPARATOR =
            Comparator.comparing(EventHolderDto::isUrgent).reversed()
                    .thenComparing(EventHolderDto::getDate, Comparator.nullsLast(Comparator.naturalOrder()));

    private final NotificationManager notificationManager;
    private final EventRepository eventRepository;

    private final ThreadPoolTaskExecutor taskExecutor;

    public EventManager(NotificationManager notificationManager, EventRepository eventRepository, @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
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
            for (Map.Entry<Long, ConcurrentSkipListSet<EventHolderDto>> entry : adminEvents.entrySet()) {
                ConcurrentSkipListSet<EventHolderDto> events = entry.getValue();
                updateUrgentStatus(events, now);
            }
        });
    }

    public void addEvent(Set<Long> adminIds, EventHolderDto event) {
        taskExecutor.execute(() -> {
            Instant now = Instant.now();

            for (Long adminId : adminIds) {
                adminEvents.computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>(EVENT_COMPARATOR))
                        .add(event);

                ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);

                updateUrgentStatus(events, now);
                sendReminder(adminId, events);
            }
        });
    }

    public void addEvent(Long adminId, EventHolderDto event) {
        taskExecutor.execute(() -> {
            Instant now = Instant.now();
            adminEvents.computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>(EVENT_COMPARATOR))
                    .add(event);

            ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);

            updateUrgentStatus(events, now);
            sendReminder(adminId, events);
        });
    }

    public void removeEvent(Set<Long> adminIds, EventHolderDto event) {
        taskExecutor.execute(() -> {
            for (Long adminId : adminIds) {
                ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
                if (events != null) {
                    events.remove(event);
                    if (events.isEmpty()) {
                        adminEvents.remove(adminId);
                    }
                }
            }
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

        if (!eventIdsToUpdate.isEmpty()) {
            eventRepository.updateUrgentToTrue(eventIdsToUpdate);
        }
    }

    private void sendReminder(Long adminId, ConcurrentSkipListSet<EventHolderDto> events) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<NotificationHolderDto> notifications = events.stream()
                .filter(event -> {
                    LocalDate eventDay = event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return eventDay.equals(today) || eventDay.equals(tomorrow);
                })
                .map(event -> {
                    LocalDate eventDay = event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    LocalTime eventTime = event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                    String dayDescription = eventDay.equals(today) ? "today" : "tomorrow";
                    return new NotificationHolderDto(
                            adminId,
                            "Event Reminder",
                            "Reminder for the event: " + event.getEventName() + " scheduled for: " + dayDescription + " at " + eventTime.toString(),
                            NotificationType.REMINDER
                    );
                })
                .toList();

        if (!notifications.isEmpty()) {
            notifications.forEach(notificationManager::addNotification);
            notificationManager.flush();
        }
    }
}
