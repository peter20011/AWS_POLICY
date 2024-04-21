package com.example.awspolicy.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Policy {
    @JsonProperty("PolicyName")
    private String policyName;

    @JsonProperty("PolicyDocument")
    private PolicyDocument policyDocument;

}
