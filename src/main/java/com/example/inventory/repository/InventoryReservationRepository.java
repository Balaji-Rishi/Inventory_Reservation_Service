package com.example.inventory.repository;

import com.example.inventory.model.InventoryReservation;
import com.example.inventory.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {

    List<InventoryReservation> findByStatusAndExpiresAtBefore(ReservationStatus status, Instant time);
}
