package com.erasm.core.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request body for reallocating employee allocation percentage")
public class ReallocationRequest {

    @Schema(description = "New allocation percentage (1 to 100)", example = "75")
    @NotNull(message = "Allocation percentage is required")
    @Min(value = 1, message = "Allocation percentage must be greater than 0")
    @Max(value = 100, message = "Allocation percentage cannot exceed 100")
    private Double allocationPercentage;

    public ReallocationRequest() {
    }

    public ReallocationRequest(Double allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }

    public Double getAllocationPercentage() {
        return allocationPercentage;
    }

    public void setAllocationPercentage(Double allocationPercentage) {
        this.allocationPercentage = allocationPercentage;
    }

    public Double getPercentage() {
        return allocationPercentage;
    }

    public void setPercentage(Double percentage) {
        this.allocationPercentage = percentage;
    }
}
