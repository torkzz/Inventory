package com.kevinfreyap.database.entity.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.kevinfreyap.database.entity.CategoryEntity
import com.kevinfreyap.database.entity.ProductEntity
import com.kevinfreyap.database.entity.StockBatchEntity

data class ProductWithDetails(
    @Embedded
    val product: ProductEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId",
    )
    val category: CategoryEntity? = null,

    @Relation(
        entity = StockBatchEntity::class,
        parentColumn = "productId",
        entityColumn = "productId",
    )
    val batches: List<BatchWithLocation> = emptyList(),
)
