package com.restaurador.api.dto;

import com.restaurador.api.domain.order.ImageOrder;
import com.restaurador.api.domain.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderResponse {
    private UUID id;
    private String originalFilename;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private String userEmail;

    public OrderResponse(ImageOrder order) {
        this.id = order.getId();
        this.originalFilename = order.getOriginalFilename();
        this.status = order.getStatus();
        this.createdAt = order.getId() != null ? LocalDateTime.now() : null; // Idealmente mapeado se tivesse getters para os campos audit
        this.userEmail = order.getUser().getEmail();
    }

    public UUID getId() { return id; }
    public String getOriginalFilename() { return originalFilename; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getUserEmail() { return userEmail; }
}
