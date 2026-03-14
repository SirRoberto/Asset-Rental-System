package com.rental.asset.infrastructure.adapter.`in`.web

import com.rental.asset.application.port.`in`.AssetUseCase
import com.rental.asset.application.port.`in`.CreateAssetCommand
import com.rental.asset.application.port.`in`.CreateInventoryItemCommand
import com.rental.asset.application.port.`in`.InventoryItemUseCase
import com.rental.asset.domain.model.Asset
import com.rental.asset.domain.model.InventoryItem
import com.rental.asset.domain.model.ItemStatus
import com.rental.asset.infrastructure.adapter.`in`.web.dto.ChangeItemStatusRequest
import com.rental.asset.infrastructure.adapter.`in`.web.dto.CreateAssetRequest
import com.rental.asset.infrastructure.adapter.`in`.web.dto.CreateInventoryItemRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1")
class AssetController(
    private val assetUseCase: AssetUseCase,
    private val inventoryItemUseCase: InventoryItemUseCase
) {

    @PostMapping("/assets")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAsset(@RequestBody request: CreateAssetRequest): Asset {
        val command = CreateAssetCommand(
            request.name, request.description, request.categoryId,
            request.dailyRate, request.hourlyRate, request.depositAmount
        )
        return assetUseCase.createAsset(command)
    }

    @GetMapping("/assets")
    fun getAssets(): List<Asset> {
        return assetUseCase.getAssets()
    }

    @PostMapping("/inventory-items")
    @ResponseStatus(HttpStatus.CREATED)
    fun createInventoryItem(@RequestBody request: CreateInventoryItemRequest): InventoryItem {
        val command = CreateInventoryItemCommand(
            request.assetId, request.serialNumber, request.qrCodeToken,
            request.lastMaintenanceDate, request.nextMaintenanceDate
        )
        return inventoryItemUseCase.createInventoryItem(command)
    }

    @GetMapping("/inventory-items/{id}")
    fun getInventoryItem(@PathVariable id: UUID): InventoryItem {
        return inventoryItemUseCase.getInventoryItem(id)
    }

    @PatchMapping("/inventory-items/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changeStatus(@PathVariable id: UUID, @RequestBody request: ChangeItemStatusRequest) {
        val newStatus = ItemStatus.valueOf(request.status)
        inventoryItemUseCase.changeItemStatus(id, newStatus)
    }
}
