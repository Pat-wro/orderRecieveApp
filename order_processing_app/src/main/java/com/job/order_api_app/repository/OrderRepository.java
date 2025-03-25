package com.job.order_api_app.repository;

import com.job.order_api_app.model.OrderRequest;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderRequest, UUID> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<OrderRequest> findByShipmentNumber(String shipmentNumber);
}