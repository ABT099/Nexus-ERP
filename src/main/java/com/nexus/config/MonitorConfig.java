package com.nexus.config;

import com.nexus.event.Event;
import com.nexus.expense.Expense;
import com.nexus.file.File;
import com.nexus.ineteraction.Interaction;
import com.nexus.monitor.MonitorManager;
import com.nexus.notification.NotificationDTO;
import com.nexus.notification.NotificationType;
import com.nexus.payment.Payment;
import com.nexus.project.Project;
import com.nexus.projectstep.ProjectStep;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorConfig {
    @PostConstruct
    public void registerStrategies() {
        MonitorManager.registerStrategy(Event.class, (
                event,
                repo,
                notifier,
                actionType,
                args) -> {

            if (args.length > 0) {
                String body ="";
                String title = "";
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            } else {
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            }

            repo.save(new Interaction());
            notifier.flush();
        });

        MonitorManager.registerStrategy(Project.class, (
                project,
                repo,
                notifier,
                actionType,
                args) -> {

            if (args.length > 0) {
                String body ="";
                String title = "";
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            } else {
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            }

            repo.save(new Interaction());
            notifier.flush();
        });

        MonitorManager.registerStrategy(ProjectStep.class, (
                projectStep,
                repo,
                notifier,
                actionType,
                args) -> {

            if (args.length > 0) {
                String body ="";
                String title = "";
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            } else {
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            }

            repo.save(new Interaction());
            notifier.flush();
        });

        MonitorManager.registerStrategy(Payment.class, (
                payment,
                repo,
                notifier,
                actionType,
                args) -> {

            if (args.length > 0) {
                String body ="";
                String title = "";
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            } else {
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            }

            repo.save(new Interaction());
            notifier.flush();
        });

        MonitorManager.registerStrategy(Expense.class, (
                expense,
                repo,
                notifier,
                actionType,
                args) -> {

            if (args.length > 0) {
                String body ="";
                String title = "";
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            } else {
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            }

            repo.save(new Interaction());
            notifier.flush();
        });

        MonitorManager.registerStrategy(File.class, (
                file,
                repo,
                notifier,
                actionType,
                args) -> {

            if (args.length > 0) {
                String body ="";
                String title = "";
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            } else {
                notifier.addNotification(new NotificationDTO(1L, "", "", NotificationType.NEW_UPDATE));
            }

            repo.save(new Interaction());
            notifier.flush();
        });
    }

}
