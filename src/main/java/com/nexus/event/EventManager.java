package com.nexus.event;

import com.nexus.notification.NotificationManager;
import com.nexus.notification.NotificationDTO;
import com.nexus.notification.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;

@Component
public class EventManager {
    private static final Logger LOG = LoggerFactory.getLogger(EventManager.class);
    private static final Map<Long, ConcurrentSkipListSet<EventDTO>> adminEvents = new ConcurrentHashMap<>();
    private static final Comparator<EventDTO> EVENT_COMPARATOR =
            Comparator.comparing(EventDTO::isUrgent).reversed()
                    .thenComparing(EventDTO::getDate, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(EventDTO::getEventId);

    private final NotificationManager notificationManager;
    private final EventRepository eventRepository;
    private final Executor taskExecutor;

    public EventManager(NotificationManager notificationManager, EventRepository eventRepository, @Qualifier("taskExecutor") Executor taskExecutor) {
        this.notificationManager = notificationManager;
        this.eventRepository = eventRepository;
        this.taskExecutor = taskExecutor;
    }

    public static Map<Long, ConcurrentSkipListSet<EventDTO>> getAdminEvents() {
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

    public void addEvent(Set<Long> adminIds, EventDTO event) {
        LOG.info("Adding event '{}' for multiple admins: {}", event.getEventName(), adminIds);
        taskExecutor.execute(() -> {
            Instant now = Instant.now();
            adminIds.forEach(adminId -> {
                addAdminToEvent(adminId, event, now);
            });
        });
    }

    public void addEvent(Long adminId, EventDTO event) {
        LOG.info("Adding event '{}' for adminId: {}", event.getEventName(), adminId);
        taskExecutor.execute(() -> {
            Instant now = Instant.now();
            addAdminToEvent(adminId, event, now);
        });
    }

    private void addAdminToEvent(Long adminId, EventDTO event, Instant now) {
        adminEvents.computeIfAbsent(adminId, id -> new ConcurrentSkipListSet<>(EVENT_COMPARATOR)).add(event);
        ConcurrentSkipListSet<EventDTO> events = adminEvents.get(adminId);
        updateUrgentStatus(events, now);
        sendReminder(adminId, events);
        LOG.debug("Event '{}' added for adminId: {}", event.getEventName(), adminId);
    }

    public void removeEvent(Set<Long> adminIds, EventDTO event) {
        LOG.info("Removing event '{}' for multiple admins: {}", event.getEventName(), adminIds);
        taskExecutor.execute(() -> adminIds.forEach(adminId -> removeAdminFromEvent(adminId, event)));
    }

    public void removeEvent(Long adminId, EventDTO event) {
        LOG.info("Removing event '{}' for adminId: {}", event.getEventName(), adminId);
        taskExecutor.execute(() -> removeAdminFromEvent(adminId, event));
    }

    private void removeAdminFromEvent(Long adminId, EventDTO event) {
        ConcurrentSkipListSet<EventDTO> events = adminEvents.get(adminId);
        if (events != null) {
            events.remove(event);
            if (events.isEmpty()) {
                adminEvents.remove(adminId);
                LOG.debug("All events removed for adminId: {}", adminId);
            }
        }
    }

    private void updateUrgentStatus(ConcurrentSkipListSet<EventDTO> events, Instant now) {
        if (events == null) return;

        LOG.debug("Updating urgent status for {} events", events.size());

        Set<Integer> eventIdsToUpdate = new HashSet<>();

        events.removeIf(event -> {
            if (event.getDate() == null) {
                return true; // Remove events with null dates.
            }

            long hoursDifference = ChronoUnit.HOURS.between(event.getDate(), now);

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

    private void sendReminder(Long adminId, ConcurrentSkipListSet<EventDTO> events) {
        Instant now = Instant.now();
        Instant todayStart = now.truncatedTo(ChronoUnit.DAYS);
        Instant tomorrowStart = todayStart.plus(1, ChronoUnit.DAYS);

        LOG.debug("Sending reminders for adminId: {}", adminId);
        List<NotificationDTO> notifications = new ArrayList<>();

        for (EventDTO event : events) {
            if (event.getDate() == null) continue;
            Instant eventDay = event.getDate().truncatedTo(ChronoUnit.DAYS);
            if (eventDay.equals(todayStart) || eventDay.equals(tomorrowStart)) {
                Instant eventTime = event.getDate();
                String dayDescription = eventDay.equals(todayStart) ? "today" : "tomorrow";
                notifications.add(new NotificationDTO(
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
