package com.kevinfreyap.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kevinfreyap.database.model.SyncState

@Entity (
    tableName = "product",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = GroupEntity::class,
            parentColumns = ["groupId"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["groupId", "productId"], unique = true),
        Index(value = ["groupId", "categoryId"]),
        Index(value = ["sku"], unique = true),
        Index(value = ["barcode"], unique = true)
    ]
)
data class ProductEntity (
    @PrimaryKey (autoGenerate = false)
    val productId: String,
    val categoryId: String? = null,
    val groupId: String,
    val name: String,
    val brand: String? = null,
    val description: String? = null,
    val barcode: String? = null,
    val barcodeFormat: String? = null,
    val sku: String? = null,
    val localImagePath: String? = null,
    val remoteImageUrl: String? = null,
    val unit: String = "pcs",
    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val quantity: Int = 0,
    val minimumQuantity: Int = 0,
    val createdAt: Long,
    val lastUpdated: Long,
    val syncState: SyncState = SyncState.SYNCED
)
