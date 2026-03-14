package com.rental.asset.domain.model

import com.rental.asset.domain.exception.InvalidStatusTransitionException
import com.rental.asset.domain.exception.MaintenanceRequiredException
import java.time.LocalDate
import java.util.UUID

data class InventoryItem(
    val id: UUID = UUID.randomUUID(),
    val assetId: UUID,
    val serialNumber: String,
    val qrCodeToken: String,
    var status: ItemStatus = ItemStatus.AVAILABLE,
    val photoUrl: String? = null,
    val lastMaintenanceDate: LocalDate? = null,
    val nextMaintenanceDate: LocalDate? = null
) {
    fun changeStatus(newStatus: ItemStatus, currentDate: LocalDate = LocalDate.now()) {
        if (this.status == newStatus) return

        // Walidacja logiki przejazdu maszyny stanów
        when (newStatus) {
            ItemStatus.RENTED -> {
                if (this.status != ItemStatus.AVAILABLE) {
                    throw InvalidStatusTransitionException(this.status.name, newStatus.name)
                }
                if (nextMaintenanceDate != null && nextMaintenanceDate.isBefore(currentDate)) {
                    throw MaintenanceRequiredException(this.serialNumber)
                }
            }
            ItemStatus.MAINTENANCE -> {
                if (this.status != ItemStatus.AVAILABLE && this.status != ItemStatus.RENTED) {
                    throw InvalidStatusTransitionException(this.status.name, newStatus.name)
                }
            }
            ItemStatus.AVAILABLE -> {
                if (this.status != ItemStatus.RENTED && this.status != ItemStatus.MAINTENANCE) {
                    throw InvalidStatusTransitionException(this.status.name, newStatus.name)
                }
            }
            ItemStatus.RETIRED -> {
                if (this.status == ItemStatus.RENTED) {
                    throw InvalidStatusTransitionException(this.status.name, newStatus.name)
                }
            }
        }
        this.status = newStatus
    }
}
