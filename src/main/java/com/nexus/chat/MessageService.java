package com.nexus.chat;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    public MessageService(MessageRepository messageRepository, UserRepository userRepository, ChatRepository chatRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
    }

    public List<Message> findAllByChatId(Long chatId) {
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
    }

    @Transactional
    public Message save(MessageRequest messageRequest) {
        User user = userRepository.findById(messageRequest.senderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = chatRepository.findById(messageRequest.chatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        Message message = new Message(user, chat, messageRequest.text());

        messageRepository.save(message);

        return message;
    }

    @Transactional
    public Message update(Long id, String text) {
        Message message = findMessageById(id);

        message.setText(text);

        messageRepository.save(message);

        return message;
    }

    public Message delete(Long id) {
        Message message = findMessageById(id);

        messageRepository.delete(message);

        return message;
    }

    private Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("message with id: " + id + " not found")
                );
    }
}
