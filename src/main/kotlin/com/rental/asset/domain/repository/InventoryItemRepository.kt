package com.rental.asset.domain.repository

import com.rental.asset.domain.model.InventoryItem
import java.util.UUID

interface InventoryItemRepository {
    fun save(item: InventoryItem): InventoryItem
    fun findById(id: UUID): InventoryItem?
    fun findByAssetId(assetId: UUID): List<InventoryItem>
    fun findByQrCodeToken(token: String): InventoryItem?
    fun deleteById(id: UUID)
}
