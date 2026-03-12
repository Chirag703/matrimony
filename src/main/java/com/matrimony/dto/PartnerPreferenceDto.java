package com.matrimony.dto;

import lombok.Data;

@Data
public class PartnerPreferenceDto {

    private Long id;
    private Long userId;
    private Integer minAge;
    private Integer maxAge;
    private String minHeight;
    private String maxHeight;
    private String religion;
    private String caste;
    private String education;
    private String occupation;
    private String location;
}
