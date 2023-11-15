package com.aau.p3.performancedashboard.dto;

import com.aau.p3.performancedashboard.model.Integration;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationMapperTest {

    @Test
    void toDTOConvertsAIntegrationToAnIntegrationDTO() {
        var input = new Integration("Sales", "Internal");

        var output = IntegrationMapper.toDTO(input);

        assertEquals("Sales", output.getName());
        assertEquals("Internal", output.getType());
    }

    @Test
    void toInternalIntegrationConvertsAnIntegrationDTOToInternalIntegration() {
        var input = new IntegrationDTO("Sales", "Internal");

        var output = IntegrationMapper.toInternalIntegration(input);

        assertEquals("Sales", output.getName());
        assertEquals("Internal", output.getType());
    }

    @Test
    void toInternalIntegrationReturnsEqualObjectIfConvertedAndBack() {
        var input = new IntegrationDTO("Sales", "Internal");

        var internal = IntegrationMapper.toInternalIntegration(input);
        var output = IntegrationMapper.toDTO(internal);

        assertTrue(new ReflectionEquals(input).matches(output));
    }
}