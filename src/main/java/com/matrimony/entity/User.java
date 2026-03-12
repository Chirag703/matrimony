package com.matrimony.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String phone;

    @Column(length = 100)
    private String name;

    @Column(length = 10)
    private String gender;

    private LocalDate dob;

    @Column(nullable = false)
    private Boolean premium = false;

    @Column(nullable = false)
    private Boolean banned = false;

    @Column(name = "onboarding_complete", nullable = false)
    private Boolean onboardingComplete = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastActive = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastActive = LocalDateTime.now();
    }
}
