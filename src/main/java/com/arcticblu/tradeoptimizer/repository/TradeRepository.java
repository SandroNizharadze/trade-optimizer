package com.arcticblu.tradeoptimizer.repository;

import com.arcticblu.tradeoptimizer.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TradeRepository extends JpaRepository<TradeEntity, UUID> {

    List<TradeEntity> findBySelectedFalseAndMarginRequiredLessThanEqual(
            BigDecimal remainingMargin
    );
}