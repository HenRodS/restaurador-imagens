package com.restaurador.api.controller;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.restaurador.api.domain.order.ImageOrder;
import com.restaurador.api.domain.order.ImageOrderRepository;
import com.restaurador.api.domain.order.OrderStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final ImageOrderRepository repository;
    private final NotificationProducer notificationProducer;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public WebhookController(ImageOrderRepository repository, NotificationProducer notificationProducer) {
        this.repository = repository;
        this.notificationProducer = notificationProducer;
    }

    @PostMapping("/payments")
    public ResponseEntity<String> handleMercadoPagoWebhook(@RequestBody Map<String, Object> payload) {
        try {
            // Em produção, deve-se validar a assinatura (x-signature) recebida no header
            
            if ("payment".equals(payload.get("type")) && payload.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                String paymentIdStr = (String) data.get("id");
                
                if (paymentIdStr != null) {
                    Long paymentId = Long.valueOf(paymentIdStr);
                    MercadoPagoConfig.setAccessToken(accessToken);
                    PaymentClient client = new PaymentClient();
                    Payment payment = client.get(paymentId);
                    
                    if ("approved".equals(payment.getStatus())) {
                        String orderIdStr = payment.getExternalReference();
                        if (orderIdStr != null) {
                            UUID orderId = UUID.fromString(orderIdStr);
                            Optional<ImageOrder> optionalOrder = repository.findById(orderId);
                            if (optionalOrder.isPresent()) {
                                ImageOrder order = optionalOrder.get();
                                order.setStatus(OrderStatus.PAID);
                                repository.save(order);

                                String subject = "Pagamento Confirmado - Imagem Liberada";
                                String body = "Obrigado! O pagamento para a foto " + order.getOriginalFilename() + " foi confirmado.\n" +
                                              "Acesse o painel para realizar o download da imagem em alta resolução.";
                                notificationProducer.sendEmailNotification(order.getUser().getEmail(), subject, body);
                            }
                        }
                    }
                }
            }
            return ResponseEntity.ok("Webhook recebido com sucesso");
        } catch (MPException | MPApiException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao processar o webhook");
        }
    }
}
