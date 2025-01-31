package com.nexus.email;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;

@Service
public class SendEmailService {

    private final JavaMailSender mailSender;
    private final Executor taskExecutor;


    @Value("${spring.mail.username}")
    private String from;

    public SendEmailService(JavaMailSender mailSender, @Qualifier("taskExecutor") Executor taskExecutor) {
        this.mailSender = mailSender;
        this.taskExecutor = taskExecutor;
    }


    public void sendEmail(String to, String subject, String text) {
        taskExecutor.execute(() -> {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("Nexus <" + from + ">");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
        });
    }
}
