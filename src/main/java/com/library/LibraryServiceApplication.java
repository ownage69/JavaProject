package com.library;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Library Service API",
                version = "1.0.0",
                description = "REST API for managing books, authors, publishers, "
                        + "categories, readers and loans.",
                contact = @Contact(name = "Library Service Team")
        )
)
@SpringBootApplication
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class LibraryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryServiceApplication.class, args);
    }
}
