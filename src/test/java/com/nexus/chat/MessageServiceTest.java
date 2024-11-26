package com.nexus.chat;

import com.nexus.exception.ResourceNotFoundException;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private UserRepository userRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void findAllByChatId_shouldReturnMessagesByChatId() {
        // Arrange
        List<Message> messages = List.of(
                new Message(),
                new Message()
        );

        when(messageRepository.findByChatIdOrderByCreatedAtAsc(1L)).thenReturn(messages);

        // Act
        List<Message> actual = messageService.findAllByChatId(1L);

        // Assert
        assertIterableEquals(messages, actual);
        verify(messageRepository).findByChatIdOrderByCreatedAtAsc(1L);
    }

    @Test
    void save_shouldSaveMessage() {
        // Arrange
        MessageRequest request = new MessageRequest(1L, 1L, "red", "text");
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(chatRepository.findById(1L)).thenReturn(Optional.of(new Chat()));

        // Act
        messageService.save(request);

        // Assert
        verify(userRepository).findById(1L);
        verify(chatRepository).findById(1L);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void save_shouldThrowException_whenChatNotFound() {
        // Arrange
        MessageRequest request = new MessageRequest(1L, 1L, "red", "text");
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(chatRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> messageService.save(request)
        );

        // Verify
        verify(userRepository).findById(1L);
        verify(chatRepository).findById(1L);
        verifyNoInteractions(messageRepository);
    }

    @Test
    void update_shouldUpdateMessage() {
        Message message = new Message();
        message.setText("oldText");

        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        String newText = "newText";
        messageService.update(1L, newText);

        assertEquals(newText, message.getText());
        verify(messageRepository).findById(1L);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void update_shouldThrowException_whenMessageNotFound() {
        // Arrange
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> messageService.update(1L, "newText")
        );

        // Verify
        verify(messageRepository).findById(1L);
        verifyNoMoreInteractions(messageRepository);
    }

    @Test
    void delete_shouldDeleteMessage_whenMessageExists() {
        // Arrange
        Message message = new Message();
        when(messageRepository.findById(1L)).thenReturn(Optional.of(message));

        // Act
        Message deletedMessage = messageService.delete(1L);

        // Assert
        assertEquals(message, deletedMessage);
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
}
