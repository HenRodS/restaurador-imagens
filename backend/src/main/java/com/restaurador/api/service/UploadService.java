package com.restaurador.api.service;

import com.restaurador.api.domain.order.ImageOrder;
import com.restaurador.api.domain.order.ImageOrderRepository;
import com.restaurador.api.domain.order.OrderStatus;
import com.restaurador.api.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploadService {

    private final S3StorageService s3StorageService;
    private final ImageOrderRepository imageOrderRepository;

    public UploadService(S3StorageService s3StorageService, ImageOrderRepository imageOrderRepository) {
        this.s3StorageService = s3StorageService;
        this.imageOrderRepository = imageOrderRepository;
    }

    public ImageOrder processUpload(MultipartFile file, User user) throws IOException {
        validateFile(file);

        String storagePath = s3StorageService.uploadFile(file, user.getId());

        ImageOrder order = new ImageOrder(
                user,
                file.getOriginalFilename(),
                storagePath,
                OrderStatus.PENDING_ANALYSIS
        );

        return imageOrderRepository.save(order);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new IllegalArgumentException("Apenas imagens JPEG ou PNG são permitidas");
        }

        // A validação de tamanho de arquivo (ex: 20MB) já é feita via property no application.yml do Spring.
    }
}
