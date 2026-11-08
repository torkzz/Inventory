package com.kevinfreyap.product.presentation.mapper

import com.kevinfreyap.product.domain.model.Product
import com.kevinfreyap.product.presentation.model.ProductListItemUi
import com.kevinfreyap.product.presentation.util.StockLevelCalculator.calculateStockLevel
import com.kevinfreyap.product.presentation.util.resolveDisplayedImage

fun Product.toUiModel(): ProductListItemUi {
    return ProductListItemUi(
        id = this.productId.value,
        name = this.name,
        quantity = this.totalQuantity,
        stockLevel = calculateStockLevel(qty = this.totalQuantity, minQty = this.minimumQuantity),
        category = this.category?.name ?: "General",
        imageUri = this.resolveDisplayedImage(),
        sku = this.sku
    )
}

fun List<Product>.toUiModel(): List<ProductListItemUi> {
    return this.map { product ->
        product.toUiModel()
    }
}