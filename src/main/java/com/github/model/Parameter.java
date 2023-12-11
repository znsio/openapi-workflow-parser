package com.github.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Parameter {

    private String name;
    private String in;
    private String value;
    private String target;


    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("in")
    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("target")
    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}