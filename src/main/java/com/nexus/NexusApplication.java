package com.nexus;

import com.nexus.auth.user.UserCreationService;
import com.nexus.auth.user.UserType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NexusApplication {

	public static void main(String[] args) {
		SpringApplication.run(NexusApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(UserCreationService uCR) {
		return args -> {
			uCR.create("abdo", "1234@a", UserType.SUPER_USER);
		};
	}
}
