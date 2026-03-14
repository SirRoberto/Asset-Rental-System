package com.rental.asset.domain.model

import java.util.UUID

data class Category(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String? = null
)
