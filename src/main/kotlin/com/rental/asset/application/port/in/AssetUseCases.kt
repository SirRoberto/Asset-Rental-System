package com.rental.asset.application.port.`in`

import com.rental.asset.domain.model.Asset
import com.rental.asset.domain.model.InventoryItem
import com.rental.asset.domain.model.ItemStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateAssetCommand(
    val name: String, 
    val description: String?, 
    val categoryId: UUID,
    val dailyRate: BigDecimal, 
    val hourlyRate: BigDecimal, 
    val depositAmount: BigDecimal
)

data class CreateInventoryItemCommand(
    val assetId: UUID, 
    val serialNumber: String, 
    val qrCodeToken: String?,
    val lastMaintenanceDate: LocalDate?, 
    val nextMaintenanceDate: LocalDate?
)

interface AssetUseCase {
    fun createAsset(command: CreateAssetCommand): Asset
    fun getAssets(): List<Asset>
}

interface InventoryItemUseCase {
    fun createInventoryItem(command: CreateInventoryItemCommand): InventoryItem
    fun getInventoryItem(id: UUID): InventoryItem
    fun changeItemStatus(id: UUID, newStatus: ItemStatus)
}
