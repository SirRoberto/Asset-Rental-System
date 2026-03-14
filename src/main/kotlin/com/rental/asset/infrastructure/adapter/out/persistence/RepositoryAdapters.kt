package com.rental.asset.infrastructure.adapter.out.persistence

import com.rental.asset.domain.model.Asset
import com.rental.asset.domain.model.Category
import com.rental.asset.domain.model.InventoryItem
import com.rental.asset.domain.model.ItemStatus
import com.rental.asset.domain.repository.AssetRepository
import com.rental.asset.domain.repository.CategoryRepository
import com.rental.asset.domain.repository.InventoryItemRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.util.UUID

// -- Mappers --
fun CategoryEntity.toDomain() = Category(id, name, description)
fun Category.toEntity() = CategoryEntity(id, name, description)

fun AssetEntity.toDomain() = Asset(id, name, description, categoryId, dailyRate, hourlyRate, depositAmount)
fun Asset.toEntity() = AssetEntity(id, name, description, categoryId, dailyRate, hourlyRate, depositAmount)

fun InventoryItemEntity.toDomain() = InventoryItem(
    id = id,
    assetId = assetId,
    serialNumber = serialNumber,
    qrCodeToken = qrCodeToken,
    status = ItemStatus.valueOf(status),
    photoUrl = photoUrl,
    lastMaintenanceDate = lastMaintenanceDate,
    nextMaintenanceDate = nextMaintenanceDate
)
fun InventoryItem.toEntity() = InventoryItemEntity(
    id = id,
    assetId = assetId,
    serialNumber = serialNumber,
    qrCodeToken = qrCodeToken,
    status = status.name,
    photoUrl = photoUrl,
    lastMaintenanceDate = lastMaintenanceDate,
    nextMaintenanceDate = nextMaintenanceDate
)

// -- Adapters --

@Repository
class CategoryRepositoryAdapter(
    private val springDataCategoryRepository: SpringDataCategoryRepository
) : CategoryRepository {
    override fun save(category: Category): Category =
        springDataCategoryRepository.save(category.toEntity()).toDomain()

    override fun findById(id: UUID): Category? =
        springDataCategoryRepository.findByIdOrNull(id)?.toDomain()

    override fun findAll(): List<Category> =
        springDataCategoryRepository.findAll().map { it.toDomain() }
}

@Repository
class AssetRepositoryAdapter(
    private val springDataAssetRepository: SpringDataAssetRepository
) : AssetRepository {
    override fun save(asset: Asset): Asset =
        springDataAssetRepository.save(asset.toEntity()).toDomain()

    override fun findById(id: UUID): Asset? =
        springDataAssetRepository.findByIdOrNull(id)?.toDomain()

    override fun findAll(): List<Asset> =
        springDataAssetRepository.findAll().map { it.toDomain() }

    override fun deleteById(id: UUID) =
        springDataAssetRepository.deleteById(id)
}

@Repository
class InventoryItemRepositoryAdapter(
    private val springDataInventoryItemRepository: SpringDataInventoryItemRepository
) : InventoryItemRepository {
    override fun save(item: InventoryItem): InventoryItem =
        springDataInventoryItemRepository.save(item.toEntity()).toDomain()

    override fun findById(id: UUID): InventoryItem? =
        springDataInventoryItemRepository.findByIdOrNull(id)?.toDomain()

    override fun findByAssetId(assetId: UUID): List<InventoryItem> =
        springDataInventoryItemRepository.findByAssetId(assetId).map { it.toDomain() }

    override fun findByQrCodeToken(token: String): InventoryItem? =
        springDataInventoryItemRepository.findByQrCodeToken(token)?.toDomain()

    override fun deleteById(id: UUID) =
        springDataInventoryItemRepository.deleteById(id)
}
