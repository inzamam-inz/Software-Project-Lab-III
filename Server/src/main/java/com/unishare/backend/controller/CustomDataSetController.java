package com.unishare.backend.controller;

import com.unishare.backend.DTO.CustomDataSetRequest;
import com.unishare.backend.DTO.CustomDataSetResponse;
import com.unishare.backend.service.CustomDataSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/custom-data-sets")
public class CustomDataSetController {
    final CustomDataSetService customDataSetService;

    @GetMapping
    public ResponseEntity<List<CustomDataSetResponse>> getAllData() {
        return ResponseEntity.ok(customDataSetService.getAllCustomDataSets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomDataSetResponse> getCustomDataSet(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(customDataSetService.getCustomDataSetById(id));
    }

    @GetMapping("/model/{modelId}")
    public ResponseEntity<List<CustomDataSetResponse>> getAllCustomDataSetsByModelId(
            @PathVariable Integer modelId
    ) {
        return ResponseEntity.ok(customDataSetService.getAllCustomDataSetsByModelId(modelId));
    }

    @PostMapping
    public ResponseEntity<CustomDataSetResponse> addCustomDataSet(
            @RequestHeader("Authorization") String token,
            @RequestBody CustomDataSetRequest request
    ) {
        return ResponseEntity.ok(customDataSetService.addCustomDataSet(token, request));
    }

}
