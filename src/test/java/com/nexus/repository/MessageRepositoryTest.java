package com.nexus.repository;


import com.nexus.chat.Chat;
import com.nexus.chat.ChatRepository;
import com.nexus.chat.Message;
import com.nexus.chat.MessageRepository;
import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByChatIdOrderByCreatedAtAsc() {
        // Arrange
        User user = new User("username", "password", UserType.SUPER_USER);

        userRepository.save(user);

        Chat chat = chatRepository.save(new Chat());

        chatRepository.save(chat);

        List<Message> messages = List.of(
                new Message(user, chat, "Message 1"),
                new Message(user, chat, "Message 2"),
                new Message(user, chat, "Message 3")
        );

        messages.getFirst().setCreatedAt(Date.from(ZonedDateTime.now().minusDays(1).toInstant()));
        messages.get(1).setCreatedAt(Date.from(ZonedDateTime.now().minusDays(1).toInstant()));
        messages.get(2).setCreatedAt(Date.from(ZonedDateTime.now().minusDays(1).toInstant()));

        messageRepository.saveAll(messages);

        // Act
        List<Message> actual = messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());

        // Assert
        assertEquals(3, actual.size());

        for (int i = 0; i < actual.size() - 1; i++) {
            assertTrue(
                    actual.get(i).getCreatedAt().compareTo(actual.get(i + 1).getCreatedAt()) < 0 ||
                            actual.get(i).getCreatedAt().compareTo(actual.get(i + 1).getCreatedAt()) == 0,
                    "Messages are not ordered by createdAt in ascending order."
            );
        }
    }
}
