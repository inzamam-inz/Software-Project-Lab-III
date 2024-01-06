package com.unishare.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unishare.backend.DTO.CustomModelRequest;
import com.unishare.backend.DTO.CustomModelResponse;
import com.unishare.backend.DTO.ReviewJson;
import com.unishare.backend.exceptionHandlers.ErrorMessageException;
import com.unishare.backend.model.CustomDataSet;
import com.unishare.backend.model.CustomModel;
import com.unishare.backend.model.Review;
import com.unishare.backend.model.User;
import com.unishare.backend.repository.CustomDataSetRepository;
import com.unishare.backend.repository.CustomModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomModelService {
    private final CustomDataSetRepository customDataSetRepository;
    private final CustomModelRepository customModelRepository;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    public CustomModelResponse makeResponse(CustomModel customModel) {
        return new CustomModelResponse(
                customModel.getId(),
                customModel.getTitle(),
                customModel.getDescription(),
                customModel.getIsPublic(),
                userService.makeResponse(customModel.getUser())
        );
    }

    public CustomModelResponse getCustomModelById(Integer id) {
        CustomModel customModel = customModelRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("CustomModel not found with ID: " + id));
        return makeResponse(customModel);
    }

    public List<CustomModelResponse> getAllCustomModels() {
        List<CustomModel> customModels = customModelRepository.findAll();
        return customModels.stream()
                .map(this::makeResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<CustomModelResponse> getAllCustomModelsByUserId(Integer userId) {
        List<CustomModel> customModels = customModelRepository.findAllByUserId(userId);
        return customModels.stream()
                .map(this::makeResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<CustomModelResponse> getAllCustomModelsByUserId(String token) {
        User user = authenticationService.getMe(token);
        if (user == null) {
            throw new ErrorMessageException("User not found");
        }

        List<CustomModel> customModels = customModelRepository.findAllByUserId(user.getId());
        return customModels.stream()
                .map(this::makeResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public CustomModelResponse addCustomModel(String token, CustomModelRequest request) {
        User user = authenticationService.getMe(token);
        if (user == null) {
            throw new ErrorMessageException("User not found");
        }

        if (request.getTitle() == null || customModelRepository.findAllByTitle(request.getTitle()).size() > 0) {
            throw new ErrorMessageException("Title is null or already exists");
        }

        CustomModel customModel = new CustomModel();
        customModel.setTitle(request.getTitle());
        customModel.setDescription(request.getDescription());
        customModel.setIsPublic(request.getIsPublic());
        customModel.setUser(user);
        customModelRepository.save(customModel);
        return makeResponse(customModel);
    }

    public String deleteCustomModel(Integer id) {
        CustomModel customModel = customModelRepository.findById(id)
                .orElseThrow(() -> new ErrorMessageException("CustomModel not found with ID: " + id));
        customModelRepository.delete(customModel);
        return "Custom Model deleted successfully";
    }

    public List<CustomModelResponse> getAllPublicCustomModels() {
        List<CustomModel> customModels = customModelRepository.findAllByIsPublic(true);
        return customModels.stream()
                .map(this::makeResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public CustomModelResponse builtInCustomModel(Integer id) {
        List<CustomDataSet> customDataSets = customDataSetRepository.findAllByCustomModelId(id);

        if (customDataSets.size() > 0) {
            addData(customDataSets);
        }

        return makeResponse(customModelRepository.findById(id).orElseThrow(() -> new ErrorMessageException("Custom Model not found with ID: " + id)));
    }

    public Boolean addData(List<CustomDataSet> customDataSets) {
        List<CustomDataSet> reviews = customDataSets;
        System.out.println(reviews.size());

        // convert to json
        List<ReviewJson> reviewJsons = reviews.stream().map(review -> new ReviewJson(
                review.getCustomUserId(),
                review.getCustomProductId(),
                review.getRating(),
                review.getCreatedAt()
        )).collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Convert the list to a JSON array
            String jsonArray = objectMapper.writeValueAsString(reviewJsons);

            // post to api
            try {
                String apiURL = "http://localhost:9000/addproject";
                RestTemplate restTemplate = new RestTemplate();

                // Set the request headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Set the request body as a string
                String requestBody = jsonArray;

                // Create the HTTP entity with headers and body
                HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

                // Make the POST request
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiURL, requestEntity, String.class);


//                ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiURL, jsonArray, String.class);
                String result = responseEntity.getBody();
                System.out.println(result);
            }
            catch (Exception e) {
                return false;
            }
            // Print the JSON array
            System.out.println("JSON array representation: " + jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
