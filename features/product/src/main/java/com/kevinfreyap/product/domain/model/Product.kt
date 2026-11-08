package com.kevinfreyap.product.domain.model

import com.kevinfreyap.domain.model.GroupId

@JvmInline
value class ProductId(val value: String)

data class Product(
    val productId: ProductId,
    val groupId: GroupId,
    val category: Category? = null,
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
    val batches: List<StockBatch> = emptyList(),
    val createdAt: Long,
    val lastUpdated: Long
) {
    val totalQuantity: Int
        get() = if (batches.isNotEmpty()) batches.sumOf { it.quantity } else quantity

    val minBatchCost: Double?
        get() = if (batches.isNotEmpty()) {
            batches.map { it.price }.filter { it > 0.0 }.minOrNull()
        } else if (purchasePrice > 0.0) purchasePrice else null

    val maxBatchCost: Double?
        get() {
            if (batches.isNotEmpty()) {
                val validPrices = batches
                    .map { it.price }
                    .filter { it > 0.0 }
                val min = validPrices.minOrNull()
                val max = validPrices.maxOrNull()
                return if (min == max) null else max
            }
            return if (sellingPrice > 0.0 && purchasePrice > 0.0 && sellingPrice != purchasePrice) sellingPrice else null
        }

    val nearestExpiringBatch: Long?
        get() = batches
            .mapNotNull { it.expirationDate }
            .minOrNull()

    val productTotalValue: Double?
        get() {
            val total = if (batches.isNotEmpty()) {
                batches.sumOf { batch ->
                    val batchItemCost = batch.price
                    batch.quantity * batchItemCost
                }
            } else {
                quantity * purchasePrice
            }

            return if (total > 0.0) total else null
        }
}
