package com.example.inventory.dto;

import com.example.inventory.model.ReservationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmReservationResponse {

    private Long reservationId;
    private ReservationStatus status;
    private Long remainingStock;
}
