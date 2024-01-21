package com.apiflows.model;

import java.util.ArrayList;
import java.util.List;

public class OpenAPIWorkflow {

    private String workflowsSpec;
    private Info info;
    private List<SourceDescription> sourceDescriptions = new ArrayList<>();
    private List<Workflow> workflows = new ArrayList<>();
    private Components components;

    public String getWorkflowsSpec() {
        return workflowsSpec;
    }

    public void setWorkflowsSpec(String workflowsSpec) {
        this.workflowsSpec = workflowsSpec;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<SourceDescription> getSourceDescriptions() {
        return sourceDescriptions;
    }

    public void setSourceDescriptions(List<SourceDescription> sourceDescriptions) {
        this.sourceDescriptions = sourceDescriptions;
    }

    public List<Workflow> getWorkflows() {
        return workflows;
    }

    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows;
    }

    public Components getComponents() {
        return components;
    }

    public void setComponents(Components components) {
        this.components = components;
    }

}
