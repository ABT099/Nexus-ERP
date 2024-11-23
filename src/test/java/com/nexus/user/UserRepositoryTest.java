package com.nexus.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private UserRepository underTest;

    private final String username = "abdo";

    @BeforeEach
    public void setUp() {
        underTest.deleteAll();

        User user = new User(username, "password", UserType.ADMIN);
        underTest.save(user);
    }

    @Test
    public void existsUserByUsername() {
        User actual = underTest.findByUsername(username);

        assertThat(actual).isNotNull();
        assertThat(actual.getUsername()).isEqualTo(username);
    }

    @Test
    public void userExistsByUsername() {
        boolean exists = underTest.existsByUsername(username);
        assertThat(exists).isTrue();
    }

    @Test
    public void findUserIdByUsername() {
        String userId = underTest.findUserIdByUsername(username);
        assertThat(userId).isNotBlank();
    }
}
