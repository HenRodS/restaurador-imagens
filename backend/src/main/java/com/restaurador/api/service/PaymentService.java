package com.restaurador.api.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.restaurador.api.domain.order.ImageOrder;
import com.restaurador.api.domain.order.ImageOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

@Service
public class PaymentService {

    private final ImageOrderRepository repository;

    @Value("${mercadopago.access-token}")
    private String accessToken;

    public PaymentService(ImageOrderRepository repository) {
        this.repository = repository;
    }

    public String createCheckoutSession(UUID orderId) throws MPException, MPApiException {
        ImageOrder order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (order.getPrice() == null || order.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("O pedido não tem um preço configurado para pagamento.");
        }

        MercadoPagoConfig.setAccessToken(accessToken);

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .title("Restauração de Imagem: " + order.getOriginalFilename())
                .quantity(1)
                .unitPrice(order.getPrice())
                .currencyId("BRL")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(Collections.singletonList(itemRequest))
                .externalReference(order.getId().toString())
                .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        order.setExternalPaymentId(preference.getId());
        repository.save(order);

        return preference.getInitPoint();
    }
}
