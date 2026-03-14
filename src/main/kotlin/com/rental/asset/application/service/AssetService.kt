package com.rental.asset.application.service

import com.rental.asset.application.port.`in`.AssetUseCase
import com.rental.asset.application.port.`in`.CreateAssetCommand
import com.rental.asset.application.port.`in`.CreateInventoryItemCommand
import com.rental.asset.application.port.`in`.InventoryItemUseCase
import com.rental.asset.application.port.out.FileStoragePort
import com.rental.asset.application.port.out.QrCodeGeneratorPort
import com.rental.asset.domain.exception.AssetNotFoundException
import com.rental.asset.domain.exception.InventoryItemNotFoundException
import com.rental.asset.domain.model.Asset
import com.rental.asset.domain.model.InventoryItem
import com.rental.asset.domain.model.ItemStatus
import com.rental.asset.domain.repository.AssetRepository
import com.rental.asset.domain.repository.InventoryItemRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class AssetService(
    private val assetRepository: AssetRepository,
    private val inventoryItemRepository: InventoryItemRepository,
    private val qrCodeGeneratorPort: QrCodeGeneratorPort,
    private val fileStoragePort: FileStoragePort
) : AssetUseCase, InventoryItemUseCase {

    override fun createAsset(command: CreateAssetCommand): Asset {
        val asset = Asset(
            name = command.name,
            description = command.description,
            categoryId = command.categoryId,
            dailyRate = command.dailyRate,
            hourlyRate = command.hourlyRate,
            depositAmount = command.depositAmount
        )
        return assetRepository.save(asset)
    }

    @Transactional(readOnly = true)
    override fun getAssets(): List<Asset> {
        return assetRepository.findAll()
    }

    override fun createInventoryItem(command: CreateInventoryItemCommand): InventoryItem {
        assetRepository.findById(command.assetId) ?: throw AssetNotFoundException(command.assetId.toString())

        val token = command.qrCodeToken ?: qrCodeGeneratorPort.generateToken()
        val item = InventoryItem(
            assetId = command.assetId,
            serialNumber = command.serialNumber,
            qrCodeToken = token,
            status = ItemStatus.AVAILABLE,
            lastMaintenanceDate = command.lastMaintenanceDate,
            nextMaintenanceDate = command.nextMaintenanceDate
        )
        return inventoryItemRepository.save(item)
    }

    @Transactional(readOnly = true)
    override fun getInventoryItem(id: UUID): InventoryItem {
        return inventoryItemRepository.findById(id) ?: throw InventoryItemNotFoundException(id.toString())
    }

    override fun changeItemStatus(id: UUID, newStatus: ItemStatus) {
        val item = inventoryItemRepository.findById(id) ?: throw InventoryItemNotFoundException(id.toString())
        item.changeStatus(newStatus)
        inventoryItemRepository.save(item)
    }

    override fun uploadItemPhoto(id: UUID, originalFileName: String, content: ByteArray, contentType: String): InventoryItem {
        val item = inventoryItemRepository.findById(id) ?: throw InventoryItemNotFoundException(id.toString())

        // UUID-prefix to avoid overwriting files across items
        val extension = originalFileName.substringAfterLast('.', "jpg")
        val uniquePath = "items/${item.id}/${UUID.randomUUID()}-${originalFileName.substringBeforeLast('.')}.$extension"

        val photoUrl = fileStoragePort.uploadFile(uniquePath, content, contentType)

        val updatedItem = item.copy(photoUrl = photoUrl)
        return inventoryItemRepository.save(updatedItem)
    }
}
