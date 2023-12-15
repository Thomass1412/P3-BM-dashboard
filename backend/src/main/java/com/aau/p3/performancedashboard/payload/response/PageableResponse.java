package com.aau.p3.performancedashboard.payload.response;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Represents a pageable response for managing {@link T} instances.
 * This class is used to encapsulate the data returned from paginated requests.
 * This class uses the {@link Schema} annotation from Swagger to provide a description for the model and its properties.
 */
@Getter
@Schema(description = "Pageable Response for managing instances of a specific type. Used to encapsulate the data returned from paginated requests.")
public class PageableResponse<T> {

    /**
     * The content of the current page.
     * This list contains the {@link T} instances for the current page.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     */
    @Schema(description = "List of content for the current page.", example = "[\"content1\", \"content2\"]")
    private List<T> content;

    /**
     * The total number of items.
     * This value represents the total number of {@link T} instances across all pages.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     */
    @Schema(description = "Total number of items across all pages.", example = "100")
    private long totalElements;

    /**
     * The total number of pages.
     * This value represents the total number of pages.
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     */
    @Schema(description = "Total number of pages.", example = "10")
    private int totalPages;

    /**
     * The current page number.
     * This value represents the current page number (0-indexed).
     * This property uses the {@link Schema} annotation from Swagger to provide a description.
     */
    @Schema(description = "Current page number (0-indexed).", example = "1")
    private int number;
}
