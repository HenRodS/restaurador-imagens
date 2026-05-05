package com.restaurador.api.service;

import com.restaurador.api.domain.order.ImageOrder;
import com.restaurador.api.domain.order.ImageOrderRepository;
import com.restaurador.api.domain.order.OrderStatus;
import com.restaurador.api.domain.user.Role;
import com.restaurador.api.domain.user.User;
import com.restaurador.api.dto.OrderResponse;
import com.restaurador.api.dto.UpdateOrderStatusRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final ImageOrderRepository repository;
    private final NotificationProducer notificationProducer;

    public OrderService(ImageOrderRepository repository, NotificationProducer notificationProducer) {
        this.repository = repository;
        this.notificationProducer = notificationProducer;
    }

    public List<OrderResponse> getOrdersForUser(User user) {
        List<ImageOrder> orders;
        if (user.getRole() == Role.ROLE_ADMIN) {
            orders = repository.findAll();
        } else {
            orders = repository.findByUserId(user.getId());
        }
        
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request, User user) {
        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new SecurityException("Apenas administradores podem alterar o status do pedido.");
        }

        ImageOrder order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        order.setStatus(request.getStatus());
        ImageOrder updatedOrder = repository.save(order);
        
        if (request.getStatus() == OrderStatus.AWAITING_PAYMENT) {
            String subject = "Sua foto restaurada está pronta!";
            String body = "Sua foto " + order.getOriginalFilename() + " já foi restaurada.\n" +
                          "Acesse o painel para realizar o pagamento e liberar o download.";
            notificationProducer.sendEmailNotification(order.getUser().getEmail(), subject, body);
        }

        return new OrderResponse(updatedOrder);
    }
}
