package com.matrimony.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.service-account-key:firebase-service-account.json}")
    private String serviceAccountKeyPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        try {
            ClassPathResource resource = new ClassPathResource(serviceAccountKeyPath);
            InputStream serviceAccountStream = resource.getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            log.info("Firebase initialized successfully");
            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.warn("Firebase service account key not found at '{}'. " +
                    "FCM push notifications will be disabled. " +
                    "Add firebase-service-account.json to src/main/resources to enable FCM.",
                    serviceAccountKeyPath);
            // Return a dummy/null-safe placeholder so the bean can still be wired
            return null;
        }
    }
}
