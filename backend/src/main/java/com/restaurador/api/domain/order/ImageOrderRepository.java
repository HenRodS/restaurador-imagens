package com.restaurador.api.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ImageOrderRepository extends JpaRepository<ImageOrder, UUID> {
}
