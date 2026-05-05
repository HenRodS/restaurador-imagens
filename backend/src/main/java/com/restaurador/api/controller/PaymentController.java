package com.restaurador.api.controller;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.restaurador.api.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{orderId}/checkout")
    public ResponseEntity<?> createCheckout(@PathVariable UUID orderId) {
        try {
            String initPoint = paymentService.createCheckoutSession(orderId);
            return ResponseEntity.ok(Map.of("checkoutUrl", initPoint));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (MPException | MPApiException e) {
            return ResponseEntity.internalServerError().body("Erro na integração com Mercado Pago.");
        }
    }
}
