package com.aau.p3.performancedashboard.dto;

import com.aau.p3.performancedashboard.model.*;

public class IntegrationMapper {

    //public static <T extends Integration> IntegrationDTO toDTO(T integration) {
       // IntegrationDTO integrationDTO = new IntegrationDTO(integration.getName(), integration.getType());
        //return integrationDTO;
    //}

    // https://stackoverflow.com/questions/450807/how-do-i-make-the-method-return-type-generic

    public static InternalIntegration toInternalIntegration(IntegrationDTO integration) {
        InternalIntegration internalIntegration = new InternalIntegration(integration.getName());
        return internalIntegration;
    }
}
