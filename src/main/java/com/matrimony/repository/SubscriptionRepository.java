package com.matrimony.repository;

import com.matrimony.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Subscription> findByUserIdAndActiveTrue(Long userId);

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Subscription s")
    BigDecimal getTotalRevenue();

    long countByActiveTrue();
}
