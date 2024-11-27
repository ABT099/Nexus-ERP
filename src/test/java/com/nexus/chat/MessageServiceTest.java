package com.nexus.chat;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRepository chatRepository;
    @Mock
    private MessageMapper messageMapper;
    @InjectMocks
    private MessageService messageService;

    @Test
    void findAllByChatId_shouldReturnMessagesByChatId() {
        // Arrange
        User user = new User();
        Chat chat = new Chat("ad");
        Message message = new Message(user, chat, "text");
        Message message2 = new Message(user, chat, "text");

        List<Message> messages = List.of(
                message,
                message2
        );

        List<MessageResponse> r = List.of(
                new MessageResponse(1L, user.getId(), chat.getId(), message.getText(), ZonedDateTime.now()),
                new MessageResponse(1L, user.getId(), chat.getId(), message2.getText(), ZonedDateTime.now())
        );

        when(messageRepository.findByChatIdOrderByCreatedAtAsc(1L)).thenReturn(messages);
        when(messageMapper.map(message)).thenReturn(r.get(0));
        when(messageMapper.map(message2)).thenReturn(r.get(1));

        // Act
        List<MessageResponse> responses = messageService.findAllByChatId(1L);

        // Assert
        assertEquals(messages.get(0).getText(), responses.get(0).text()); // Corrected from getFirst()
        assertEquals(messages.get(1).getText(), responses.get(1).text()); // Corrected from getFirst()
        verify(messageRepository).findByChatIdOrderByCreatedAtAsc(1L);
    }

    @Test
    void save_shouldSaveMessage() {
        // Arrange
        MessageRequest request = new MessageRequest(1L, 1L, "text");
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));

        // Act
        messageService.sendAndSave(request);

        // Assert
        verify(userRepository).findById(1L);
        verify(chatRepository).findById(1L);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void save_shouldThrowException_whenChatNotFound() {
        // Arrange
        MessageRequest request = new MessageRequest(1L, 1L, "text");
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> messageService.sendAndSave(request)
        );

        // Verify
        verify(userRepository).findById(1L);
        verify(chatRepository).findById(1L);
        verifyNoInteractions(messageRepository);
    }

    @Test
    void update_shouldUpdateMessage() {
        Message message = createMessage();

        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        String newText = "newText";
        UpdateMessageRequest request = new UpdateMessageRequest(1L, newText);

        messageService.update(request);

        assertEquals(newText, message.getText());
        verify(messageRepository).findById(1L);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void update_shouldThrowException_whenMessageNotFound() {
        // Arrange
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateMessageRequest request = new UpdateMessageRequest(1L, "newText");

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> messageService.update(request)
        );

        // Verify
        verify(messageRepository).findById(1L);
        verifyNoMoreInteractions(messageRepository);
    }

    @Test
    void delete_shouldDeleteMessage_whenMessageExists() {
        // Arrange
        Message message = createMessage();
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        // Act
        messageService.delete(1L);

        // Assert
        verify(messageRepository).findById(1L);
        verify(messageRepository).delete(message);
    }

    @Test
    void delete_shouldThrowException_whenMessageNotFound() {
        // Arrange
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> messageService.delete(1L)
        );

        // Verify
        verify(messageRepository).findById(1L);
        verifyNoMoreInteractions(messageRepository);
    }

    private Message createMessage() {
        Chat chat = new Chat("ad");
        Message message = new Message();

        message.setChat(chat);
        message.setText("text");

        return message;
    }
}
