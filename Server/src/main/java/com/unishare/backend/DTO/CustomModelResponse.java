package com.unishare.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomModelResponse {
    private Integer id;
    private String title;
    private String description;
    private Boolean isPublic;
    private UserResponse userResponse;
}
