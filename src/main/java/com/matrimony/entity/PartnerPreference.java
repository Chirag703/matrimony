package com.matrimony.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "partner_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartnerPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "min_height", length = 20)
    private String minHeight;

    @Column(name = "max_height", length = 20)
    private String maxHeight;

    @Column(length = 50)
    private String religion;

    @Column(length = 50)
    private String caste;

    @Column(length = 100)
    private String education;

    @Column(length = 100)
    private String occupation;

    @Column(length = 100)
    private String location;
}
