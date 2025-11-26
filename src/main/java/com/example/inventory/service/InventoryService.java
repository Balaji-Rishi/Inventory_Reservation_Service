package com.example.inventory.service;

import com.example.inventory.dto.ConfirmReservationResponse;
import com.example.inventory.dto.CreateReservationRequest;
import com.example.inventory.dto.ReservationResponse;
import com.example.inventory.model.InventoryReservation;
import com.example.inventory.model.Product;
import com.example.inventory.model.ReservationStatus;
import com.example.inventory.repository.InventoryReservationRepository;
import com.example.inventory.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final ProductRepository productRepository;
    private final InventoryReservationRepository reservationRepository;

    @Value("${reservation.expiry.minutes:5}")
    private int expiryMinutes;

    // Create reservation API -> PENDING
    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        Product product = productRepository.findBySku(request.getSku())
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        // Check stock, but DO NOT decrease yet
        if (product.getAvailableStock() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        Instant now = Instant.now();
        InventoryReservation reservation = InventoryReservation.builder()
                .product(product)
                .quantity(request.getQuantity())
                .status(ReservationStatus.PENDING)
                .createdAt(now)
                .lastUpdatedAt(now)
                .expiresAt(now.plus(expiryMinutes, ChronoUnit.MINUTES))
                .build();

        reservationRepository.save(reservation);

        log.info("Created reservation id={} for sku={} qty={} status={}",
                reservation.getId(), product.getSku(), reservation.getQuantity(), reservation.getStatus());

        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .sku(product.getSku())
                .quantity(reservation.getQuantity())
                .status(reservation.getStatus())
                .build();
    }

    // Confirm reservation -> decrease stock now
    @Transactional
    public ConfirmReservationResponse confirmReservation(Long reservationId) {
        InventoryReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only PENDING reservations can be confirmed");
        }

        // if already expired
        if (reservation.getExpiresAt().isBefore(Instant.now())) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservation.setLastUpdatedAt(Instant.now());
            log.info("Reservation {} already expired when confirming", reservationId);
            return ConfirmReservationResponse.builder()
                    .reservationId(reservationId)
                    .status(ReservationStatus.EXPIRED)
                    .remainingStock(reservation.getProduct().getAvailableStock())
                    .build();
        }

        Product product = reservation.getProduct();

        // optimistic locking works here through @Version
        if (product.getAvailableStock() < reservation.getQuantity()) {
            throw new IllegalStateException("Insufficient stock at confirmation time");
        }

        product.setAvailableStock(product.getAvailableStock() - reservation.getQuantity());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setLastUpdatedAt(Instant.now());

        try {
            productRepository.save(product);
            reservationRepository.save(reservation);
        } catch (OptimisticLockException e) {
            // concurrent update -> oversell prevention
            throw new IllegalStateException("Concurrent modification detected, please retry");
        }

        log.info("Confirmed reservation id={} for sku={} qty={}. Remaining stock={}",
                reservationId, product.getSku(), reservation.getQuantity(), product.getAvailableStock());

        return ConfirmReservationResponse.builder()
                .reservationId(reservationId)
                .status(reservation.getStatus())
                .remainingStock(product.getAvailableStock())
                .build();
    }

    // Scheduler uses this to expire old reservations & restore stock
    @Transactional
    public void expireOldReservations() {
        Instant now = Instant.now();
        var toExpire = reservationRepository
                .findByStatusAndExpiresAtBefore(ReservationStatus.PENDING, now);

        for (InventoryReservation r : toExpire) {
            Product product = r.getProduct();
            // restore stock only for PENDING that are being expired
            product.setAvailableStock(product.getAvailableStock() + r.getQuantity());
            r.setStatus(ReservationStatus.EXPIRED);
            r.setLastUpdatedAt(now);

            productRepository.save(product);
            reservationRepository.save(r);

            log.info("Expired reservation id={} for sku={} qty={}. Stock restored.",
                    r.getId(), product.getSku(), r.getQuantity());
        }
    }

    public Long getAvailableStock(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new NoSuchElementException("Product not found"));
        return product.getAvailableStock();
    }

    @Transactional
    public Product createOrUpdateProduct(Product product) {
        return productRepository.save(product);
    }
}
