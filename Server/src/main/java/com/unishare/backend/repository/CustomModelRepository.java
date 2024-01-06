package com.unishare.backend.repository;

import com.unishare.backend.model.CustomModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomModelRepository extends JpaRepository<CustomModel, Integer> {
    List<CustomModel> findAllByUserId(Integer userId);
    List<CustomModel> findAllByIsPublic(Boolean isPublic);

    @Query("SELECT cm FROM CustomModel cm WHERE cm.title = :title")
    List<CustomModel> findAllByTitle(@Param("title") String title);
}
