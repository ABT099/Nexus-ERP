package com.nexus.chat;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("messages")
public class MessageController {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChatRepository chatRepository;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageRepository messageRepository, UserService userService, ChatRepository chatRepository, MessageMapper messageMapper, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.chatRepository = chatRepository;
        this.messageMapper = messageMapper;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("{chatId}")
    public ResponseEntity<List<MessageResponse>> getByChatId(@Valid @Positive @PathVariable Long chatId) {
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);

        return ResponseEntity.ok(messages.stream()
                .map(messageMapper::map)
                .toList());
    }

    @MessageMapping("/send-message")
    @Transactional
    public void sendMessage(@Valid MessageRequest messageRequest) {
        User user = userService.findById(messageRequest.senderId());

        Chat chat = chatRepository.findById(messageRequest.chatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        Message message = new Message(user, chat, messageRequest.text());

        messageRepository.save(message);

        MessageResponse messageResponse = messageMapper.map(message);

        sendToQueue(message, messageResponse);
    }

    @PutMapping
    @Transactional
    public void update(@Valid @RequestBody UpdateMessageRequest messageRequest) {
        Message message = findMessageById(messageRequest.id());

        if (Objects.equals(messageRequest.text(), message.getText())) {
            return;
        }

        message.setText(messageRequest.text());
        messageRepository.save(message);

        MessageResponse messageResponse = messageMapper.map(message);

        sendToQueue(message, messageResponse);
    }

    @DeleteMapping("{id}")
    @Transactional
    public void delete(@Valid @Positive @PathVariable long id) {
        Message message = findMessageById(id);

        messageRepository.delete(message);

        MessageResponse messageResponse = messageMapper.map(message);

        sendToQueue(message, messageResponse);
    }

    private Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("message with id: " + id + " not found")
                );
    }

    private void sendToQueue(Message message, MessageResponse messageResponse) {
        messagingTemplate.convertAndSend(
                "/queue/messages" + message.getChat().getId(),
                messageResponse);
    }
}
