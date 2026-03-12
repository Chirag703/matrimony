package com.matrimony.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BasicInfoRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Date of birth is required")
    private LocalDate dob;

    @NotBlank(message = "Gender is required")
    private String gender;

    private String height;
    private String maritalStatus;
}
