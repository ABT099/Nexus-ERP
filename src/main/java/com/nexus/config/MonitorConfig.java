package com.nexus.config;

import com.nexus.abstraction.AbstractAppUser;
import com.nexus.admin.Admin;
import com.nexus.email.SendEmailService;
import com.nexus.event.Event;
import com.nexus.expense.Expense;
import com.nexus.ineteraction.Interaction;
import com.nexus.monitor.ActionType;
import com.nexus.monitor.MonitorManager;
import com.nexus.notification.NotificationDTO;
import com.nexus.notification.NotificationManager;
import com.nexus.notification.NotificationType;
import com.nexus.payment.Payment;
import com.nexus.project.Project;
import com.nexus.projectstep.ProjectStep;
import com.nexus.user.User;
import com.nexus.user.UserService;
import com.nexus.user.UserType;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MonitorConfig {

    private final UserService userService;
    private final SendEmailService sendEmailService;

    public MonitorConfig(UserService userService, SendEmailService sendEmailService) {
        this.userService = userService;
        this.sendEmailService = sendEmailService;
    }

    @PostConstruct
    public void registerStrategies() {
        MonitorManager.registerStrategy(Event.class, (
                event,
                repo,
                notifier,
                actionType,
                args) -> {

            String title = "";
            StringBuilder body = new StringBuilder();

            User changedBy = event.getLastModifiedBy().orElseThrow(
                    () -> new RuntimeException("Event has no last modified by user!")
            );

            if (args.length > 0 && actionType.equals(ActionType.UPDATE)) {
                Instant changedAt = event.getLastModifiedDate().orElseThrow(
                        () -> new RuntimeException("Event has no last modified by user!")
                );

                title = "An Event Has Been Updated!";
                body.append(String.format("%s Updated Event: %s, at: %s\n", changedBy.getUsername(), event, changedAt));
                body.append(args[0]);
            } else {
                switch (actionType) {
                    case CREATE -> {
                        User createdBy = event.getCreatedBy().orElseThrow(
                                () -> new RuntimeException("Event has no creator!")
                        );

                        title = "An Event Has Been Created!";
                        body.append(String.format("%s Created Event: %s\n", createdBy, event));
                    }
                    case ADD_ADMIN -> {
                        title = "New Admin Has Been Added to Event!";
                        body.append(String.format("%s Added %s Event: %s\n", changedBy, event, args[0]));
                    }
                    case REMOVE_ADMIN -> {
                        title = "Admin Has Been Removed from Event!";
                        body.append(String.format("%s Removed %s Event: %s\n", changedBy, event, args[0]));
                    }
                    case DELETE -> {
                        title = "An Event Has Been Deleted!";
                        body.append(String.format("%s Deleted Event: %s\n", changedBy, event));
                    }
                }
            }

            List<NotificationDTO> notificationDTOS = new ArrayList<>();

            for (Admin admin : event.getAdmins()) {
                notificationDTOS.add(new NotificationDTO(
                        admin.getUser().getId(),
                        title, body.toString(),
                        NotificationType.NEW_UPDATE
                ));
            }

            notifier.addBatchNotification(notificationDTOS);
            notifier.flush();

            sendEmailService.sendEmail("abdo.personal99@gmail.com", title, body.toString());
        });

        MonitorManager.registerStrategy(Project.class, (
                project,
                repo,
                notifier,
                actionType,
                args) -> {

            String title ="";
            StringBuilder body = new StringBuilder();

            User changedBy = project.getLastModifiedBy().orElseThrow(
                    () -> new RuntimeException("Project has no last modified by user!")
            );

            Instant changedAt = project.getLastModifiedDate().orElseThrow(
                    () -> new RuntimeException("Project has no last modified by user!")
            );

            if (args.length > 0 && actionType.equals(ActionType.UPDATE)) {
                title = "A Project Has Been Updated!";
                body.append(String.format("%s Updated Project: %s, at: %s\n", changedBy, project, changedAt));
                body.append(args[0]);
            } else {
                switch (actionType) {
                    case CREATE -> {
                        User createdBy = project.getCreatedBy().orElseThrow(
                                () -> new RuntimeException("Project has no creator!")
                        );

                        title = "A Project Has Been Created!";
                        body.append(String.format("%s Created Project: %s\n", createdBy, project));
                    }
                    case DELETE -> {
                        title = "A Project Has Been Deleted!";
                        body.append(String.format("%s Deleted Project: %s\n", changedBy, project));
                    }
                    case ADD_FILE -> {
                        title = "A Project Has Been Added to File!";
                        body.append(String.format("%s Added a new File: %s\n", changedBy, args[0]));
                    }
                    case REMOVE_FILE -> {
                        title = "A Project Has Been Removed from File!";
                        body.append(String.format("%s Removed File: %s\n", changedBy, args[0]));
                    }
                }
            }

            Interaction interaction = new Interaction(changedBy, title, body.toString(), changedAt);
            repo.save(interaction);

            List<User> users = userService.findAllByTenantIdAndUserType(project.getTenantId(), UserType.ADMIN);
            users.add(project.getOwner());

            prepareAndSendNotifications(notifier, title, body.toString(), users);
        });

        MonitorManager.registerStrategy(ProjectStep.class, (
                projectStep,
                repo,
                notifier,
                actionType,
                args) -> {

            String title = "";
            StringBuilder body = new StringBuilder();

            User changedBy = projectStep.getLastModifiedBy().orElseThrow(
                    () -> new RuntimeException("Project step has no last modified by user!")
            );

            if (args.length > 0 && actionType.equals(ActionType.UPDATE)) {
                title = String.format("A Step Has Been Updated For Project: %s", projectStep.getProject().getName());
                body.append(String.format("%s Updated Step %s for Project: %s\n",
                        changedBy, projectStep.getName(), projectStep.getProject().getName()
                ));
                body.append(args[0]);
            } else {
                switch (actionType) {
                    case CREATE -> {
                        User createdBy = projectStep.getCreatedBy().orElseThrow(
                                () -> new RuntimeException("Project step has no creator!")
                        );

                        title = String.format("A Step Has Been Add For Project: %s", projectStep.getProject().getName());
                        body.append(String.format(
                                "%s Added A New Step: %s for Project: %s",
                                createdBy, projectStep, projectStep.getProject().getName()
                        ));
                    }
                    case DELETE -> {
                        title = String.format("A Step Has Been Removed For Project: %s", projectStep.getProject().getName());
                        body.append(String.format(
                                "%s Removed Step: %s for project: %s",
                                changedBy, projectStep, projectStep.getProject().getName()
                        ));
                    }
                    case ADD_EMPLOYEE -> {
                        title = String.format("New Employee Has Been Added to Project: %s", projectStep.getProject().getName());
                        body.append(String.format(
                                "%s Added Employee: %s to Step: %s\n", changedBy, args[0], projectStep.getName()
                        ));
                    }
                    case REMOVE_EMPLOYEE -> {
                        title = String.format("Employee Has Been Removed From Project: %s", projectStep.getProject().getName());
                        body.append(String.format(
                                "%s Removed Employee: %s from Step: %s\n", changedBy, args[0], projectStep.getName()
                        ));
                    }
                }
            }

            Instant changedAt = projectStep.getLastModifiedDate().orElseThrow(
                    () -> new RuntimeException("Project step has no last modified by user!")
            );

            Interaction interaction = new Interaction(changedBy, title, body.toString(), changedAt);
            repo.save(interaction);

            List<User> users = userService.findAllByTenantIdAndUserType(
                    projectStep.getProject().getTenantId(),
                    UserType.ADMIN
            );

            users.addAll(projectStep.getEmployees().stream().
                    map(AbstractAppUser::getUser)
                    .toList());

            prepareAndSendNotifications(notifier, title, body.toString(), users);
        });

        MonitorManager.registerStrategy(Payment.class, (
                payment,
                repo,
                notifier,
                actionType,
                args) -> {

            String title = "";
            StringBuilder body = new StringBuilder();

            User changedBy = payment.getLastModifiedBy().orElseThrow(
                    () -> new RuntimeException("Project step has no last modified by user!")
            );

            if (args.length > 0 && actionType.equals(ActionType.UPDATE)) {
                title = "A Payment Has Been Updated!";
                body.append(String.format("%s Updated Payment: %s\n", changedBy, payment));
            } else {
                switch (actionType) {
                    case CREATE -> {
                        title = "A New Payment Received!";
                        body.append(String.format("%s Have Payed: %s\n", payment.getPayer(), payment.getAmount()));
                    }
                    case DELETE -> {
                        title = "A Payment Has Been Deleted!";
                        body.append(String.format("%s Deleted Payment: %s\n", changedBy, payment));
                    }
                }
            }

            List<User> users = userService.findAllByTenantIdAndUserType(payment.getTenantId(), UserType.ADMIN);

            prepareAndSendNotifications(notifier, title, body.toString(), users);
        });

        MonitorManager.registerStrategy(Expense.class, (
                expense,
                repo,
                notifier,
                actionType,
                args) -> {

            String title = "";
            StringBuilder body = new StringBuilder();

            User changedBy = expense.getLastModifiedBy().orElseThrow(
                    () -> new RuntimeException("Project step has no last modified by user!")
            );

            if (args.length > 0 && actionType.equals(ActionType.UPDATE)) {
                title = "A Expense Has Been Updated!";
                body.append(String.format("%s Updated Expense: %s", changedBy, expense));
            } else {
                switch (actionType) {
                    case CREATE -> {
                        User createdBy = expense.getCreatedBy().orElseThrow(
                                () -> new RuntimeException("Expense has no last modified by user!")
                        );

                        title = "An Expense Has Been Created!";
                        body.append(String.format("%s Created Expense: %s\n", createdBy, expense));
                    }
                    case DELETE -> {
                        title = "An Expense Has Been Deleted!";
                        body.append(String.format("%s Deleted Expense: %s\n", changedBy, expense));
                    }
                }
            }

            List<User> users = userService.findAllByTenantIdAndUserType(expense.getTenantId(), UserType.ADMIN);

            prepareAndSendNotifications(notifier, title, body.toString(), users);
        });
    }

    private void prepareAndSendNotifications(NotificationManager notifier, String title, String body, List<User> users) {
        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (User user : users) {
            notificationDTOS.add(new NotificationDTO(
                    user.getId(),
                    title, body,
                    NotificationType.NEW_UPDATE
            ));

            sendEmailService.sendEmail("abdo.personal99@gmail.com", title, body);
        }

        notifier.addBatchNotification(notificationDTOS);
        notifier.flush();
    }

}
