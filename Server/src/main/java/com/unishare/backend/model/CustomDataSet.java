package com.unishare.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CustomDataSet {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer customUserId;
    private Integer customProductId;
    private Double rating;
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "modelId")
    private CustomModel customModel;
}
