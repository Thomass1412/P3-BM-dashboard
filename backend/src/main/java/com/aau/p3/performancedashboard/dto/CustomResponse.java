package com.aau.p3.performancedashboard.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class CustomResponse {
    @Schema(name = "message", example = "Validation Error")
    @Getter
    @Setter
    private String message;
    
    @Schema(name = "message", example = "Error")
    @Getter
    @Setter
    private String status;

    @Schema(name = "message", example = "['error1']")
    @Getter
    @Setter
    private List<String> error;

    public CustomResponse(String message, String status, String error) {
        this.message = message;
        this.status = status;
        List<String> errorList = new ArrayList<>();
        errorList.add(error);
        this.error = errorList;
    }
}