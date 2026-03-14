package com.rental.asset.domain.exception

abstract class DomainException(message: String) : RuntimeException(message)

class InvalidStatusTransitionException(
    val from: String,
    val to: String
) : DomainException("Cannot transition from status $from to $to.")

class MaintenanceRequiredException(
    val itemSerialNumber: String
) : DomainException("Item $itemSerialNumber cannot be rented because it requires maintenance.")

class AssetNotFoundException(val id: String) : DomainException("Asset with ID $id not found.")
class InventoryItemNotFoundException(val id: String) : DomainException("Inventory item with ID $id not found.")
