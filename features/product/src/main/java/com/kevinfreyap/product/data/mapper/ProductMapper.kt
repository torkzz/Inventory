package com.kevinfreyap.product.data.mapper

import com.kevinfreyap.database.entity.ProductEntity
import com.kevinfreyap.database.entity.relation.ProductWithDetails
import com.kevinfreyap.database.model.SyncState
import com.kevinfreyap.domain.model.GroupId
import com.kevinfreyap.product.data.network.dto.sync.SyncProductDto
import com.kevinfreyap.product.domain.model.Product
import com.kevinfreyap.product.domain.model.ProductId

fun ProductWithDetails.toDomain(): Product {
    return Product(
        productId = ProductId(this.product.productId),
        groupId = GroupId(this.product.groupId),
        category = this.category?.toDomain(),
        name = this.product.name,
        brand = this.product.brand,
        description = this.product.description,
        barcode = this.product.barcode,
        barcodeFormat = this.product.barcodeFormat,
        sku = this.product.sku,
        localImagePath = this.product.localImagePath,
        remoteImageUrl = this.product.remoteImageUrl,
        unit = this.product.unit,
        purchasePrice = this.product.purchasePrice,
        sellingPrice = this.product.sellingPrice,
        quantity = this.product.quantity,
        minimumQuantity = this.product.minimumQuantity,
        batches = this.batches.toDomain(),
        createdAt = this.product.createdAt,
        lastUpdated = this.product.lastUpdated
    )
}

fun Product.toEntity(syncState: SyncState): ProductEntity {
    return ProductEntity(
        productId = this.productId.value,
        groupId = this.groupId.value,
        categoryId = this.category?.categoryId?.value,
        name = this.name,
        brand = this.brand,
        description = this.description,
        barcode = if (this.barcode.isNullOrBlank()) null else this.barcode.trim(),
        barcodeFormat = this.barcodeFormat,
        sku = if (this.sku.isNullOrBlank()) null else this.sku.trim(),
        localImagePath = this.localImagePath,
        remoteImageUrl = this.remoteImageUrl,
        unit = this.unit.ifBlank { "pcs" },
        purchasePrice = this.purchasePrice,
        sellingPrice = this.sellingPrice,
        quantity = this.quantity,
        minimumQuantity = this.minimumQuantity,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
        syncState = syncState
    )
}

fun List<ProductWithDetails>.toDomainList(): List<Product> {
    return this.map { it.toDomain() }
}

// Sync
fun SyncProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        productId = this.productId,
        categoryId = this.categoryId,
        groupId = this.groupId,
        name = this.name,
        brand = this.brand,
        description = this.description,
        barcode = if (this.barcode.isNullOrBlank()) null else this.barcode.trim(),
        barcodeFormat = this.barcodeFormat,
        sku = if (this.sku.isNullOrBlank()) null else this.sku.trim(),
        localImagePath = null,
        remoteImageUrl = this.imageUri,
        unit = this.unit.ifBlank { "pcs" },
        purchasePrice = this.purchasePrice,
        sellingPrice = this.sellingPrice,
        quantity = this.quantity,
        minimumQuantity = this.minimumQuantity,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated,
        syncState = SyncState.SYNCED
    )
}

fun ProductEntity.toRequest(): SyncProductDto {
    return SyncProductDto(
        productId = this.productId,
        categoryId = this.categoryId,
        groupId = this.groupId,
        name = this.name,
        brand = this.brand,
        description = this.description,
        barcode = this.barcode,
        barcodeFormat = this.barcodeFormat,
        sku = this.sku,
        imageUri = this.remoteImageUrl,
        unit = this.unit,
        purchasePrice = this.purchasePrice,
        sellingPrice = this.sellingPrice,
        quantity = this.quantity,
        minimumQuantity = this.minimumQuantity,
        createdAt = this.createdAt,
        lastUpdated = this.lastUpdated
    )
}
