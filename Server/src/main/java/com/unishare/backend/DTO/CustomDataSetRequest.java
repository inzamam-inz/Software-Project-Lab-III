package com.unishare.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomDataSetRequest {
    private Integer customModelId;
    private Integer customUserId;
    private Integer customProductId;
    private Double rating;
    private Date createdAt;
}
