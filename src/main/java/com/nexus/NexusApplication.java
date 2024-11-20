package com.nexus;

import com.nexus.user.UserCreationService;
import com.nexus.user.UserType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableJpaAuditing
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
