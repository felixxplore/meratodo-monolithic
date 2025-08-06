package com.felix.meratodo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "MeraTodo Service API",
                description = "enhanced the efficiency of team collaboration",
                version = "1.0.0",
                contact= @Contact(name = "email",email = "satyampawar0070@gmail.com")

        )
)
public class MeraTodoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MeraTodoApplication.class, args);
    }
}

