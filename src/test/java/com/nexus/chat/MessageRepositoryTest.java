package com.nexus.chat;


import com.nexus.user.User;
import com.nexus.user.UserRepository;
import com.nexus.user.UserType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.ZonedDateTime;
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

        Chat chat = chatRepository.save(new Chat("chat"));

        chatRepository.save(chat);

        List<Message> messages = List.of(
                new Message(user, chat, "Message 1"),
                new Message(user, chat, "Message 2"),
                new Message(user, chat, "Message 3")
        );

        messages.getFirst().setCreatedAt(ZonedDateTime.now().minusMinutes(3));
        messages.get(1).setCreatedAt(ZonedDateTime.now().minusMinutes(2));
        messages.get(2).setCreatedAt(ZonedDateTime.now().minusMinutes(1));

        messageRepository.saveAll(messages);

        // Act
        List<Message> actual = messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());

        // Assert
        assertEquals(3, actual.size());

        for (int i = 0; i < actual.size() - 1; i++) {
            assertTrue(
                    actual.get(i).getCreatedAt().isBefore(actual.get(i + 1).getCreatedAt()) ||
                            actual.get(i).getCreatedAt().isEqual(actual.get(i + 1).getCreatedAt()),
                    "Messages are not ordered by createdAt in ascending order."
            );
        }
    }
}
