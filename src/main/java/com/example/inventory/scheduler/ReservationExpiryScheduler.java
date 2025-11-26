package com.example.inventory.scheduler;

import com.example.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationExpiryScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpiryScheduler.class);
    private final InventoryService inventoryService;

    // Run every 1 minute
    @Scheduled(fixedDelay = 60000)
    public void expireReservations() {
        log.info("Running reservation expiry scheduler...");
        inventoryService.expireOldReservations();
    }
}
