package com.rental.asset.domain.repository

import com.rental.asset.domain.model.Category
import java.util.UUID

interface CategoryRepository {
    fun save(category: Category): Category
    fun findById(id: UUID): Category?
    fun findAll(): List<Category>
}
