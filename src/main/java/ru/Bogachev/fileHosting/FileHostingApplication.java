package ru.Bogachev.fileHosting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FileHostingApplication {
    public static void main(final String[] args) {
        SpringApplication.run(FileHostingApplication.class, args);
    }
}
