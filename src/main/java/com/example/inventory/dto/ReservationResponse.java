package com.example.inventory.dto;

import com.example.inventory.model.ReservationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponse {

    private Long reservationId;
    private String sku;
    private Long quantity;
    private ReservationStatus status;
}
