package com.unishare.backend.repository;

import com.unishare.backend.model.CustomDataSet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomDataSetRepository extends JpaRepository<CustomDataSet, Integer> {
    List<CustomDataSet> findAllByUserId(Integer userId);
    List<CustomDataSet> findAllByCustomModelId(Integer customModelId);
    List<CustomDataSet> findAllByUserIdAndCustomModelId(Integer userId, Integer customModelId);
}
