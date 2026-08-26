package com.arcticblu.tradeoptimizer.repository;

import com.arcticblu.tradeoptimizer.entity.OptimizationRunEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OptimizationRunRepository extends JpaRepository<OptimizationRunEntity, UUID> {

    Page<OptimizationRunEntity> findAllByOrderByCreatedAtDesc(
            Pageable pageable
    );
}
