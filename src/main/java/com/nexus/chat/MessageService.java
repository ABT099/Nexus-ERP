package com.nexus.chat;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;


    public MessageService(MessageRepository messageRepository, UserRepository userRepository, ChatRepository chatRepository, MessageMapper messageMapper, SimpMessagingTemplate messagingTemplate) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.messageMapper = messageMapper;
        this.messagingTemplate = messagingTemplate;
    }

    public List<MessageResponse> findAllByChatId(Long chatId) {
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);

        return messages.stream()
                .map(messageMapper::map)
                .toList();
    }

    @Transactional
    public void sendAndSave(MessageRequest messageRequest) {
        User user = userRepository.findById(messageRequest.senderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = chatRepository.findById(messageRequest.chatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        Message message = new Message(user, chat, messageRequest.text());

        messageRepository.save(message);

        MessageResponse messageResponse = messageMapper.map(message);

        messagingTemplate.convertAndSend(
                "/queue/messages" + message.getChat().getId(),
                messageResponse);

        messagingTemplate.convertAndSend(
                "/queue/messages" + message.getChat().getId(),
                messageResponse);
    }

    @Transactional
    public void update(UpdateMessageRequest messageRequest) {
        Message message = findMessageById(messageRequest.id());

        if (Objects.equals(messageRequest.text(), message.getText())) {
            return;
        }

        message.setText(messageRequest.text());
        messageRepository.save(message);

        MessageResponse messageResponse = messageMapper.map(message);

        messagingTemplate.convertAndSend(
                "/queue/messages" + message.getChat().getId(),
                messageResponse);

        messagingTemplate.convertAndSend(
                "/queue/messages" + message.getChat().getId(),
                messageResponse);
    }

    @Transactional
    public void delete(Long id) {
        Message message = findMessageById(id);

        messageRepository.delete(message);

        MessageResponse messageResponse = messageMapper.map(message);

        messagingTemplate.convertAndSend(
                "/queue/messages" + message.getChat().getId(),
                messageResponse);
    }

    private Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("message with id: " + id + " not found")
                );
    }
}
