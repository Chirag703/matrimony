package com.matrimony.dto;

import lombok.Data;

@Data
public class ProfileDto {

    private Long id;
    private Long userId;
    private String name;
    private String gender;
    private Integer age;
    private String height;
    private String maritalStatus;
    private String religion;
    private String caste;
    private String community;
    private String education;
    private String occupation;
    private String salary;
    private String smoking;
    private String drinking;
    private String diet;
    private String country;
    private String state;
    private String city;
    private String about;
    private String photoUrl;
    private Integer profileCompletion;
    private Boolean verified;
    private Boolean premium;
}
