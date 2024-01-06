package com.unishare.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Integer id;
    private Integer userId;
    private Integer movieId;
    private Double rating;
    private Boolean isAddedToModel;
    private String createdAt;
}
