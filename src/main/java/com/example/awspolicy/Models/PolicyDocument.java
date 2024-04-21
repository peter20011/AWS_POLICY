package com.example.awspolicy.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
public class PolicyDocument {
    @JsonProperty("Version")
    private String version;

    @JsonProperty("Statement")
    private List<Statement> statements;

}
