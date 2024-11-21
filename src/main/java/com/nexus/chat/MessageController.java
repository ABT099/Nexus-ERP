package com.nexus.chat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("messages")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/send-message")
    public void sendMessage(@Valid MessageRequest message) {
        var sentMessage = messageService.save(message);

        messagingTemplate.convertAndSendToUser(
                message.receiverUsername(),
                "/user/queue/messages" + message.chatId(),
                sentMessage);

        messagingTemplate.convertAndSendToUser(
                sentMessage.getSender().getUsername(),
                "/user/queue/messages" + message.chatId(),
                sentMessage);
    }

    @GetMapping("{chatId}")
    public ResponseEntity<List<Message>> getByChatId(@Valid @Positive @PathVariable Long chatId) {
        return ResponseEntity.ok(messageService.findAllByChatId(chatId));
    }

    @PutMapping
    public void update(@Valid @RequestBody UpdateMessageRequest message) {
        var updatedMessage = messageService.update(message.id(), message.text());

        messagingTemplate.convertAndSendToUser(
                message.receiverUsername(),
                "/user/queue/messages" + updatedMessage.getChat().getId(),
                updatedMessage);

        messagingTemplate.convertAndSendToUser(
                updatedMessage.getSender().getUsername(),
                "/user/queue/messages" + updatedMessage.getChat().getId(),
                updatedMessage);
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody UpdateMessageRequest message) {
        var deletedMessage = messageService.delete(message.id());

        messagingTemplate.convertAndSendToUser(
                message.receiverUsername(),
                "/user/queue/messages" + deletedMessage.getChat().getId(),
                deletedMessage);
    }
}
