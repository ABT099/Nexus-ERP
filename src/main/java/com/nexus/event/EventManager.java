package com.nexus.event;

import com.nexus.notification.NotificationManager;
import com.nexus.notification.NotificationHolderDto;
import com.nexus.notification.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(EventManager.class);
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
        LOG.info("Starting scheduled urgent status check");
        taskExecutor.execute(() -> {
            Instant now = Instant.now();
            adminEvents.forEach((adminId, events) -> {
                LOG.debug("Checking urgent status for adminId: {}", adminId);
                updateUrgentStatus(events, now);
            });
        });
        LOG.info("Scheduled urgent status check completed");
    }

    public void addEvent(Set<Long> adminIds, EventHolderDto event) {
        LOG.info("Adding event '{}' for multiple admins: {}", event.getEventName(), adminIds);
        taskExecutor.execute(() -> {
            Instant now = Instant.now();
            adminIds.forEach(adminId -> {
                adminEvents.computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>(EVENT_COMPARATOR)).add(event);
                ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
                updateUrgentStatus(events, now);
                sendReminder(adminId, events);
                LOG.debug("Event '{}' added for adminId: {}", event.getEventName(), adminId);
            });
        });
    }

    public void addEvent(Long adminId, EventHolderDto event) {
        LOG.info("Adding event '{}' for adminId: {}", event.getEventName(), adminId);
        taskExecutor.execute(() -> {
            Instant now = Instant.now();
            adminEvents.computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>(EVENT_COMPARATOR)).add(event);
            ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
            updateUrgentStatus(events, now);
            sendReminder(adminId, events);
            LOG.debug("Event '{}' added for adminId: {}", event.getEventName(), adminId);
        });
    }

    public void removeEvent(Set<Long> adminIds, EventHolderDto event) {
        LOG.info("Removing event '{}' for multiple admins: {}", event.getEventName(), adminIds);
        taskExecutor.execute(() -> {
            adminIds.forEach(adminId -> {
                ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
                if (events != null) {
                    events.remove(event);
                    if (events.isEmpty()) {
                        adminEvents.remove(adminId);
                        LOG.debug("All events removed for adminId: {}", adminId);
                    }
                }
            });
        });
    }

    public void removeEvent(Long adminId, EventHolderDto event) {
        LOG.info("Removing event '{}' for adminId: {}", event.getEventName(), adminId);
        taskExecutor.execute(() -> {
            ConcurrentSkipListSet<EventHolderDto> events = adminEvents.get(adminId);
            if (events != null) {
                events.remove(event);
                if (events.isEmpty()) {
                    adminEvents.remove(adminId);
                    LOG.debug("All events removed for adminId: {}", adminId);
                }
            }
        });
    }

    private void updateUrgentStatus(ConcurrentSkipListSet<EventHolderDto> events, Instant now) {
        if (events == null) return;

        LOG.debug("Updating urgent status for {} events", events.size());
        Set<Integer> eventIdsToUpdate = new HashSet<>();
        events.removeIf(event -> {
            long hoursDifference = ChronoUnit.HOURS.between(event.getDate().toInstant(), now);
            if (hoursDifference < 0) {
                LOG.debug("Removing expired event: {}", event.getEventName());
                return true;
            } else if (hoursDifference > 0 && hoursDifference < 24 && !event.isUrgent()) {
                event.setUrgent(true);
                eventIdsToUpdate.add(event.getEventId());
                LOG.debug("Marked event '{}' as urgent", event.getEventName());
            }
            return false;
        });

        if (!eventIdsToUpdate.isEmpty()) {
            LOG.info("Updating urgent status in repository for event IDs: {}", eventIdsToUpdate);
            eventRepository.updateUrgentToTrue(eventIdsToUpdate);
        }
    }

    private void sendReminder(Long adminId, ConcurrentSkipListSet<EventHolderDto> events) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        LOG.debug("Sending reminders for adminId: {}", adminId);
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
            LOG.info("Reminders sent for adminId: {}", adminId);
        }
    }
}
