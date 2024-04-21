package com.example.awspolicy.Services;

import com.example.awspolicy.Models.Policy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class PolicyFileValidationService {

    private final PolicyValidationService policyValidationService;

    public PolicyFileValidationService(PolicyValidationService policyValidationService) {
        this.policyValidationService = policyValidationService;
    }

    public boolean validatePolicyFromFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String jsonData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Policy policy = new ObjectMapper().readValue(jsonData, Policy.class);
            return policyValidationService.validatePolicy(policy);
        } catch (Exception e) {
            System.err.println("Error reading or parsing file: " + e.getMessage());
            return false;
        }
    }

}
