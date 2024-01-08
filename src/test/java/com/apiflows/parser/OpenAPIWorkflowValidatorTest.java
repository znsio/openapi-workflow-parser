package com.apiflows.parser;

import com.apiflows.model.OpenAPIWorkflow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenAPIWorkflowValidatorTest {

    @Test
    void validate() {
        OpenAPIWorkflow openAPIWorkflow = new OpenAPIWorkflow();
        OpenAPIWorkflowValidatorResult result = new OpenAPIWorkflowValidator().validate(openAPIWorkflow);

        assertFalse(result.isValid());
        assertFalse(result.getErrors().isEmpty());
        assertEquals("'workflowsSpec' is undefined", result.getErrors().get(0));
    }
}