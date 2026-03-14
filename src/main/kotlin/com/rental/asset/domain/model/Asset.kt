package com.rental.asset.domain.model

import java.math.BigDecimal
import java.util.UUID

data class Asset(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null,
    val categoryId: UUID,
    val dailyRate: BigDecimal,
    val hourlyRate: BigDecimal,
    val depositAmount: BigDecimal
)
