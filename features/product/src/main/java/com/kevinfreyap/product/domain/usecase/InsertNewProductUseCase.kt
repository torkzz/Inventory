package com.kevinfreyap.product.domain.usecase

import com.kevinfreyap.domain.Result
import com.kevinfreyap.domain.error.SessionError
import com.kevinfreyap.domain.model.GroupId
import com.kevinfreyap.domain.util.errorOrNull
import com.kevinfreyap.domain.util.getOrNull
import com.kevinfreyap.product.domain.model.BatchId
import com.kevinfreyap.product.domain.model.Product
import com.kevinfreyap.product.domain.model.ProductId
import com.kevinfreyap.product.domain.model.StockBatch
import com.kevinfreyap.product.domain.model.error.ProductFormError
import com.kevinfreyap.domain.manager.IImageManager
import com.kevinfreyap.domain.session.IActiveGroupSession
import com.kevinfreyap.domain.validation.ValidateImageUseCase
import com.kevinfreyap.domain.manager.ISyncManager
import com.kevinfreyap.product.domain.repository.IProductRepository
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject

class InsertNewProductUseCase @Inject constructor(
    private val repository: IProductRepository,
    private val imageManager: IImageManager,
    private val syncManager: ISyncManager,
    private val activeGroupSession: IActiveGroupSession,
    private val validateProductName: ValidateProductNameUseCase,
    private val validateProductCategory: ValidateProductCategoryUseCase,
    private val validateProductDescription: ValidateProductDescriptionUseCase,
    private val validateProductBarcode: ValidateProductBarcodeUseCase,
    private val validateProductSku: ValidateProductSkuUseCase,
    private val validateProductImage: ValidateImageUseCase,
    private val validateProductMinimumQuantity: ValidateProductMinimumQuantityUseCase,
    private val validateBatchQuantity: ValidateBatchQuantityUseCase,
    private val validateBatchLocation: ValidateBatchLocationUseCase,
    private val validateBatchPrice: ValidateBatchPriceUseCase,
    private val validateBatchExpiration: ValidateBatchExpirationUseCase,
    private val validateBatchSupplier: ValidateBatchSupplierUseCase,
    private val createOrGetCategory: CreateOrGetCategoryUseCase,
    private val createOrGetLocation: CreateOrGetLocationUseCase
) {
    suspend operator fun invoke(
        productName: String,
        productCategoryName: String,
        productDescription: String?,
        productBarcode: String?,
        productBarcodeFormat: String?,
        productSku: String?,
        productImageUriString: String?,
        productMinQuantity: String,
        isMinQuantityConfirmed: Boolean,
        addInitialStock: Boolean,
        batchQuantity: String,
        isQuantityConfirmed: Boolean,
        batchLocation: String,
        batchPrice: String,
        isPriceConfirmed: Boolean,
        batchExpiration: Long?,
        batchSupplier: String?,
        brand: String? = null,
        unit: String = "pcs",
        purchasePrice: Double = 0.0,
        sellingPrice: Double = 0.0,
        quantity: Int = 0

    ): Result<Unit, ProductFormError> {
        val session = activeGroupSession.currentGroupId.first()
            ?: return Result.Error(
                ProductFormError(sessionError = SessionError.MISSING_GROUP)
            )

        val groupId = GroupId(session)

        val nameResult = validateProductName(productName)
        val categoryResult = validateProductCategory(productCategoryName)
        val descriptionResult = validateProductDescription(productDescription)
        val barcodeResult = validateProductBarcode(productBarcode, groupId)
        val skuResult = validateProductSku(productSku, groupId)
        val imageResult = validateProductImage(productImageUriString)
        val minQuantityResult = validateProductMinimumQuantity(productMinQuantity, isMinQuantityConfirmed)

        val quantityResult = if (addInitialStock) {
            validateBatchQuantity(batchQuantity, isQuantityConfirmed)
        } else {
            Result.Success(0)
        }

        val locationResult = if (addInitialStock) {
            validateBatchLocation(batchLocation)
        } else {
            null
        }

        val priceResult = if (addInitialStock) {
            validateBatchPrice(batchPrice, isPriceConfirmed)
        } else {
            Result.Success(0.0)
        }

        val expirationResult = if (addInitialStock) {
            validateBatchExpiration(batchExpiration)
        } else {
            Result.Success(null)
        }

        val supplierResult = if (addInitialStock) {
            validateBatchSupplier(batchSupplier)
        } else {
            Result.Success(null)
        }

        if (
            nameResult is Result.Error ||
            categoryResult is Result.Error ||
            descriptionResult is Result.Error ||
            barcodeResult is Result.Error ||
            skuResult is Result.Error ||
            imageResult is Result.Error ||
            minQuantityResult is Result.Error ||
            quantityResult is Result.Error ||
            locationResult is Result.Error ||
            priceResult is Result.Error ||
            expirationResult is Result.Error ||
            supplierResult is Result.Error
        ) {
            return Result.Error(
                ProductFormError(
                    nameError = nameResult.errorOrNull(),
                    categoryError = categoryResult.errorOrNull(),
                    descriptionError = descriptionResult.errorOrNull(),
                    barcodeError = barcodeResult.errorOrNull(),
                    skuError = skuResult.errorOrNull(),
                    imageError = imageResult.errorOrNull(),
                    minQuantityError = minQuantityResult.errorOrNull(),
                    quantityError = quantityResult.errorOrNull(),
                    locationError = locationResult?.errorOrNull(),
                    priceError = priceResult.errorOrNull(),
                    expirationError = expirationResult.errorOrNull(),
                    supplierError = supplierResult.errorOrNull()
                )
            )
        }

        val validCategoryName = categoryResult.getOrNull() ?: ""
        val categoryDomain = if (validCategoryName.isNotBlank()) {
            createOrGetCategory(validCategoryName, groupId)
        } else null

        val newProductId = ProductId("product-${UUID.randomUUID()}")

        val validBatchQuantity = quantityResult.getOrNull() ?: 0
        val validPrice = priceResult.getOrNull() ?: 0.0

        val initialBatch = if (addInitialStock && validBatchQuantity > 0) {
            val validLocation = locationResult!!.getOrNull()!!
            val locationDomain = createOrGetLocation(validLocation, groupId)

            listOf(
                StockBatch(
                    batchId = BatchId("batch-${UUID.randomUUID()}"),
                    productId = newProductId,
                    groupId = groupId,
                    location = locationDomain,
                    quantity = validBatchQuantity,
                    price = if (purchasePrice > 0.0) purchasePrice else validPrice,
                    expirationDate = expirationResult.getOrNull(),
                    supplier = supplierResult.getOrNull(),
                    createdAt = System.currentTimeMillis(),
                    lastUpdated = System.currentTimeMillis()
                )
            )
        } else {
            emptyList()
        }

        val currentUri = imageResult.getOrNull()

        val permanentPath = if (currentUri != null && currentUri.startsWith("content://")) {
            imageManager.saveImageToInternalStorage(currentUri)
        } else {
            currentUri
        }

        val finalQty = if (initialBatch.isNotEmpty()) {
            validBatchQuantity
        } else {
            quantity
        }

        repository.insertProduct(
            Product(
                productId = newProductId,
                category = categoryDomain,
                groupId = groupId,
                name = nameResult.getOrNull()!!,
                brand = brand?.trim()?.ifEmpty { null },
                description = descriptionResult.getOrNull(),
                barcode = barcodeResult.getOrNull(),
                barcodeFormat = productBarcodeFormat,
                sku = skuResult.getOrNull(),
                localImagePath = permanentPath,
                remoteImageUrl = null,
                unit = unit.ifBlank { "pcs" },
                purchasePrice = if (purchasePrice > 0.0) purchasePrice else validPrice,
                sellingPrice = sellingPrice,
                quantity = finalQty,
                minimumQuantity = minQuantityResult.getOrNull() ?: 0,
                batches = initialBatch,
                createdAt = System.currentTimeMillis(),
                lastUpdated = System.currentTimeMillis()
            )
        )

        syncManager.triggerSync()
        return Result.Success(Unit)
    }
}
