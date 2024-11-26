package com.nexus.chat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @MessageMapping("/send-message")
    public void sendMessage(@Valid MessageRequest message) {
        messageService.sendAndSave(message);
    }

    @GetMapping("{chatId}")
    public ResponseEntity<List<MessageResponse>> getByChatId(@Valid @Positive @PathVariable Long chatId) {
        return ResponseEntity.ok(messageService.findAllByChatId(chatId));
    }

    @PutMapping
    public void update(@Valid @RequestBody UpdateMessageRequest message) {
        messageService.update(message);
    }

    @DeleteMapping("{id}")
    public void delete(@Valid @Positive @PathVariable long id) {
        messageService.delete(id);
    }
}
