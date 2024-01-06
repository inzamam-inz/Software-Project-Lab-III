package com.unishare.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomModel {
    @Id
    @GeneratedValue
    private Integer id;
    private String title;
    private String description;
    private Boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
