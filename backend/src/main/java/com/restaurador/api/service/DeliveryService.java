package com.restaurador.api.service;

import com.restaurador.api.domain.order.ImageOrder;
import com.restaurador.api.domain.order.ImageOrderRepository;
import com.restaurador.api.domain.order.OrderStatus;
import com.restaurador.api.domain.user.Role;
import com.restaurador.api.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class DeliveryService {

    private final ImageOrderRepository repository;
    private final S3StorageService s3StorageService;

    public DeliveryService(ImageOrderRepository repository, S3StorageService s3StorageService) {
        this.repository = repository;
        this.s3StorageService = s3StorageService;
    }

    public ImageOrder uploadRestoredImage(UUID orderId, MultipartFile file, User user) throws IOException {
        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new SecurityException("Apenas administradores podem fazer upload da versão restaurada.");
        }

        ImageOrder order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        String restoredPath = s3StorageService.uploadRestoredFile(file, orderId);
        order.setRestoredStoragePath(restoredPath);
        
        // Pode avançar o status automaticamente se desejar, mas manteremos separado.
        
        return repository.save(order);
    }

    public String generateDownloadUrlForRestored(UUID orderId, User user) {
        ImageOrder order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        if (user.getRole() != Role.ROLE_ADMIN && !order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Você não tem permissão para acessar este pedido.");
        }

        if (user.getRole() != Role.ROLE_ADMIN && order.getStatus() != OrderStatus.PAID) {
            throw new SecurityException("O download só é permitido após a confirmação do pagamento.");
        }

        if (order.getRestoredStoragePath() == null) {
            throw new IllegalStateException("A imagem restaurada ainda não foi disponibilizada.");
        }

        return s3StorageService.generatePresignedUrl(order.getRestoredStoragePath(), Duration.ofMinutes(15));
    }

    public String generateDownloadUrlForOriginal(UUID orderId, User user) {
        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new SecurityException("Apenas administradores podem baixar a imagem original.");
        }

        ImageOrder order = repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        return s3StorageService.generatePresignedUrl(order.getStoragePath(), Duration.ofMinutes(60));
    }
}
