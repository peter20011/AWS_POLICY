package com.example.awspolicy.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Statement {
    @JsonProperty("Sid")
    private String sid;

    @JsonProperty("Effect")
    private String effect;

    @JsonProperty("Action")
    private List<String> action;

    @JsonProperty("Resource")
    private Object resource;

}
