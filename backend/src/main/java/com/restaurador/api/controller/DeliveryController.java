package com.restaurador.api.controller;

import com.restaurador.api.domain.order.ImageOrder;
import com.restaurador.api.domain.user.User;
import com.restaurador.api.service.DeliveryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/{orderId}/upload")
    public ResponseEntity<?> uploadRestoredImage(
            @PathVariable UUID orderId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        try {
            ImageOrder order = deliveryService.uploadRestoredImage(orderId, file, user);
            return ResponseEntity.ok(order);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar arquivo restaurado.");
        }
    }

    @GetMapping("/{orderId}/download")
    public ResponseEntity<?> getDownloadUrl(@PathVariable UUID orderId, @AuthenticationPrincipal User user) {
        try {
            String url = deliveryService.generateDownloadUrlForRestored(orderId, user);
            return ResponseEntity.ok(Map.of("downloadUrl", url));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{orderId}/download-original")
    public ResponseEntity<?> getOriginalDownloadUrl(@PathVariable UUID orderId, @AuthenticationPrincipal User user) {
        try {
            String url = deliveryService.generateDownloadUrlForOriginal(orderId, user);
            return ResponseEntity.ok(Map.of("downloadUrl", url));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
