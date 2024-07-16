package com.viancis.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"repository.question", "repository.user", "repository.comment"})
@EntityScan(basePackages = {"entities.comment", "entities.question", "entities.users"})
@ComponentScan(basePackages = {"filter", "config", "com.viancis", "exception"})
public class CommentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommentApplication.class, args);
    }

}
