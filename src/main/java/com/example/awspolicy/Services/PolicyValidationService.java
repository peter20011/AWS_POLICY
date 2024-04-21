package com.example.awspolicy.Services;

import com.example.awspolicy.Models.Policy;
import com.example.awspolicy.Models.PolicyDocument;
import com.example.awspolicy.Models.Statement;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PolicyValidationService {

    private static final List<String> ALLOWED_EFFECTS = Arrays.asList("Allow", "Deny");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern POLICY_NAME_PATTERN = Pattern.compile("^[\\w+=,.@-]{1,128}$");

    private boolean isValidDateFormat(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean validateStatement(Statement statement) {
        if (statement == null || statement.getSid() == null || statement.getEffect() == null || statement.getResource() == null) {
            return false;
        }

        if (statement.getSid().isEmpty()) return false;
        if (!ALLOWED_EFFECTS.contains(statement.getEffect())) return false;
        if (statement.getResource() instanceof String && "*".equals(statement.getResource())) return false;
        if (statement.getResource() instanceof List) {
            List<String> resources = (List<String>) statement.getResource();
            if (resources.contains("*")) return false;
        }
        return true;
    }

    public boolean validatePolicy(Policy policy) {
        if (policy == null || policy.getPolicyName() == null || policy.getPolicyDocument() == null) {
            return false;
        }
        if (!POLICY_NAME_PATTERN.matcher(policy.getPolicyName()).matches()) {
            return false;
        }

        PolicyDocument policyDoc = policy.getPolicyDocument();
        if (policyDoc.getVersion() == null || !isValidDateFormat(policyDoc.getVersion())) {
            return false;
        }
        if (policyDoc.getStatements() == null || policyDoc.getStatements().isEmpty()) {
            return false;
        }

        for (Statement statement : policyDoc.getStatements()) {
            if (!validateStatement(statement)) {
                return false;
            }
        }
        return true;
    }
}