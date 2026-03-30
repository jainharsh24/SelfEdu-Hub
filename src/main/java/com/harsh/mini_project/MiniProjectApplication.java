package com.harsh.mini_project;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MiniProjectApplication {

    public static void main(String[] args) {
        loadEnv();
        SpringApplication.run(MiniProjectApplication.class, args);
    }

    private static void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();
        setIfMissing("GROQ_API_KEY", dotenv.get("GROQ_API_KEY"));
        setIfMissing("YOUTUBE_API_KEY", dotenv.get("YOUTUBE_API_KEY"));
    }

    private static void setIfMissing(String key, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (System.getProperty(key) == null) {
            System.setProperty(key, value);
        }
    }

}
