package com.rental.asset.infrastructure.adapter.`in`.web.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateAssetRequest(
    val name: String,
    val description: String?,
    val categoryId: UUID,
    val dailyRate: BigDecimal,
    val hourlyRate: BigDecimal,
    val depositAmount: BigDecimal
)

data class CreateInventoryItemRequest(
    val assetId: UUID,
    val serialNumber: String,
    val qrCodeToken: String?,
    val lastMaintenanceDate: LocalDate?,
    val nextMaintenanceDate: LocalDate?
)

data class ChangeItemStatusRequest(
    val status: String
)
