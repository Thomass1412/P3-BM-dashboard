package com.aau.p3.performancedashboard.converter;

import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.model.ApiKey;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.response.ApiKeyResponse;

@Component
public class ApiKeyConverter {

    /**
        * Converts an ApiKey object to an ApiKeyResponse object.
        * 
        * @param apiKey The ApiKey object to be converted.
        * @return The converted ApiKeyResponse object.
        */
    public ApiKeyResponse convertToResponse(ApiKey apiKey) {
        // Extract creator information
        User creator = apiKey.getCreator();
        String creatorLogin = (creator != null) ? creator.getLogin() : null;
        String creatorEmail = (creator != null) ? creator.getEmail() : null;

        // Create response
        ApiKeyResponse response = new ApiKeyResponse(apiKey.getCreationDate(), apiKey.getExpirationDate());
        response.setKey(apiKey.getKey());
        response.setName(apiKey.getName());
        response.setCreator(creatorLogin);
        response.setCreatorEmail(creatorEmail);
        return response;
    }
}
