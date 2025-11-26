package com.example.inventory.controller;

import com.example.inventory.dto.ConfirmReservationResponse;
import com.example.inventory.dto.CreateReservationRequest;
import com.example.inventory.dto.ReservationResponse;
import com.example.inventory.model.Product;
import com.example.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // Create / update product (for testing)
    @PostMapping("/products")
    public ResponseEntity<Product> createOrUpdateProduct(@RequestBody Product product) {
        return ResponseEntity.ok(inventoryService.createOrUpdateProduct(product));
    }

    @GetMapping("/products/{sku}/stock")
    public ResponseEntity<Long> getStock(@PathVariable String sku) {
        return ResponseEntity.ok(inventoryService.getAvailableStock(sku));
    }

    // Create reservation -> PENDING
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {
        return ResponseEntity.ok(inventoryService.createReservation(request));
    }

    // Confirm reservation -> decrease stock
    @PostMapping("/reservations/{id}/confirm")
    public ResponseEntity<ConfirmReservationResponse> confirmReservation(
            @PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.confirmReservation(id));
    }
}
