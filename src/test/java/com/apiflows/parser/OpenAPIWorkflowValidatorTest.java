package com.apiflows.parser;

import com.apiflows.model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenAPIWorkflowValidatorTest {

    OpenAPIWorkflowValidator validator = new OpenAPIWorkflowValidator();

    @Test
    void validate() {
        OpenAPIWorkflow openAPIWorkflow = new OpenAPIWorkflow();
        OpenAPIWorkflowValidatorResult result = new OpenAPIWorkflowValidator(openAPIWorkflow).validate();

        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("'workflowsSpec' is undefined", result.getErrors().get(0));
    }

    @Test
    void validateInfoVersion() {
        Info info = new Info();
        info.setTitle("title");

        assertEquals(1, validator.validateInfo(info).size());
    }

    @Test
    void validateSourceDescriptions() {
        List<SourceDescription> sourceDescriptions = null;

        assertEquals(1, validator.validateSourceDescriptions(sourceDescriptions).size());
    }

    @Test
    void validateSourceDescriptionsWithoutUrl() {
        List<SourceDescription> sourceDescriptions = new ArrayList<>();
        sourceDescriptions.add(new SourceDescription()
                .name("Source one")
                .type("openapi")
                .url(null));
        assertEquals(1, validator.validateSourceDescriptions(sourceDescriptions).size());
    }

    @Test
    void validateSourceDescriptionsInvalidType() {
        List<SourceDescription> sourceDescriptions = new ArrayList<>();
        sourceDescriptions.add(new SourceDescription()
                .name("Source one")
                .type("unkwown")
                .url("https://example.com/spec.json"));
        assertEquals(1, validator.validateSourceDescriptions(sourceDescriptions).size());
    }

    @Test
    void validateWorkflowWithoutWorkflowId() {
        Workflow workflow = new Workflow()
                .workflowId(null)
                .addStep(new Step()
                        .stepId("step-one"));
        int index = 0;

        assertEquals(1, validator.validateWorkflow(workflow, index).size());
    }

    @Test
    void validateWorkflowWithoutSteps() {
        Workflow workflow = new Workflow()
                .workflowId("workflow-id-1");
        int index = 0;

        assertEquals(1, validator.validateWorkflow(workflow, index).size());
    }

    @Test
    void validateStep() {
        Step step = new Step()
                .stepId("step-one")
                .description("First step in the workflow")
                .operationId("openapi-endpoint");
        String worklowId = "q1";

        assertEquals(0, validator.validateStep(step, worklowId).size());
    }

    @Test
    void validateStepMissingStepId() {
        Step step = new Step()
                .stepId(null)
                .description("First step in the workflow")
                .operationId("openapi-endpoint");
        String worklowId = "q1";

        assertEquals(1, validator.validateStep(step, worklowId).size());
    }

    @Test
    void validateStepMissingEntity() {
        Step step = new Step()
                .stepId("step-one")
                .description("First step in the workflow")
                .operationId(null)
                .workflowId(null)
                .operationRef(null);
        String worklowId = "q1";

        assertEquals(1, validator.validateStep(step, worklowId).size());
    }

    @Test
    void validateStepDependsOn() {
        final String WORKFLOW_ID = "q1";

        List<Workflow> workflows = List.of(
                new Workflow()
                        .workflowId(WORKFLOW_ID)
                        .addStep(new Step()
                                .stepId("step-one"))
                        .addStep(new Step()
                                .stepId("step-two"))
        );

        validator.loadStepIds(workflows);

        Step step = new Step()
                .stepId("step-two")
                .description("Second step in the workflow")
                .operationId("openapi-endpoint")
                .dependsOn("step-one");

        assertEquals(0, validator.validateStep(step, WORKFLOW_ID).size());
    }

    @Test
    void validateStepDependsOnMissingStep() {
        final String WORKFLOW_ID = "q1";

        List<Workflow> workflows = List.of(
                new Workflow()
                        .workflowId(WORKFLOW_ID)
                        .addStep(new Step()
                                .stepId("step-one"))
                        .addStep(new Step()
                                .stepId("step-two"))
        );

        validator.loadStepIds(workflows);

        Step step = new Step()
                .stepId("step-two")
                .description("Second step in the workflow")
                .operationId("openapi-endpoint")
                .dependsOn("step-three");

        assertEquals(1, validator.validateStep(step, WORKFLOW_ID).size());
    }

    @Test
    void validateStepDependsOnSelf() {
        final String WORKFLOW_ID = "q1";

        List<Workflow> workflows = List.of(
                new Workflow()
                        .workflowId(WORKFLOW_ID)
                        .addStep(new Step()
                                .stepId("step-one"))
                        .addStep(new Step()
                                .stepId("step-two"))
        );

        validator.loadStepIds(workflows);

        Step step = new Step()
                .stepId("step-one")
                .description("Second step in the workflow")
                .operationId("openapi-endpoint")
                .dependsOn("step-one");

        assertEquals(1, validator.validateStep(step, WORKFLOW_ID).size());
    }

    @Test
    void validateStepWithoutInAttribute() {
        Step step = new Step()
                .stepId("step-one")
                .description("First step in the workflow")
                .workflowId("workflow-id-2");
        step.addParameter(new Parameter()
                .name("param")
                .value("value"));

        String worklowId = "q1";

        assertEquals(1, validator.validateStep(step, worklowId).size());
    }


    @Test
    void validateParameter() {
        Parameter parameter = new Parameter()
                .name("param")
                .value("1")
                .in("path");
        String worklowId = "q1";

        assertEquals(0, validator.validateParameter(parameter, worklowId).size());
    }

    @Test
    void validateParameterInvalidIn() {
        Parameter parameter = new Parameter()
                .name("param")
                .value("1")
                .in("dummy");
        String worklowId = "q1";

        assertEquals(1, validator.validateParameter(parameter, worklowId).size());
    }

    @Test
    void validateParameterWithoutValue() {
        Parameter parameter = new Parameter()
                .name("param")
                .value(null)
                .in("query");
        String worklowId = "q1";

        assertEquals(1, validator.validateParameter(parameter, worklowId).size());
    }

    @Test
    void validateSuccessAction() {
        String stepId = "step-one";
        SuccessAction successAction = new SuccessAction()
                .type("end")
                .stepId("step-one");

        successAction.addCriteria(
                new Criterion()
                        .context("$statusCode == 200"));

        assertEquals(0, validator.validateSuccessAction(successAction, stepId).size());
    }

    @Test
    void validateSuccessActionInvalidType() {
        String stepId = "step-one";
        SuccessAction successAction = new SuccessAction()
                .type("invalid-type")
                .stepId("step-one");

        successAction.addCriteria(
                new Criterion()
                        .context("$statusCode == 200"));

        assertEquals(1, validator.validateSuccessAction(successAction, stepId).size());
    }

    @Test
    void validateSuccessActionMissingEntity() {
        String stepId = "step-one";
        SuccessAction successAction = new SuccessAction()
                .type("end")
                .stepId(null)
                .workflowId(null);

        successAction.addCriteria(
                new Criterion()
                        .context("$statusCode == 200"));

        assertEquals(1, validator.validateSuccessAction(successAction, stepId).size());
    }

    @Test
    void validateSuccessActionInvalidEntity() {
        String stepId = "step-one";
        SuccessAction successAction = new SuccessAction()
                .type("end")
                .stepId("step-one")
                .workflowId("workflow-id");

        successAction.addCriteria(
                new Criterion()
                        .condition("$statusCode == 200"));

        assertEquals(1, validator.validateSuccessAction(successAction, stepId).size());
    }

    @Test
    void validateCriterion() {
        String stepId = "step-one";

        Criterion criterion = new Criterion()
                .condition("$statusCode == 200")
                .type("simple")
                .context("$response.body");

        assertEquals(0, validator.validateCriterion(criterion, stepId).size());
    }

    @Test
    void validateCriterionWithoutType() {
        String stepId = "step-one";

        Criterion criterion = new Criterion()
                .condition("$statusCode == 200");

        assertEquals(0, validator.validateCriterion(criterion, stepId).size());
    }
    @Test
    void validateCriterionInvalidType() {
        String stepId = "step-one";

        Criterion criterion = new Criterion()
                .condition("$statusCode == 200")
                .type("dummy")
                .context("$response.body");

        assertEquals(1, validator.validateCriterion(criterion, stepId).size());
    }

    @Test
    void validateCriterionMissingContext() {
        String stepId = "step-one";

        Criterion criterion = new Criterion()
                .condition("$statusCode == 200")
                .type("simple")
                .context(null);

        assertEquals(1, validator.validateCriterion(criterion, stepId).size());
    }

    @Test
    void validateFailureAction() {
        String stepId = "step-one";
        FailureAction failureAction = new FailureAction()
                .type("retry")
                .stepId("step-one")
                .retryAfter(1000L)
                .retryLimit(3);

        failureAction.addCriteria(
                new Criterion()
                        .context("$statusCode == 200"));

        assertEquals(0, validator.validateFailureAction(failureAction, stepId).size());
    }

    @Test
    void validateFailureActionInvalidType() {
        String stepId = "step-one";
        FailureAction failureAction = new FailureAction()
                .type("dummy")
                .stepId("step-one")
                .retryAfter(1000L)
                .retryLimit(3);

        failureAction.addCriteria(
                new Criterion()
                        .context("$statusCode == 200"));

        assertEquals(1, validator.validateFailureAction(failureAction, stepId).size());
    }

    @Test
    void validateFailureActionInvalidRetrySettings() {
        String stepId = "step-one";
        FailureAction failureAction = new FailureAction()
                .type("retry")
                .stepId("step-one")
                .retryAfter(-1000L)
                .retryLimit(-3);

        failureAction.addCriteria(
                new Criterion()
                        .context("$statusCode == 200"));

        assertEquals(2, validator.validateFailureAction(failureAction, stepId).size());
    }

    @Test
    void validateFailureActionMissingEntity() {
        String stepId = "step-one";
        FailureAction failureAction = new FailureAction()
                .type("retry")
                .stepId(null)
                .workflowId(null)
                .retryAfter(1000L)
                .retryLimit(3);

        failureAction.addCriteria(
                new Criterion()
                        .context("$statusCode == 200"));

        assertEquals(1, validator.validateFailureAction(failureAction, stepId).size());
    }

    @Test
    void validateFailureActionInvalidEntity() {
        String stepId = "step-one";
        FailureAction failureAction = new FailureAction()
                .type("retry")
                .stepId("step-one")
                .workflowId("workflow-test")
                .retryAfter(1000L)
                .retryLimit(3);

        failureAction.addCriteria(
                new Criterion()
                        .context("$statusCode == 200"));

        assertEquals(1, validator.validateFailureAction(failureAction, stepId).size());
    }

    @Test
    void loadWorkflowIWithDuplicateIds() {
        List<Workflow> list = List.of(
                new Workflow()
                        .workflowId("one"),
                new Workflow()
                        .workflowId("one"));

        assertEquals(1, validator.loadWorkflowIds(list).size());
    }

    @Test
    void validateComponentsParameterInvalidIn() {
        Parameter parameter = new Parameter()
                .name("param")
                .value("1")
                .in("dummy");
        String worklowId = "q1";

        Components components = new Components();
        components.addParameter("param1", parameter);

        assertEquals(1, validator.validateParameter(parameter, worklowId).size());
    }

    @Test
    void loadStepsWithDuplicateIds() {
        List<Workflow> list = List.of(
                new Workflow()
                        .workflowId("one")
                        .addStep(new Step()
                                .stepId("step-ABC"))
                        .addStep(new Step()
                                .stepId("step-ABC"))
        );

        assertEquals(1, validator.loadStepIds(list).size());
    }


    @Test
    void validWorkflowId() {
        assertTrue(new OpenAPIWorkflowValidator().isValidWorkflowId("idOfTheWorkflow_1"));
    }

    @Test
    void invalidWorkflowId() {
        assertFalse(new OpenAPIWorkflowValidator().isValidWorkflowId("workflow id"));
    }

    @Test
    void validOutputsKey() {
        assertTrue(new OpenAPIWorkflowValidator().isValidOutputsKey("tokenExpires"));
    }

    @Test
    void invalidOutputsKey() {
        assertFalse(new OpenAPIWorkflowValidator().isValidOutputsKey("$tokenExpires"));
    }

    @Test
    void invalidOutputsKeyWithSpace() {
        assertFalse(new OpenAPIWorkflowValidator().isValidOutputsKey("$token Expires"));
    }

    @Test
    void validComponentKey() {
        assertTrue(new OpenAPIWorkflowValidator().isValidComponentKey("pagination"));
    }

    @Test
    void invalidComponentKey() {
        assertFalse(new OpenAPIWorkflowValidator().isValidComponentKey("pagination order"));
    }


    @Test
    void isValidJsonPointer() {
        assertTrue(new OpenAPIWorkflowValidator().isValidJsonPointer("/user/id"));
    }

    @Test
    void invalidJsonPointer() {
        assertFalse(new OpenAPIWorkflowValidator().isValidJsonPointer("user/id"));
    }

//    @Test
//    void isValidJsonPointer2() {
//        assertTrue(new OpenAPIWorkflowValidator().isValidJsonPointer("#/petId"));
//    }

}