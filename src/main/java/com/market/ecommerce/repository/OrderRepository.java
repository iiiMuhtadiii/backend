package com.market.ecommerce.repository;

import com.market.ecommerce.entity.Order;
import com.market.ecommerce.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // دالتك الأصلية التي تدعم الـ Pagination (ممتازة للـ Frontend)
    Page<Order> findByUserId(Long userId, Pageable pageable);

    // الدالة التي أضفناها لدعم OrderService
    List<Order> findByUserId(Long userId);

    // الدالة التي أضفناها لدعم DashboardService و CustomerSegmentationService
    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Long id);
}
