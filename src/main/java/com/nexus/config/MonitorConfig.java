package com.nexus.config;

import com.nexus.ineteraction.Interaction;
import com.nexus.monitor.MonitorManager;
import com.nexus.notification.NotificationDTO;
import com.nexus.notification.NotificationType;
import com.nexus.project.Project;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorConfig {
    @PostConstruct
    public void registerStrategies() {
        MonitorManager.registerStrategy(Project.class, (
                project,
                repo,
                notifier,
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
