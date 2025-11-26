package com.example.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateReservationRequest {

    @NotBlank
    private String sku;

    @Min(1)
    private Long quantity;
}
