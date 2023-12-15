package com.aau.p3.performancedashboard.converter;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.aau.p3.performancedashboard.model.Authority;
import com.aau.p3.performancedashboard.model.User;
import com.aau.p3.performancedashboard.payload.response.UserResponse;

@Component
public class UserConverter {
    
    /**
        * Converts a User object to a UserResponse object.
        *
        * @param user the User object to be converted
        * @return the converted UserResponse object
        */
    public UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setLogin(user.getLogin());
        response.setDisplayName(user.getDisplayName());
        response.setEmail(user.getEmail());
        response.setAchievements(user.getAchievements());
        response.setAuthorities(user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList()));
        return response;
    }
}
