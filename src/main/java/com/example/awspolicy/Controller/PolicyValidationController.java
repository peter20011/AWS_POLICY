package com.example.awspolicy.Controller;


import com.example.awspolicy.Models.Policy;
import com.example.awspolicy.Services.PolicyFileValidationService;
import com.example.awspolicy.Services.PolicyValidationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/policy")
public class PolicyValidationController {

    private final PolicyValidationService policyValidationService;
    private final PolicyFileValidationService policyFileValidationService;

    public PolicyValidationController(PolicyValidationService policyValidationService, PolicyFileValidationService policyFileValidationService) {
        this.policyValidationService = policyValidationService;
        this.policyFileValidationService = policyFileValidationService;
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validatePolicy(@RequestBody Policy policyJson) {
        try {
            boolean isValid = policyValidationService.validatePolicy(policyJson);
            return ResponseEntity.ok(isValid);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }

    @PostMapping("/validateFile")
    public ResponseEntity<Boolean> validatePolicyFromFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(false);
        }

        try {
            boolean isValid = policyFileValidationService.validatePolicyFromFile(file);
            return ResponseEntity.ok(isValid);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(false);
        }
    }
}
