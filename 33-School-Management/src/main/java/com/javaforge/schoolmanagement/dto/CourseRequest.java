package com.javaforge.schoolmanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CourseRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9-]{2,20}") String code,
        @NotBlank @Size(max = 120) String title,
        @Min(1) @Max(6) int credits
) {
}
