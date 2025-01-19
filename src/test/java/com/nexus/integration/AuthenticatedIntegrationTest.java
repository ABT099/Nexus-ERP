package com.nexus.integration;

import com.github.javafaker.Faker;
import com.nexus.config.TestContainerConfig;
import com.nexus.user.User;
import com.nexus.user.UserCreationContext;
import com.nexus.user.UserDTO;
import com.nexus.user.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = TestContainerConfig.class)
public abstract class AuthenticatedIntegrationTest {

    @Autowired
    protected WebTestClient webTestClient;

    @Autowired
    private UserCreationContext userContext;
    protected Faker faker = new Faker();
    protected String token;
    protected User user;

    protected void createUser() {
        String username = faker.name().username();
        String password = faker.internet().password();


        UserDTO userDto = userContext.create(username, password, UserType.SUPER_USER);

        user = userDto.user();
        assertNotNull(user);

        token = "Bearer " + userDto.token();
        assertNotNull(token);
    }
}
