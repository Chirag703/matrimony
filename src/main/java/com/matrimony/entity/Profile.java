package com.matrimony.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 50)
    private String religion;

    @Column(length = 50)
    private String caste;

    @Column(length = 100)
    private String community;

    @Column(length = 100)
    private String education;

    @Column(length = 100)
    private String occupation;

    @Column(length = 50)
    private String salary;

    @Column(length = 20)
    private String smoking;

    @Column(length = 20)
    private String drinking;

    @Column(length = 20)
    private String diet;

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String city;

    @Column(length = 20)
    private String height;

    @Column(name = "marital_status", length = 30)
    private String maritalStatus;

    @Column(columnDefinition = "TEXT")
    private String about;

    @Column(name = "photo_url", length = 255)
    private String photoUrl;

    @Column(name = "profile_completion")
    private Integer profileCompletion = 0;

    @Column(nullable = false)
    private Boolean verified = false;
}
