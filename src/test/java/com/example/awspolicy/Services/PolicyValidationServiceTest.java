package com.example.awspolicy.Services;

import com.example.awspolicy.Models.Policy;
import com.example.awspolicy.Models.PolicyDocument;
import com.example.awspolicy.Models.Statement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PolicyValidationServiceTest {

    @InjectMocks
    private PolicyValidationService service;

    @Test
    public void testValidatePolicy_ValidPolicy_ReturnsTrue() {
        // Arrange
        Statement statement = new Statement();
        statement.setSid("TestSID");
        statement.setEffect("Allow");
        statement.setResource("arn:aws:iam::123456789012:role/SampleRole");

        PolicyDocument document = new PolicyDocument();
        document.setVersion("2012-10-17");
        document.setStatements(List.of(statement));

        Policy policy = new Policy();
        policy.setPolicyName("ValidPolicy");
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testValidatePolicy_ResourceIsAsterisk_ReturnsFalse() {
        // Arrange
        Statement statement = new Statement();
        statement.setSid("TestSID");
        statement.setEffect("Allow");
        statement.setResource("*");

        PolicyDocument document = new PolicyDocument();
        document.setVersion("2012-10-17");
        document.setStatements(List.of(statement));

        Policy policy = new Policy();
        policy.setPolicyName("InvalidPolicy");
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testValidatePolicy_InvalidPolicyName_ReturnsFalse() {
        // Arrange
        Policy policy = new Policy();
        policy.setPolicyName(""); // Test with an empty name
        PolicyDocument document = new PolicyDocument();
        document.setVersion("2012-10-17");
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testValidatePolicy_InvalidDateFormat_ReturnsFalse() {
        // Arrange
        Statement statement = new Statement();
        statement.setSid("TestSID");
        statement.setEffect("Allow");
        statement.setResource("arn:aws:iam::123456789012:role/SampleRole");

        PolicyDocument document = new PolicyDocument();
        document.setVersion("invalid-date"); // Test with an invalid date format
        document.setStatements(List.of(statement));

        Policy policy = new Policy();
        policy.setPolicyName("SomePolicy");
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertFalse(result);
    }


    @Test
    public void testValidatePolicy_MultipleStatementsOneInvalid_ReturnsFalse() {
        // Arrange
        Statement validStatement = new Statement();
        validStatement.setSid("ValidSID");
        validStatement.setEffect("Allow");
        validStatement.setResource("arn:aws:iam::123456789012:role/ValidRole");

        Statement invalidStatement = new Statement();
        invalidStatement.setSid("InvalidSID");
        invalidStatement.setEffect("Allow");
        invalidStatement.setResource("*");

        PolicyDocument document = new PolicyDocument();
        document.setVersion("2012-10-17");
        document.setStatements(List.of(validStatement, invalidStatement));

        Policy policy = new Policy();
        policy.setPolicyName("MixedValidityPolicy");
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testValidatePolicy_MultipleInvalidResources_ReturnsFalse() {
        // Arrange
        Statement statementOne = new Statement();
        statementOne.setSid("FirstSID");
        statementOne.setEffect("Allow");
        statementOne.setResource("*");

        Statement statementTwo = new Statement();
        statementTwo.setSid("SecondSID");
        statementTwo.setEffect("Allow");
        statementTwo.setResource("*");

        PolicyDocument document = new PolicyDocument();
        document.setVersion("2012-10-17");
        document.setStatements(List.of(statementOne, statementTwo));

        Policy policy = new Policy();
        policy.setPolicyName("InvalidResourcesPolicy");
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testValidatePolicy_AllValidMultipleStatements_ReturnsTrue() {
        // Arrange
        Statement statementOne = new Statement();
        statementOne.setSid("FirstValidSID");
        statementOne.setEffect("Allow");
        statementOne.setResource("arn:aws:iam::123456789012:role/FirstValidRole");

        Statement statementTwo = new Statement();
        statementTwo.setSid("SecondValidSID");
        statementTwo.setEffect("Allow");
        statementTwo.setResource("arn:aws:iam::123456789012:role/SecondValidRole");

        PolicyDocument document = new PolicyDocument();
        document.setVersion("2012-10-17");
        document.setStatements(List.of(statementOne, statementTwo));

        Policy policy = new Policy();
        policy.setPolicyName("ValidPolicyMultipleStatements");
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testValidatePolicy_InvalidEffect_ReturnsFalse() {
        // Arrange
        Statement statement = new Statement();
        statement.setSid("InvalidEffectSID");
        statement.setEffect("InvalidEffect");  // An effect that is not in the allowed list
        statement.setResource("arn:aws:iam::123456789012:role/ValidRole");

        PolicyDocument document = new PolicyDocument();
        document.setVersion("2012-10-17");
        document.setStatements(List.of(statement));

        Policy policy = new Policy();
        policy.setPolicyName("InvalidEffectPolicy");
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testValidatePolicy_NullPolicy_ReturnsFalse() {
        // Act
        boolean result = service.validatePolicy(null);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testValidatePolicy_InvalidJSONStructure_ReturnsFalse() {
        // Arrange
        Policy policy = new Policy();  // missing PolicyDocument or other critical fields

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testValidatePolicy_MaximumLengthPolicyName_ReturnsTrue() {
        // Arrange
        Policy policy = new Policy();
        policy.setPolicyName("a".repeat(128));  // Max length for policy name

        // Create a sample statement
        Statement statement = new Statement();
        statement.setSid("ExampleSID");
        statement.setEffect("Allow");
        statement.setAction(List.of("iam:ListAllMyBuckets"));
        statement.setResource("arn:aws:s3:::example_bucket");

        // Create a PolicyDocument and add the statement
        PolicyDocument document = new PolicyDocument();
        document.setVersion("2012-10-17");
        document.setStatements(List.of(statement)); // Adding statement to document
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertTrue(result); // The test checks if the service correctly handles the maximum policy name length and valid statements.
    }


    @Test
    public void testValidatePolicy_SpecialCharactersInPolicyName_ReturnsTrue() {
        // Arrange
        Policy policy = new Policy();
        policy.setPolicyName("policy_name-with.special+characters@2023");

        Statement statement = new Statement();
        statement.setSid("TestSID");
        statement.setEffect("Allow");
        statement.setAction(List.of("iam:ListRoles", "iam:ListUsers"));
        statement.setResource("arn:aws:iam::123456789012:role/SampleRole");

        PolicyDocument document = new PolicyDocument();
        document.setVersion("2012-10-17");
        document.setStatements(List.of(statement)); // Adding a valid statement list
        policy.setPolicyDocument(document);

        // Act
        boolean result = service.validatePolicy(policy);

        // Assert
        assertTrue(result); // The test should pass if all fields including special characters in the name are handled correctly.
    }

    @Test
    public void testValidatePolicy_Concurrency_ReturnsConsistentResults() throws InterruptedException {
        // Arrange
        Runnable task = () -> {
            Policy policy = new Policy();
            policy.setPolicyName("ConcurrentPolicy");
            PolicyDocument document = new PolicyDocument();
            document.setVersion("2012-10-17");
            policy.setPolicyDocument(document);

            assertTrue(service.validatePolicy(policy));
        };

        // Act
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

}
