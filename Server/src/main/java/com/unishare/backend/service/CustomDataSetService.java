package com.unishare.backend.service;

import com.unishare.backend.DTO.CustomDataSetRequest;
import com.unishare.backend.DTO.CustomDataSetResponse;
import com.unishare.backend.exceptionHandlers.ErrorMessageException;
import com.unishare.backend.model.CustomDataSet;
import com.unishare.backend.model.CustomModel;
import com.unishare.backend.model.User;
import com.unishare.backend.repository.CustomDataSetRepository;
import com.unishare.backend.repository.CustomModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomDataSetService {
    private final CustomDataSetRepository customDataSetRepository;
    private final CustomModelRepository customModelRepository;
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CustomModelService customModelService;

    public CustomDataSetResponse makeResponse(CustomDataSet customDataSet) {
        return new CustomDataSetResponse(
                customDataSet.getId(),
                customDataSet.getCustomUserId(),
                customDataSet.getCustomProductId(),
                customDataSet.getRating(),
                customDataSet.getCreatedAt(),
                userService.makeResponse(customDataSet.getUser()),
                customModelService.makeResponse(customDataSet.getCustomModel())
        );
    }

    public CustomDataSetResponse getCustomDataSetById(Integer id) {
        CustomDataSet customDataSet = customDataSetRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("CustomDataSet not found with ID: " + id));
        return makeResponse(customDataSet);
    }

    public List<CustomDataSetResponse> getAllCustomDataSets() {
        List<CustomDataSet> customDataSets = customDataSetRepository.findAll();
        return customDataSets.stream()
                .map(this::makeResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<CustomDataSetResponse> getAllCustomDataSetsByUserId(Integer userId) {
        List<CustomDataSet> customDataSets = customDataSetRepository.findAllByUserId(userId);
        return customDataSets.stream()
                .map(this::makeResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<CustomDataSetResponse> getAllCustomDataSetsByModelId(Integer customModelId) {
        List<CustomDataSet> customDataSets = customDataSetRepository.findAllByCustomModelId(customModelId);
        return customDataSets.stream()
                .map(this::makeResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<CustomDataSetResponse> getAllCustomDataSetByUserIdAndCustomModelId(Integer userId, Integer customModelId) {
        List<CustomDataSet> customDataSets = customDataSetRepository.findAllByUserIdAndCustomModelId(userId, customModelId);
        return customDataSets.stream()
                .map(this::makeResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public CustomDataSetResponse addCustomDataSet(String token, CustomDataSetRequest request) {
        User user = authenticationService.getMe(token);
        if (user == null) {
            throw new ErrorMessageException("User not found");
        }

        CustomModel customModel = customModelRepository.findById(request.getCustomModelId())
                .orElseThrow(() -> new ErrorMessageException("CustomModel not found with ID: " + request.getCustomModelId()));

        CustomDataSet customDataSet = new CustomDataSet();
        customDataSet.setCustomUserId(request.getCustomUserId());
        customDataSet.setCustomProductId(request.getCustomProductId());
        customDataSet.setRating(request.getRating());
        customDataSet.setCreatedAt(request.getCreatedAt());
        customDataSet.setUser(user);
        customDataSet.setCustomModel(customModel);

        customDataSetRepository.save(customDataSet);
        return makeResponse(customDataSet);
    }
}
