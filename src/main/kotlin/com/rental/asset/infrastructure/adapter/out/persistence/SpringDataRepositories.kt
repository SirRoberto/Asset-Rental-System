package com.rental.asset.infrastructure.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SpringDataCategoryRepository : JpaRepository<CategoryEntity, UUID>

interface SpringDataAssetRepository : JpaRepository<AssetEntity, UUID>

interface SpringDataInventoryItemRepository : JpaRepository<InventoryItemEntity, UUID> {
    fun findByAssetId(assetId: UUID): List<InventoryItemEntity>
    fun findByQrCodeToken(qrCodeToken: String): InventoryItemEntity?
}
