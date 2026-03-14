package com.rental.asset.domain.repository

import com.rental.asset.domain.model.Asset
import java.util.UUID

interface AssetRepository {
    fun save(asset: Asset): Asset
    fun findById(id: UUID): Asset?
    fun findAll(): List<Asset>
    fun deleteById(id: UUID)
}
