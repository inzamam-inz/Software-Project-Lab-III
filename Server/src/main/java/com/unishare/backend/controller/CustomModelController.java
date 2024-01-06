package com.unishare.backend.controller;

import com.unishare.backend.DTO.CustomModelRequest;
import com.unishare.backend.DTO.CustomModelResponse;
import com.unishare.backend.service.CustomModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/custom-models")
public class CustomModelController {
    final CustomModelService customModelService;

    @GetMapping()
    public ResponseEntity<List<CustomModelResponse>> getAllCustomModels() {
        return ResponseEntity.ok(customModelService.getAllCustomModels());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CustomModelResponse>> getAllCustomModelsByUserId(
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(customModelService.getAllCustomModelsByUserId(userId));
    }

    @GetMapping("/user")
    public ResponseEntity<List<CustomModelResponse>> getAllCustomModelsByUserId(
            @RequestHeader("Authorization") String token
    ) {
        return ResponseEntity.ok(customModelService.getAllCustomModelsByUserId(token));
    }

    @GetMapping("public")
    public ResponseEntity<List<CustomModelResponse>> getAllPublicCustomModels() {
        return ResponseEntity.ok(customModelService.getAllPublicCustomModels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomModelResponse> getCustomModelById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(customModelService.getCustomModelById(id));
    }

    @PostMapping
    public ResponseEntity<CustomModelResponse> addCustomModel(
            @RequestHeader("Authorization") String token,
            @RequestBody CustomModelRequest request
            ) {
        return ResponseEntity.ok(customModelService.addCustomModel(token, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomModel(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(customModelService.deleteCustomModel(id));
    }

    @PostMapping("/build/{id}")
    public ResponseEntity<CustomModelResponse> buildCustomModel(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(customModelService.builtInCustomModel(id));
    }

}
