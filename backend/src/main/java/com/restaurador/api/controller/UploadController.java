package com.restaurador.api.controller;

import com.restaurador.api.domain.order.ImageOrder;
import com.restaurador.api.domain.user.User;
import com.restaurador.api.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        
        try {
            ImageOrder order = uploadService.processUpload(file, user);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao processar o arquivo: " + e.getMessage());
        }
    }
}
