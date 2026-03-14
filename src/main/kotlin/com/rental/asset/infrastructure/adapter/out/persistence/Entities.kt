package com.rental.asset.infrastructure.adapter.out.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "category")
data class CategoryEntity(
    @Id val id: UUID,
    @Column(nullable = false) val name: String,
    @Column(columnDefinition = "TEXT") val description: String?
)

@Entity
@Table(name = "asset")
data class AssetEntity(
    @Id val id: UUID,
    @Column(nullable = false) val name: String,
    @Column(columnDefinition = "TEXT") val description: String?,
    @Column(name = "category_id", nullable = false) val categoryId: UUID,
    
    @Column(name = "daily_rate", precision = 10, scale = 2, nullable = false)
    val dailyRate: BigDecimal,
    
    @Column(name = "hourly_rate", precision = 10, scale = 2, nullable = false)
    val hourlyRate: BigDecimal,
    
    @Column(name = "deposit_amount", precision = 10, scale = 2, nullable = false)
    val depositAmount: BigDecimal
)

@Entity
@Table(name = "inventory_item")
data class InventoryItemEntity(
    @Id val id: UUID,
    @Column(name = "asset_id", nullable = false) val assetId: UUID,
    @Column(name = "serial_number", nullable = false) val serialNumber: String,
    @Column(name = "qr_code_token", nullable = false, unique = true) val qrCodeToken: String,
    @Column(nullable = false) val status: String,
    @Column(name = "photo_url", columnDefinition = "TEXT") val photoUrl: String?,
    @Column(name = "last_maintenance_date") val lastMaintenanceDate: LocalDate?,
    @Column(name = "next_maintenance_date") val nextMaintenanceDate: LocalDate?
)
